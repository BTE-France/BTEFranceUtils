package fr.maxyolo01.btefranceutils.sync;

import fr.dudie.nominatim.model.Address;
import fr.maxyolo01.btefranceutils.test.nominatim.NominatimTestUtil;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchematicSyncConfigTest {

    private static final String MOCK_CONFIG_PATH = "test_schematics_sync_config.yaml";

    private final SchematicSyncConfig config;

    public SchematicSyncConfigTest() throws IOException {
        ConfigurationSection section;
        try (InputStream stream = this.getClass().getResourceAsStream(MOCK_CONFIG_PATH)){
            if (stream == null) throw new FileNotFoundException("Could not find mock config");
            section = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        }

        this.config = new SchematicSyncConfig(section, Logger.getLogger("Test logger"));
    }

    @Test
    public void testFullEmbed() throws IOException {
        Address testAddress = NominatimTestUtil.getDisplayNameAddress(
                "rue Dupont",
                "Beaucartier",
                "Perduville",
                "Perduville",
                "Belleregion",
                "185000",
                "Paysdesgens",
                "pdg",
                "1 rue Dupont, 185000 Perduville, Paysdesgens"
        );
        SchematicDiscordEmbedProvider.SchematicEmbedData data = new SchematicDiscordEmbedProvider.SchematicEmbedData(
                new URL("https://example.com/schematics/000/schematic.schematic"),
                "TestPlayer",
                "00000000",
                testAddress,
                2048
        );
        MessageEmbed embed = this.config.makeEmbed(data);
        assertEquals("Test title", embed.getTitle());
        assertEquals("Test description: https://example.com/schematics/000/schematic.schematic, <@00000000>, TestPlayer, <@00000000>, 2.0 kiO, 1 rue Dupont, 185000 Perduville, Paysdesgens", embed.getDescription());
        assertEquals("Perduville", embed.getFields().get(2).getValue());
        data = new SchematicDiscordEmbedProvider.SchematicEmbedData(
                new URL("https://example.com/schematics/000/schematic.schematic"),
                "TestPlayer",
                "00000000",
                null,
                2048
        );
        embed = this.config.makeEmbed(data);
        assertEquals("Test description: https://example.com/schematics/000/schematic.schematic, <@00000000>, TestPlayer, <@00000000>, 2.0 kiO", embed.getDescription());
        data = new SchematicDiscordEmbedProvider.SchematicEmbedData(
                new URL("https://example.com/schematics/000/schematic.schematic"),
                "TestPlayer",
                "00000000",
                null,
                -1
        );
        embed = this.config.makeEmbed(data);
        assertEquals("Test description: https://example.com/schematics/000/schematic.schematic, <@00000000>, TestPlayer, <@00000000>", embed.getDescription());
        data = new SchematicDiscordEmbedProvider.SchematicEmbedData(
                new URL("https://example.com/schematics/000/schematic.schematic"),
                "TestPlayer",
                null,
                null,
                -1
        );
        embed = this.config.makeEmbed(data);
        assertEquals("Test description: https://example.com/schematics/000/schematic.schematic, TestPlayer, TestPlayer", embed.getDescription());
        data = new SchematicDiscordEmbedProvider.SchematicEmbedData(
                new URL("https://example.com/schematics/000/schematic.schematic"),
                null,
                null,
                null,
                -1
        );
        embed = this.config.makeEmbed(data);
        assertEquals("Test description: https://example.com/schematics/000/schematic.schematic", embed.getDescription());
        data = new SchematicDiscordEmbedProvider.SchematicEmbedData(
                null,
                null,
                null,
                null,
                -1
        );
        embed = this.config.makeEmbed(data);
        assertEquals("Test description", embed.getDescription());
    }
}