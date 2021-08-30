package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import fr.maxyolo01.btefranceutils.BteFranceUtils;
import fr.maxyolo01.btefranceutils.events.worldedit.SchematicSavedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;

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
            @Nonnull NominatimClient nominatim) {
        this.schematicDirectory = schematicDirectory;
        this.webDirectory = webDirectory;
        this.urlRoot = urlRoot;
        this.salt = salt;
        this.channel = channel;
        this.messageProvider = messageProvider;
        this.discordIdResolver = discordIdResolver;
        this.projection = projection;
        this.nominatim = nominatim;
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
        this.executor = Executors.newCachedThreadPool(SchematicSynchronizationService::makeThread);
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
            BteFranceUtils.instance().getLogger().severe("Schematic service working pool took more than one minute to stop, something might be wrong!");
        }
        this.running = false;
    }

    /**
     * @return whether or not this service is running
     */
    public boolean isRunning() {
        return this.running;
    }

    @Subscribe
    public void onSchematicSaved(SchematicSavedEvent event) {
        Vector selectionCenter = this.getSelectionCenter(event.session());
        CompletableFuture<URL> urlFuture = CompletableFuture.supplyAsync(() -> this.linkSchematicFile(event.file()), this.executor);
        CompletableFuture<Address> cityFuture = CompletableFuture.supplyAsync(() -> this.getAddress(selectionCenter), this.executor);
        String mcName = event.player().getName();
        String dcName = this.discordIdResolver.apply(event.player().getUniqueId());
        long size = event.file().length();
        urlFuture.thenAcceptBothAsync(cityFuture, (url, address) -> {
            MessageEmbed embed = this.messageProvider.provide(url, mcName, dcName, address, size);
            this.channel.sendMessage(embed);
        }, this.executor);
    }

    private URL linkSchematicFile(File file) {
        String prefix  = this.computeFilePrefix(file);
        File newDir = this.webDirectory.resolve(prefix).toFile();
        URL url;
        try {
            String urlRoot = this.urlRoot + (this.urlRoot.endsWith("/") ? "": "/");
            url = new URL(urlRoot + newDir.getName() + "/" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString()));
        } catch (MalformedURLException e) {
            BteFranceUtils.instance().getLogger().severe("Could not get URL for file " + newDir + "/" + file.getName());
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            BteFranceUtils.instance().getLogger().severe("How on Earth is UTF-8 not supported ??");
            e.printStackTrace();
            return null;
        }
        if (!newDir.exists() && !newDir.mkdir()) {
            BteFranceUtils.instance().getLogger().severe("Failed to create a sub directory in the schematic web directory!");
            return null;
        } else if (!newDir.isDirectory()){
            BteFranceUtils.instance().getLogger().severe("Sub directory in the schematic web directory is a file!");
            return null;
        } else if (!newDir.canWrite()){
            BteFranceUtils.instance().getLogger().severe("Cannot write in schematic web sub directory!");
            return null;
        } else {
            Path newPath = newDir.toPath().resolve(file.getName());
            try {
                Files.createSymbolicLink(newPath, file.toPath());
                return url;
            } catch (IOException e) {
                BteFranceUtils.instance().getLogger().severe("Failed to create schematic symlink!");
                e.printStackTrace();
                return null;
            }
        }
    }

    private static Thread makeThread(Runnable run) {
        Thread thread = new Thread(run);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("BTE-FR Schematic worker");
        return thread;
    }

    private String computeFilePrefix(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return hexString(md.digest((this.salt + file.getName()).getBytes(StandardCharsets.UTF_8)));
        } catch(NoSuchAlgorithmException e) {
            BteFranceUtils.instance().getLogger().severe("How on Earth is SHA-256 not supported??");
            e.printStackTrace();
            return "";
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
                return this.nominatim.getAddress(lola[0], lola[1], 20);
            } catch (OutOfProjectionBoundsException silenced) {
            } catch (Exception e) {
                BteFranceUtils.instance().getLogger().warning("Failed to get schematic address");
                e.printStackTrace();
            }
        }
        return null;
    }

}