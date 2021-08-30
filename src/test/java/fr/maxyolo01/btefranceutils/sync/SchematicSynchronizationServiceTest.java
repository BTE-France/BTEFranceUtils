package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import fr.maxyolo01.btefranceutils.events.worldedit.SchematicSavedEvent;
import fr.maxyolo01.btefranceutils.test.discord.DummyTextChannel;
import fr.maxyolo01.btefranceutils.test.worldedit.DummyLocalSession;
import fr.maxyolo01.btefranceutils.test.worldedit.DummyPlayer;
import github.scarsz.discordsrv.dependencies.jda.api.entities.EmbedType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import net.buildtheearth.terraplusplus.projection.EquirectangularProjection;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraplusplus.projection.transform.ScaleProjectionTransform;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.TempDirectory;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SchematicSynchronizationServiceTest {

    private File schematicDirectory;
    private File wedDirectory;
    private DummyTextChannel channel;

    private final ConcurrentHashMap<UUID, String> discordIds = new ConcurrentHashMap<>();
    private final GeographicProjection projection;
    private final NominatimClient nominatim;

    private SchematicSynchronizationService service;

    public SchematicSynchronizationServiceTest() {
        this.projection = new ScaleProjectionTransform(new EquirectangularProjection(), 100000, 100000);
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setUserAgent("Bte France Minecraft plugin JUnit test");
        this.nominatim =  new JsonNominatimClient("https://nominatim.openstreetmap.org/", builder.build(), "smyler@mail.com");
    }

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
                "123",
                this.channel,
                TestMessageEmbed::new,
                this.discordIds::get,
                this.projection,
                this.nominatim,
                Logger.getLogger("Schematic synchronization test"));
        this.service.setup();
        this.service.start();
    }

    @AfterAll
    public void shutdownService() throws InterruptedException {
        this.service.stop();
    }

    @Test
    @Timeout(value = 10)
    public void testSchematicLinks() throws IOException, InterruptedException, OutOfProjectionBoundsException {
        SchematicSavedEvent event = this.createFakeSaveEvent("DummyPlayer", "DummyPlayer#4567", "schem", 2.35220, 48.85660);
        this.service.onSchematicSaved(event);
        TestMessageEmbed embed = (TestMessageEmbed) this.channel.waitForNextEmbed();
        assertTrue(this.wedDirectory.toPath().resolve("41d82e57322fcb2adc80111ccfc50bc7e6a82d32eb9740bb4dd758248ff3ae52/schem.schematic").toFile().exists());
        embed.assertUrl("https://example.com/schematics/41d82e57322fcb2adc80111ccfc50bc7e6a82d32eb9740bb4dd758248ff3ae52/schem.schematic");
        embed.assertMcPlayer("DummyPlayer");
        embed.assertDiscordId("DummyPlayer#4567");
        embed.assertAddressContains("Paris");
        embed.assertFileSize(2048);
    }

    private SchematicSavedEvent createFakeSaveEvent(String playerName, String discordId, String schemName, double longitude, double latitude) throws IOException, OutOfProjectionBoundsException {
        UUID uuid = UUID.randomUUID();
        DummyPlayer player = new DummyPlayer(uuid, playerName);
        if (discordId != null) {
            this.discordIds.put(uuid, discordId);
        }
        double[] xz = this.projection.fromGeo(longitude, latitude);
        double x = xz[0], z = xz[1];
        Region region = new CuboidRegion(new Vector(x - 5, -5, z - 5), new Vector(x + 5, 16, z + 5));
        File schematic = this.createFakeSchematic(schemName, 2048);
        return new SchematicSavedEvent(player, schematic, new DummyLocalSession(region));
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

    private static class TestMessageEmbed extends MessageEmbed {

        private final URL url;
        private final String mcPlayerName;
        private final String discordUserName;
        private final Address address;
        private final long fileSize;

        public TestMessageEmbed(@Nullable URL schematicUrl,
                                @Nullable String mcPlayerName,
                                @Nullable String discordUserName,
                                @Nullable Address address,
                                long fileSize) {
            super("", "", "", EmbedType.RICH, OffsetDateTime.now(), 0, null, null, null, null, null, null, Collections.emptyList());
            this.url = schematicUrl;
            this.mcPlayerName = mcPlayerName;
            this.discordUserName = discordUserName;
            this.address = address;
            this.fileSize = fileSize;
        }

        public void assertUrl(String url) {
            assertNotNull(this.url);
            assertEquals(url, this.url.toString());
        }

        public void assertNoUrl() {
            assertNull(this.url);
        }

        public void assertMcPlayer(String playerName) {
            assertNotNull(this.mcPlayerName);
            assertEquals(playerName, this.mcPlayerName);
        }

        public void assertNoMcPlayer() {
            assertNull(this.mcPlayerName);
        }

        public void assertDiscordId(String name) {
            assertNotNull(this.discordUserName);
            assertEquals(name, this.discordUserName);
        }

        public void assertNoDiscordId() {
            assertNull(this.discordUserName);
        }

        public void assertAddressContains(String str) {
            assertNotNull(this.address);
            assertTrue(this.address.getDisplayName().contains(str));
        }

        public void assertNoAddress() {
            assertNull(this.address);
        }

        public void assertFileSize(long size) {
            assertEquals(size, this.fileSize);
        }

        public void assertNoFileSize() {
            assertEquals(-1, this.fileSize);
        }

    }

}
