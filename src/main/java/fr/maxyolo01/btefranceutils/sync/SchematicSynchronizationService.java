package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import fr.maxyolo01.btefranceutils.BteFranceUtils;
import fr.maxyolo01.btefranceutils.events.worldedit.SchematicSavedEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * A service that synchronizes the world edit schematic directory with a discord channel.
 *
 * @author SmylerMC
 */
public class SchematicSynchronizationService {

    private final Path schematicDirectory, webDirectory;
    private ExecutorService executor;

    private boolean setup, running;

    public SchematicSynchronizationService(@Nonnull Path schematicDirectory, @Nonnull Path webDirectory) {
        this.schematicDirectory = schematicDirectory;
        this.webDirectory = webDirectory;
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
        this.executor.awaitTermination(1, TimeUnit.MINUTES);
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
        CompletableFuture<File> futureEvent = CompletableFuture.supplyAsync(() -> this.linkSchematicFile(event.file()));
    }

    private File linkSchematicFile(File file) {
        String prefix  = this.computeFilePrefix(file);
        File newDir = this.webDirectory.resolve(prefix).toFile();
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
                return newPath.toFile();
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
        //FIXME Vulnerable to an enumeration attack, we need to hash the file instead
        return UUID.nameUUIDFromBytes(file.getName().getBytes(StandardCharsets.UTF_8)).toString();
    }

}