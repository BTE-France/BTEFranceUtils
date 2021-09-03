package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import fr.maxyolo01.btefranceutils.BteFranceUtils;
import fr.maxyolo01.btefranceutils.util.formatting.ByteFormatter;

import fr.maxyolo01.btefranceutils.util.formatting.IECByteFormatter;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class SchematicSyncConfig {

    private final ConfigurationSection section;

    private final ByteFormatter formatter = new IECByteFormatter();

    public SchematicSyncConfig(ConfigurationSection section) {
        this.formatter.setSuffixes("O", "kiO", "MiO", "GiO", "TiO", "PiO", "EiO");
        this.section = section;
    }

    public SchematicSynchronizationService makeService() throws InvalidSchematicSyncConfigException{
        Logger logger = BteFranceUtils.instance().getLogger();
        WorldEdit worldEdit = WorldEdit.getInstance();
        LocalConfiguration worldEditConfig = worldEdit.getConfiguration();
        Path schemDir = worldEdit.getWorkingDirectoryFile(worldEditConfig.saveDir).toPath();
        String webDirPath = this.section.getString("webDirectoryPath");
        if (webDirPath == null) throw new InvalidSchematicSyncConfigException("Missing web directory");
        Path webDir;
        try {
            webDir = new File(webDirPath).toPath();
        } catch (InvalidPathException e) {
            throw new InvalidSchematicSyncConfigException("Invalid web directory path");
        }
        String symlinkPath = this.section.getString("symlinkPath");
        Path symlinkRoot;
        if (symlinkPath == null) {
            symlinkRoot = schemDir;
        } else {
            symlinkRoot = new File(symlinkPath).toPath();
        }
        String urlRoot = this.section.getString("urlRoot");
        if (urlRoot == null) throw new InvalidSchematicSyncConfigException("Missing url root");
        String salt = this.section.getString("salt");
        if (salt == null) throw new InvalidSchematicSyncConfigException("Missing salt");
        DiscordSRV discordSrv = DiscordSRV.getPlugin();
        TextChannel channel = discordSrv.getJda().getTextChannelById(this.section.getLong("channelId"));
        if (channel == null) throw new InvalidSchematicSyncConfigException("Invalid channel ID");
        EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
        HttpClientBuilder builder = HttpClientBuilder.create();
        String userAgent = this.section.getString("nominatimUserAgent");
        if (userAgent == null) throw new InvalidSchematicSyncConfigException("Missing user agent");
        builder.setUserAgent(userAgent);
        String nominatimEndpoint = this.section.getString("nominatimEndpoint", "https://nominatim.openstreetmap.org/");
        String nominatimEmail = this.section.getString("nominatimEmail");
        if (nominatimEmail == null || "example@example.com".equals(nominatimEmail)) {
            throw new InvalidSchematicSyncConfigException("Missing or default nominatim email");
        }
        NominatimClient nominatim =  new JsonNominatimClient(nominatimEndpoint, builder.build(), nominatimEmail);
        return new SchematicSynchronizationService(
                schemDir,
                webDir,
                symlinkRoot,
                urlRoot,
                salt,
                channel,
                this::makeEmbed,
                discordSrv.getAccountLinkManager()::getDiscordId,
                bteSettings.projection(),
                nominatim,
                logger);
    }

    public MessageEmbed makeEmbed(@Nullable URL schematicUrl, @Nullable String mcPlayerName, @Nullable String discordUserName, @Nullable Address address, long fileSize) {
        EmbedBuilder builder = new EmbedBuilder();
        String thumbnailUrl = this.section.getString("thumbnail");
        if (thumbnailUrl != null) builder.setThumbnail(thumbnailUrl);
        if (schematicUrl == null) {
            ConfigurationSection errorSection = this.section.getConfigurationSection("errorEmbed");
            if (errorSection != null) {
                builder.setTitle(errorSection.getString("title", "Error"));
                builder.setDescription(errorSection.getString("description", "There was an error when linking a new schematic"));
            } else {
                builder.setTitle("Error");
            }
            builder.setColor(0xFF0000);
        } else {
            ConfigurationSection section = this.section.getConfigurationSection("embed");
            if (section != null) {
                builder.setTitle(section.getString("title", "New schematic"));
                String desc = section.getString("description", "Someone created a new schematic!");
                desc = desc.replace("{url}", schematicUrl.toString());
                if (discordUserName != null) {
                    builder.setDescription(desc.replace("{user}", "<@" + discordUserName + ">"));
                } else if (mcPlayerName != null) {
                    builder.setDescription(desc.replace("{user}", mcPlayerName));
                } else if (!desc.contains("{user}")) {
                    builder.setDescription(desc);
                }

                ConfigurationSection fields = section.getConfigurationSection("fields");
                for (String key : fields.getKeys(false)) {
                    ConfigurationSection fieldSection = fields.getConfigurationSection(key);
                    String name = fieldSection.getString("name", "Field name:");
                    String pattern = fieldSection.getString("pattern", "Some stuff");
                    boolean inline = fieldSection.getBoolean("inline", false);
                    if (pattern.contains("{user}")) {
                        if (discordUserName != null) {
                            pattern = pattern.replace("{user}", discordUserName);
                        } else if (mcPlayerName != null) {
                            pattern = pattern.replace("{user}", mcPlayerName);
                        } else {
                            continue;
                        }
                    }
                    if (pattern.contains("{address}")) {
                        String adr;
                        if (address != null && (adr = address.getDisplayName()) != null) {
                            pattern = pattern.replace("{address}", adr);
                        } else {
                            continue;
                        }
                    }
                    pattern = pattern.replace("{url}", schematicUrl.toString());
                    if (pattern.contains("{size}")) {
                        if (discordUserName != null) {
                            pattern = pattern.replace("{size}", this.formatter.format(fileSize));
                        } else {
                            continue;
                        }
                    }
                    builder.addField(name, pattern, inline);
                }
            } else {
                builder.setTitle("Missing config section");
            }
            builder.setColor(0x00c794);
        }
        return builder.build();
    }

    public static class InvalidSchematicSyncConfigException extends Exception {
        private InvalidSchematicSyncConfigException(String message) {
            super(message);
        }
    }

}
