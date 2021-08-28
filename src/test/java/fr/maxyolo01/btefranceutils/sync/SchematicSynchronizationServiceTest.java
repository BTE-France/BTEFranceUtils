package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.maxyolo01.btefranceutils.events.worldedit.SchematicSavedEvent;
import fr.maxyolo01.btefranceutils.test.discord.DummyTextChannel;
import fr.maxyolo01.btefranceutils.test.worldedit.DummyLocalSession;
import fr.maxyolo01.btefranceutils.test.worldedit.DummyPlayer;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import net.buildtheearth.terraplusplus.projection.EquirectangularProjection;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.transform.ScaleProjectionTransform;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SchematicSynchronizationServiceTest {

    private File schematicDirectory;
    private File wedDirectory;
    private DummyTextChannel channel;

    private SchematicSynchronizationService service;

    @BeforeAll
    public void initService(@TempDir Path tempDir) throws IOException {
        this.schematicDirectory = tempDir.resolve("schematics").toFile();
        assertTrue(this.schematicDirectory.mkdir());
        this.wedDirectory = tempDir.resolve("web").toFile();
        assertTrue(this.wedDirectory.mkdir());
        this.channel = new DummyTextChannel();
        this.service = new SchematicSynchronizationService(
                this.schematicDirectory.toPath(),
                this.wedDirectory.toPath(),
                "https://example.com/schematics",
                this.channel,
                this.projection(),
                this.nominatimClient());
        this.service.setSalt("123");
        this.service.setup();
        this.service.start();
    }

    @AfterAll
    public void shutdownService() throws InterruptedException {
        this.service.stop();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testSchematicLinks() throws IOException, InterruptedException {
        SchematicSavedEvent event = this.createFakeSaveEvent("DummyPlayer", "schem", 2.35220, 48.85660);
        this.service.onSchematicSaved(event);
        MessageEmbed embed = this.channel.waitForNextEmbed();
        assertTrue(this.wedDirectory.toPath().resolve("41d82e57322fcb2adc80111ccfc50bc7e6a82d32eb9740bb4dd758248ff3ae52/schem.schematic").toFile().exists());
        String title = embed.getTitle();
        assertNotNull(title);
        String description = embed.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("DummyPlayer"));
        assertTrue(description.contains("Paris"));
        List<MessageEmbed.Field> fields = embed.getFields();
        assertEquals(2, fields.size());
        String valueUrl = fields.get(0).getValue();
        String valueSize = fields.get(1).getValue();
        assertNotNull(valueUrl);
        assertNotNull(valueSize);
        assertTrue(valueUrl.contains("https://example.com/schematics/41d82e57322fcb2adc80111ccfc50bc7e6a82d32eb9740bb4dd758248ff3ae52/schem.schematic"));
        assertTrue(valueSize.contains("2.0 kiO"));
    }

    private GeographicProjection projection() {
        return new ScaleProjectionTransform(
                new EquirectangularProjection(),
                100000, 100000);
    }

    private JsonNominatimClient nominatimClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setUserAgent("Bte France Minecraft plugin JUnit test");
        return new JsonNominatimClient("https://nominatim.openstreetmap.org/", builder.build(), "smyler@mail.com");
    }

    private File createFakeSchematic(String name, long fileSize) throws IOException {
        File schematic = this.schematicDirectory.toPath().resolve(name + ".schematic").toFile();
        assertTrue(schematic.createNewFile());
        this.writeJunk(schematic, fileSize);
        return schematic;
    }

    private void writeJunk(File f, long junkSize) {
        Random random = new Random();
        try(OutputStream stream = new FileOutputStream(f)) {
            for (int i = 0; i < junkSize; i++) stream.write(random.nextInt());
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private SchematicSavedEvent createFakeSaveEvent(String playerName, String schemName, double longitude, double latitude) throws IOException {
        DummyPlayer player = new DummyPlayer(playerName);
        double x = longitude * 100000;
        double z = latitude * 100000;
        Region region = new CuboidRegion(new Vector(x - 5, -5, z - 5), new Vector(x + 5, 16, z + 5));
        File schematic = this.createFakeSchematic(schemName, 2048);
        return new SchematicSavedEvent(player, schematic, new DummyLocalSession(region));
    }

}
