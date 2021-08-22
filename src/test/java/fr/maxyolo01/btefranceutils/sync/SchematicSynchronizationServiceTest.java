package fr.maxyolo01.btefranceutils.sync;

import fr.maxyolo01.btefranceutils.events.worldedit.SchematicSavedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchematicSynchronizationServiceTest {

    @Test
    @ExtendWith(TempDirectory.class)
    public void testSchematicLinks(@TempDir Path dir) throws IOException, InterruptedException, NoSuchAlgorithmException {
        File schemDir = dir.resolve("schematics").toFile();
        schemDir.mkdir();
        File webDir = dir.resolve("web").toFile();
        webDir.mkdir();
        SchematicSynchronizationService service = new SchematicSynchronizationService(schemDir.toPath(), webDir.toPath());
        File schematic = schemDir.toPath().resolve("schem.schematic").toFile();
        assertTrue(schematic.createNewFile());
        service.setup();
        service.start();
        service.onSchematicSaved(new SchematicSavedEvent(null, schematic, null));
        Thread.sleep(100);
        assertTrue(webDir.toPath().resolve("634bb151-88d7-3499-b1d3-8fac053cd762/schem.schematic").toFile().exists());
        service.stop();
    }

}
