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
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.maxyolo01.btefranceutils.util.formatting.Formatting.hexString;

/**
 * A service that synchronizes the world edit schematic directory with a discord channel through a directory exposed to a web server..
 *
 * @author SmylerMC
 */
public class SchematicSynchronizationService {

    private final Path schematicDirectory, webDirectory;
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

    private boolean setup, running;

    public SchematicSynchronizationService(
            @Nonnull Path schematicDirectory,
            @Nonnull Path webDirectory,
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
     * Prepares and checks the necessary resources for the service to run.
     *
     * @throws IOException if the necessary resources are not available
     */
    public void setup() throws IOException {
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
        this.setup = true;
    }

    /**
     * Starts the service.
     *
     * @throws IllegalStateException if the service hasn't been setup with {@link #setup()} or is already running
     */
    public void start() {
        if (!this.setup) {
            throw new IllegalStateException("Schematic service hasn't been setup!");
        }
        if (this.isRunning()) {
            throw new IllegalStateException("Already running");
        }
        WorldEdit.getInstance().getEventBus().register(this);
        this.running = true;
        this.logger.info("Schematic synchronization service is now running.");
    }

    /**
     * Stops this services and frees all held resources.
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
        this.setup = false;
        this.logger.info("Schematic synchronization has been stopped.");
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
                MessageEmbed embed = this.messageProvider.provide(url, mcName, dcName, address, size);
                this.channel.sendMessage(embed).queue();
                this.logger.fine("Sent Discord notification for schematic!");
            } catch (Exception e) {
                this.logger.severe("Failed to send Discord notification for schematic!");
                e.printStackTrace();
            }
        }, this.executor);
    }

    public void processExistingSchematics(CommandSender sender) {
        File[] inDirectory = this.schematicDirectory.toFile().listFiles();
        if (inDirectory == null) {
            throw new IllegalStateException("Cannot list schematic directory");
        }
        Set<File> files = Stream.of(inDirectory).filter(File::isFile).collect(Collectors.toSet());
        final int count = files.size();
        sender.sendMessage(ChatColor.BLUE + "Found " + count + " schematics, starting linking process...");
        AtomicInteger progress = new AtomicInteger(0);
        final Object lock = new Object();
        files.forEach(file -> CompletableFuture.runAsync(() -> {
            File subWebDir = this.getWebSubDirectory(file);
            if (subWebDir.exists() && subWebDir.isDirectory()) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE +
                        String.format("Schematic %s was already linked, ignored it (%s/%s)",
                                file.getName(),
                                progress.incrementAndGet(),
                                count));
            } else {
                URL url = this.linkSchematicFile(file, subWebDir);
                if (url == null) {
                    sender.sendMessage(ChatColor.RED +
                            String.format("There was a problem when linking schematic %s (%s/%s)",
                                    file.getName(),
                                    progress.incrementAndGet(),
                                    count));
                } else {
                    MessageEmbed embed = this.messageProvider.provide(url,  null, null, null, file.length());
                    synchronized (lock) {
                        this.channel.sendMessage(embed).queue();
                        try {
                            Thread.sleep(this.messageDelay);
                        } catch (InterruptedException ignored) {}
                        lock.notify();
                    }
                    sender.sendMessage(ChatColor.GREEN +
                            String.format("Schematic %s was successfully linked (%s/%s)",
                                    file.getName(),
                                    progress.incrementAndGet(),
                                    count));
                }
            }
        }, this.executor));
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
            Path newPath = webSubDir.toPath().resolve(file.getName());
            try {
                Files.createSymbolicLink(newPath, file.toPath());
                this.logger.fine(String.format("Linked schematic file %s at %s, resulting in url %s", file, newPath, url));
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
            String prefix = hexString(md.digest((this.salt + file.getName()).getBytes(StandardCharsets.UTF_8)));
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

}