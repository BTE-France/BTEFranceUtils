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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SchematicSynchronizationServiceTest {

    @Test
    @ExtendWith(TempDirectory.class)
    public void testSchematicLinks(@TempDir Path dir) throws IOException, InterruptedException {
        File schemDir = dir.resolve("schematics").toFile();
        assertTrue(schemDir.mkdir());
        File webDir = dir.resolve("web").toFile();
        assertTrue(webDir.mkdir());
        DummyTextChannel channel = new DummyTextChannel();
        DummyPlayer player = new DummyPlayer();
        SchematicSynchronizationService service = new SchematicSynchronizationService(
                schemDir.toPath(),
                webDir.toPath(),
                "https://example.com/schematics",
                channel,
                this.projection(),
                this.nominatimClient());
        service.setSalt("123");
        File schematic = schemDir.toPath().resolve("schem.schematic").toFile();
        assertTrue(schematic.createNewFile());
        this.write2k(schematic);
        service.setup();
        service.start();
        Region region = new CuboidRegion(new Vector(4885660, -5, 235220), new Vector(4885670, 16, 235225));
        service.onSchematicSaved(new SchematicSavedEvent(player, schematic, new DummyLocalSession(region)));
        Thread.sleep(2000);
        assertTrue(webDir.toPath().resolve("41d82e57322fcb2adc80111ccfc50bc7e6a82d32eb9740bb4dd758248ff3ae52/schem.schematic").toFile().exists());
        assertEquals(1, channel.getEmbeds().size()) ;
        MessageEmbed embed = channel.getEmbeds().remove(0);
        String title = embed.getTitle();
        assertNotNull(title);
        String description = embed.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("Paris"));
        List<MessageEmbed.Field> fields = embed.getFields();
        assertEquals(2, fields.size());
        String valueUrl = fields.get(0).getValue();
        String valueSize = fields.get(1).getValue();
        assertNotNull(valueUrl);
        assertNotNull(valueSize);
        assertTrue(valueUrl.contains("https://example.com/schematics/41d82e57322fcb2adc80111ccfc50bc7e6a82d32eb9740bb4dd758248ff3ae52/schem.schematic"));
        assertTrue(valueSize.contains("2.0 kiO"));
        service.stop();
    }

    private void write2k(File f) {
        Random random = new Random();
        try(OutputStream stream = new FileOutputStream(f)) {
            for (int i = 0; i < 2048; i++) stream.write(random.nextInt());
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private GeographicProjection projection() {
        // BTE projection
        return new ScaleProjectionTransform(
                new EquirectangularProjection(),
                100000, 100000);
    }

    private JsonNominatimClient nominatimClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setUserAgent("Bte France Minecraft plugin JUnit test");
        return new JsonNominatimClient("https://nominatim.openstreetmap.org/", builder.build(), "smyler@mail.com");
    }

}
