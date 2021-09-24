package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.platform.SchematicSavedEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.maxyolo01.btefranceutils.util.formatting.Formatting.bytesToHexString;

/**
 * A service that synchronizes the world edit schematic directory with a discord channel through a directory exposed to a web server..
 *
 * @author SmylerMC
 */
public class SchematicSynchronizationService {

    private final Path schematicDirectory, webDirectory, symlinkRoot;
    private final String urlRoot;
    private final TextChannel channel;
    private final SchematicDiscordEmbedProvider messageProvider;
    private final Function<UUID, String> discordIdResolver;
    private final String salt;
    private final GeographicProjection projection;
    private final NominatimClient nominatim;
    private final Logger logger;
    private long messageDelay = 1000; // Time to wait between two messages when bulk sending, in ms

    private ExecutorService executor;

    private boolean running;

    private BulkUpdateTask bulkTask;

    public SchematicSynchronizationService(
            @Nonnull Path schematicDirectory,
            @Nonnull Path webDirectory,
            @Nonnull Path symlinkRoot,
            @Nonnull String urlRoot,
            @Nonnull String salt,
            @Nonnull TextChannel channel,
            @Nonnull SchematicDiscordEmbedProvider messageProvider,
            @Nonnull Function<UUID, String> discordIdResolver,
            @Nonnull GeographicProjection projection,
            @Nonnull NominatimClient nominatim,
            @Nonnull Logger logger) {
        this.schematicDirectory = schematicDirectory;
        this.webDirectory = webDirectory;
        this.symlinkRoot = symlinkRoot;
        this.urlRoot = urlRoot;
        this.salt = salt;
        this.channel = channel;
        this.messageProvider = messageProvider;
        this.discordIdResolver = discordIdResolver;
        this.projection = projection;
        this.nominatim = nominatim;
        this.logger = logger;
    }

    /**
     * Starts the service.
     *
     * @throws IOException if something is wrong with your IO setup
     */
    public void start() throws IOException {
        if (this.isRunning()) {
            throw new IllegalStateException("Already running");
        }
        File schemDir = this.schematicDirectory.toFile();
        File webDir = this.webDirectory.toFile();
        if (!schemDir.exists() && !schemDir.mkdirs()) {
            throw new IOException("Schematic directory does not exist and failed to create");
        } else if(!schemDir.isDirectory()) {
            throw new IOException("Schematic directory path is not a directory");
        } else if (!schemDir.canRead() || !schemDir.canWrite()) {
            throw new IOException("Missing required permission for the schematic directory: read and write are required");
        } else if (!webDir.exists()) {
            throw new IOException("Web directory did not exist.");
        } else if (!webDir.isDirectory()) {
            throw new IOException("Web directory path is not a directory");
        } else if (!webDir.canWrite()) {
            throw new IOException("Missing required write permission for the web directory");
        }
        this.executor = Executors.newFixedThreadPool(2, SchematicSynchronizationService::makeThread);
        WorldEdit.getInstance().getEventBus().register(this);
        this.running = true;
        this.logger.info("Schematic synchronization service is now running.");
    }

    /**
     * Stops this services and frees all held resources. Waits for tasks to finish.
     *
     * @throws InterruptedException if the thread was interrupted before all tasks finished
     * @throws IllegalStateException if the service hasn't been started with {@link #start()}
     */
    public void stop() throws InterruptedException {
        if (!this.isRunning()) {
            throw new IllegalStateException("Not running");
        }
        WorldEdit.getInstance().getEventBus().unregister(this);
        this.executor.shutdown();
        if (!this.executor.awaitTermination(1, TimeUnit.MINUTES)) {
            this.logger.severe("Schematic service working pool took more than one minute to stop, something might be wrong!");
        }
        this.running = false;
        this.logger.info("Schematic synchronization has been stopped.");
    }

    /**
     * Stops this services and frees all held resources. Cancels all tasks.
     *
     * @throws InterruptedException if the thread was interrupted before all tasks finished
     * @throws IllegalStateException if the service hasn't been started with {@link #start()}
     */
    public void terminate() throws InterruptedException {
        if (!this.isRunning()) {
            throw new IllegalStateException("Not running");
        }
        WorldEdit.getInstance().getEventBus().unregister(this);
        this.executor.shutdownNow();
        if (!this.executor.awaitTermination(1, TimeUnit.MINUTES)) {
            this.logger.severe("Schematic service working pool took more than one minute to terminate, something might be wrong!");
        }
        this.running = false;
        this.logger.info("Schematic synchronization has been terminated.");
    }

    /**
     * @return whether or not this service is running
     */
    public boolean isRunning() {
        return this.running;
    }

    @Subscribe
    public void onSchematicSaved(final SchematicSavedEvent event) {
        Vector selectionCenter = this.getSelectionCenter(event.session());
        File file = event.file();
        CompletableFuture<URL> urlFuture = CompletableFuture.supplyAsync(
                // Make a symlink of the file in a directory with an unpredictable name
                () -> this.linkSchematicFile(file, this.getWebSubDirectory(file)),
                this.executor);
        CompletableFuture<Address> cityFuture = CompletableFuture.supplyAsync(
                // Get the address of the center of the schematic region from Nominatim
                () -> this.getAddress(selectionCenter),
                this.executor);
        String mcName = event.player().getName();
        String dcName = this.discordIdResolver.apply(event.player().getUniqueId());
        long size = event.file().length();
        this.logger.fine(String.format("New schematic saved: %s by %s", event.file().getName(), mcName));
        urlFuture.thenAcceptBothAsync(cityFuture, (url, address) -> {
            // Send a notification on Discord
            try {
                SchematicDiscordEmbedProvider.SchematicEmbedData data = new SchematicDiscordEmbedProvider.SchematicEmbedData(url, event.file().getName(), mcName, dcName, address, size);
                MessageEmbed embed = this.messageProvider.provide(data);
                this.channel.sendMessage(embed).queue();
                this.logger.fine("Sent Discord notification for schematic!");
            } catch (Exception e) {
                this.logger.severe("Failed to send Discord notification for schematic!");
                e.printStackTrace();
            }
        }, this.executor);
    }

    public void startBulkUpdate(CommandSender sender) {
        if (bulkTask != null) throw new IllegalStateException("A bulk update task is already running.");
        File[] inDirectory = this.schematicDirectory.toFile().listFiles();
        if (inDirectory == null) {
            throw new IllegalStateException("Cannot list schematic directory");
        }
        Set<File> files = Stream.of(inDirectory).filter(File::isFile).collect(Collectors.toSet());
        this.bulkTask = new BulkUpdateTask(files.size());
        this.bulkTask.subscribeSender(sender);
        String message = ChatColor.BLUE + "Found " + this.bulkTask.totalFileCount + " schematics, starting linking process...";
        this.bulkTask.broadcast(message);
        if (!(sender instanceof ConsoleCommandSender)) {
            this.logger.info(message);
        }
        final Object lock = new Object();
        files.forEach(file -> this.bulkTask.addFuture(CompletableFuture.runAsync(() -> {
            File subWebDir = this.getWebSubDirectory(file);
            if (subWebDir.exists() && subWebDir.isDirectory()) {
                this.bulkTask.ignored(file);
            } else {
                URL url = this.linkSchematicFile(file, subWebDir);
                if (url == null) {
                    this.bulkTask.error(file);
                } else {
                    SchematicDiscordEmbedProvider.SchematicEmbedData data = new SchematicDiscordEmbedProvider.SchematicEmbedData(url,  file.getName(), null, null, null, file.length());
                    MessageEmbed embed = this.messageProvider.provide(data);
                    synchronized (lock) {
                        this.channel.sendMessage(embed).queue();
                        try {
                            Thread.sleep(this.messageDelay);
                        } catch (InterruptedException ignored) {}
                        lock.notify();
                    }
                    this.bulkTask.success(file);
                }
            }
        }, this.executor)));
    }

    public BulkUpdateTask getBulkUpdateTask() {
        return this.bulkTask;
    }

    private URL linkSchematicFile(File file, File webSubDir) {
        URL url;
        try {
            String urlRoot = this.urlRoot + (this.urlRoot.endsWith("/") ? "": "/");
            url = new URL(urlRoot + webSubDir.getName() + "/" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString()));
        } catch (MalformedURLException e) {
            this.logger.severe("Could not get URL for file " + webSubDir + "/" + file.getName());
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            this.logger.severe("How on Earth is UTF-8 not supported ??");
            e.printStackTrace();
            return null;
        }
        if (!webSubDir.exists() && !webSubDir.mkdir()) {
            this.logger.severe("Failed to create a sub directory in the schematic web directory!");
            return null;
        } else if (!webSubDir.isDirectory()){
            this.logger.severe("Sub directory in the schematic web directory is a file!");
            return null;
        } else if (!webSubDir.canWrite()){
            this.logger.severe("Cannot write in schematic web sub directory!");
            return null;
        } else {
            Path linkPath = webSubDir.toPath().resolve(file.getName());
            Path targetPath = this.symlinkRoot.resolve(file.getName());
            try {
                Files.createSymbolicLink(linkPath, targetPath);
                this.logger.fine(String.format("Linked schematic file %s at %s, resulting in url %s", file, targetPath, url));
                return url;
            } catch (IOException e) {
                this.logger.severe("Failed to create schematic symlink!");
                e.printStackTrace();
                return null;
            }
        }
    }

    private File getWebSubDirectory(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String prefix = bytesToHexString(md.digest((this.salt + file.getName()).getBytes(StandardCharsets.UTF_8)));
            return this.webDirectory.resolve(prefix).toFile();
        } catch(NoSuchAlgorithmException e) {
            this.logger.severe("How on Earth is SHA-256 not supported??");
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private Vector getSelectionCenter(LocalSession session) {
        try {
            return session.getSelection(session.getSelectionWorld()).getCenter();
        } catch (IncompleteRegionException e) {
            return null;
        }
    }

    private Address getAddress(Vector place) {
        if (place == null) {
            return null;
        } else {
            try {
                double[] lola = this.projection.toGeo(place.getX(), place.getZ());
                Address address = this.nominatim.getAddress(lola[0], lola[1], 20);
                this.logger.fine(String.format(Locale.US, "Successfully requested address for %s, %s, got %s", lola[1], lola[0], address.getDisplayName()));
                return address;
            } catch (OutOfProjectionBoundsException ignored) {
            } catch (Exception e) {
                this.logger.warning("Failed to get schematic address");
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setBulkMessageDelay(long delay) {
        this.messageDelay = delay;
    }

    private static Thread makeThread(Runnable run) {
        Thread thread = new Thread(run);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("BTE-FR Schematic worker");
        return thread;
    }

    public class BulkUpdateTask {

        private final int totalFileCount;
        private final AtomicInteger successes = new AtomicInteger();
        private final AtomicInteger errors = new AtomicInteger();
        private final AtomicInteger ignored = new AtomicInteger();
        private final Set<CommandSender> senders = new HashSet<>();
        private final List<CompletableFuture<?>> futures = new ArrayList<>();

        private BulkUpdateTask(int totalFileCount) {
            this.totalFileCount = totalFileCount;
        }

        public void success(File file) {
            this.successes.incrementAndGet();
            this.broadcast(ChatColor.GREEN +
                    "Schematic %s was successfully linked (%s/%s | %s%%)",
                    file.getName(),
                    this.progress(),
                    this.totalFileCount,
                    Math.floor(this.percentProgress()));
            this.checkDone();
        }

        public void ignored(File file) {
            this.ignored.incrementAndGet();
            this.broadcast(ChatColor.LIGHT_PURPLE +
                            "Schematic %s was already linked, ignored it (%s/%s | %s%%)",
                            file.getName(),
                            this.progress(),
                            this.totalFileCount,
                    Math.floor(this.percentProgress()));
            this.checkDone();
        }

        public void error(File file) {
            this.errors.incrementAndGet();
            this.broadcast(
                    ChatColor.RED + "There was a problem when linking schematic %s (%s/%s | %s%%)",
                    file.getName(),
                    this.progress(),
                    this.totalFileCount,
                    Math.floor(this.percentProgress()));
            this.checkDone();
        }

        public int progress() {
            return this.successes.get() + this.errors.get() + this.ignored.get();
        }

        public float percentProgress() {
            return this.progress() * 100f / this.totalFileCount;
        }

        public void checkDone() {
            if (this.progress() == this.totalFileCount) {
                this.finish(ChatColor.GREEN + "COMPLETED" + ChatColor.RESET);
            }
        }

        private void finish(String cause) {
            String message1 = String.format("Finished bulk update: %s ", cause);
            String message2 = this.getProgressMessage();
            this.broadcast(message1);
            this.broadcast(message2);
            boolean console = false;
            synchronized (this.senders) {
                for(CommandSender sender: this.senders) if (sender instanceof ConsoleCommandSender) {
                    console = true;
                    break;
                }
            }
            if (!console) {
                SchematicSynchronizationService.this.logger.info(message1);
                SchematicSynchronizationService.this.logger.info(message2);
            }
            synchronized (this.senders) {
                this.senders.clear();
            }
            synchronized (this.futures) {
                this.futures.clear();
            }
            SchematicSynchronizationService.this.bulkTask = null; // We are done here
        }

        public void sendProgressTo(CommandSender sender) {
            sender.sendMessage(this.getProgressMessage());
        }

        public void broadcast(String message, Object... args) {
            synchronized (this.senders) {
                for(CommandSender sender: this.senders) {
                    sender.sendMessage(String.format(message, args));
                }
            }
        }

        private String getProgressMessage() {
            final int successes = this.successes.get();
            final int errors = this.errors.get();
            final int ignored = this.ignored.get();
            final int remaining = this.totalFileCount - successes - ignored - errors;
            ChatColor successesColor;
            if (successes == this.totalFileCount) {
                successesColor = ChatColor.GREEN;
            } else if (successes == 0) {
                successesColor = ChatColor.RED;
            } else {
                successesColor = ChatColor.GOLD;
            }
            ChatColor errorColor = errors == 0 ? ChatColor.GREEN : ChatColor.RED;
            ChatColor ignoredColor = ChatColor.AQUA;
            ChatColor remainingColor = remaining == 0 ? ChatColor.GREEN : ChatColor.AQUA;
            return String.format(
                    "Success: %s%s%s Already linked: %s%s%s Errors: %s%s%s Not processed: %s%s%s",
                    successesColor, successes, ChatColor.RESET,
                    ignoredColor, ignored, ChatColor.RESET,
                    errorColor, errors, ChatColor.RESET,
                    remainingColor, remaining, ChatColor.RESET);
        }

        public void addFuture(CompletableFuture<?> future) {
            synchronized (this.futures) {
                this.futures.add(future);
            }
        }

        public void cancel() {
            synchronized (this.futures) {
                for (CompletableFuture<?> future: this.futures) {
                    future.cancel(true);
                    try {
                        future.get();
                    } catch (Exception ignored) {
                        // Wait for termination
                    }
                }
            }
            this.finish(ChatColor.RED + "ABORTED" + ChatColor.RESET);
        }

        public void subscribeSender(CommandSender sender) {
            synchronized(this.senders) {
                this.senders.add(sender);
            }
        }

        public void unsubscribeSender(CommandSender sender) {
            synchronized(this.senders) {
                this.senders.remove(sender);
            }
        }

    }

}