package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.entity.Player;
import fr.maxyolo01.btefranceutils.events.worldedit.SchematicSavedEvent;
import fr.maxyolo01.btefranceutils.test.discord.DummyTextChannel;
import fr.maxyolo01.btefranceutils.test.worldedit.DummyPlayer;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchematicSynchronizationServiceTest {

    @Test
    @ExtendWith(TempDirectory.class)
    public void testSchematicLinks(@TempDir Path dir) throws IOException, InterruptedException {
        File schemDir = dir.resolve("schematics").toFile();
        schemDir.mkdir();
        File webDir = dir.resolve("web").toFile();
        webDir.mkdir();
        DummyTextChannel channel = new DummyTextChannel();
        DummyPlayer player = new DummyPlayer();
        SchematicSynchronizationService service = new SchematicSynchronizationService(
                schemDir.toPath(),
                webDir.toPath(),
                "https://example.com/schematics",
                channel);
        File schematic = schemDir.toPath().resolve("schem.schematic").toFile();
        assertTrue(schematic.createNewFile());
        service.setup();
        service.start();
        service.onSchematicSaved(new SchematicSavedEvent(player, schematic, null));
        Thread.sleep(1000);
        assertTrue(webDir.toPath().resolve("634bb151-88d7-3499-b1d3-8fac053cd762/schem.schematic").toFile().exists());
        assertEquals(1, channel.getEmbeds().size()) ;
        MessageEmbed embed = channel.getEmbeds().remove(0);
        List<MessageEmbed.Field> fields = embed.getFields();
        assertEquals(2, fields.size());
        assertTrue(fields.get(0).getValue().contains("https://example.com/schematics/634bb151-88d7-3499-b1d3-8fac053cd762/schem.schematic"));
        service.stop();
    }

}
