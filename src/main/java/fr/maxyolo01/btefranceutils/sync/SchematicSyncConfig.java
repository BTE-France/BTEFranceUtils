package fr.maxyolo01.btefranceutils.sync;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import fr.dudie.nominatim.model.Element;
import fr.maxyolo01.btefranceutils.util.formatting.ByteFormatter;

import fr.maxyolo01.btefranceutils.util.formatting.Formatting;
import fr.maxyolo01.btefranceutils.util.formatting.IECByteFormatter;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SchematicSyncConfig {

    private final ConfigurationSection section;

    private final ByteFormatter formatter = new IECByteFormatter();
    private final Map<String, Function<SchematicDiscordEmbedProvider.SchematicEmbedData, String>> placeholders = new HashMap<>();
    private final List<Field> fields = new ArrayList<>();
    private String thumbnailUrl;
    private String title;
    private Field description;
    private int color;
    private MessageEmbed errorEmbed;

    private final Logger logger;

    public SchematicSyncConfig(ConfigurationSection section, Logger logger) {
        this.formatter.setSuffixes("O", "kiO", "MiO", "GiO", "TiO", "PiO", "EiO");
        this.section = section;
        this.logger = logger;
        this.setupPlaceholders();
        this.readMessageConfig();
    }

    public SchematicSynchronizationService makeService() throws InvalidSchematicSyncConfigException{
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
                this.logger);
    }

    public MessageEmbed makeEmbed(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            if (this.color >= 0) builder.setColor(this.color);
            if (this.title != null) builder.setTitle(this.title);
            if (this.thumbnailUrl != null) builder.setThumbnail(this.thumbnailUrl);
            String desc;
            if (this.description != null && (desc = this.description.getFormattedValue(data)) != null)
                builder.setDescription(desc);
            for (Field field: this.fields) {
                String formatted = field.getFormattedValue(data);
                if (formatted != null) {
                    builder.addField(field.name, formatted, field.inLine);
                } else if (field.required) {
                    return this.errorEmbed;
                }
            }
            return builder.build();
        } catch (Exception e) {
            this.logger.severe("Error when building schematic embed message");
            e.printStackTrace();
            return this.errorEmbed;
        }
    }

    private void setupPlaceholders() {
        this.placeholders.put("{url}", this::getFormattedUrl);
        this.placeholders.put("{fileName}", this::getFormattedFileName);
        this.placeholders.put("{playerName}", this::getFormattedPlayerName);
        this.placeholders.put("{minecraftName}", SchematicDiscordEmbedProvider.SchematicEmbedData::getMcPlayerName);
        this.placeholders.put("{discordName}", this::getFormattedDiscordId);
        this.placeholders.put("{address}", this::getDisplayAddress);
        String[] elements = {"road", "suburb", "city", "country"};
        for (String element: elements) {
            this.placeholders.put("{" + element + "}", d -> this.getFormattedAddressElement(element, d));
        }
        this.placeholders.put("{size}", this::getDisplayFileSize);
    }

    private void readMessageConfig() {
        EmbedBuilder errorBuilder = new EmbedBuilder();
        ConfigurationSection section = this.section.getConfigurationSection("embed");
        errorBuilder.setTitle("Error").setDescription("An error happened when formatting an embed message.").setColor(0xFF0000);
        this.errorEmbed = errorBuilder.build();
        try {
            if (section == null) throw new InvalidSchematicSyncConfigException("No embed configuration section");
            this.title = section.getString("title");
            ConfigurationSection desc = section.getConfigurationSection("description");
            if (desc != null) this.description = new Field(desc);
            this.thumbnailUrl = section.getString("thumbnail");
            ConfigurationSection fields = section.getConfigurationSection("fields");
            if (fields != null) for (String key: fields.getKeys(false)) {
                ConfigurationSection fieldSection = fields.getConfigurationSection(key);
                this.fields.add(new Field(fieldSection));
            }
            String colorString = section.getString("color");
            this.color = colorString != null ? Formatting.hexColorToInt(colorString, false) : -1;
        } catch (Exception e) {
            this.logger.severe("An error happened when reading the schematic sync embed section. Only error messages will be sent.");
            e.printStackTrace();
        }
    }

    private String getFormattedUrl(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        URL url = data.getSchematicUrl();
        if (url != null) {
            return url.toString();
        } else {
            return null;
        }
    }

    private String getFormattedFileName(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        String fileName = data.getFileName();
        if (fileName == null) return null;
        return MarkdownSanitizer.escape(fileName);
    }

    private String getFormattedPlayerName(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        String discordId = this.getFormattedDiscordId(data);
        if (discordId != null) {
            return discordId;
        } else {
            String mcName = data.getMcPlayerName();
            if (mcName != null) return MarkdownSanitizer.escape(mcName);
        }
        return null;
    }

    private String getFormattedDiscordId(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        String discordId = data.getDiscordPlayerId();
        if (discordId != null) {
            return "<@" + discordId + ">";
        }
        return null;
    }

    private String getDisplayAddress(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        Address addrr = data.getAddress();
        if (addrr != null) return MarkdownSanitizer.escape(addrr.getDisplayName());
        return null;
    }

    private String getFormattedAddressElement(String element, SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        Address address = data.getAddress();
        if (address == null || element == null) return null;
        Element[] elements = address.getAddressElements();
        for (Element el: elements) if (element.equals(el.getKey())) {
            return el.getValue();
        }
        return null;
    }

    private String getDisplayFileSize(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
        long size = data.getFileSize();
        if (size < 0) return null;
        return this.formatter.format(size);
    }

    private class Field {

        String name;
        List<String> patterns;
        boolean inLine;
        boolean required;

        Field(ConfigurationSection section) {
            this.name = section.getString("name");
            this.patterns = section.getStringList("patterns");
            this.inLine = section.getBoolean("inline");
            this.required = section.getBoolean("required", false);
        }

        private String formatValue(String formatString, SchematicDiscordEmbedProvider.SchematicEmbedData data) {
            if (formatString == null) return null;
            for (String placeholder: SchematicSyncConfig.this.placeholders.keySet()) if (formatString.contains(placeholder)) {
                String value = SchematicSyncConfig.this.placeholders.get(placeholder).apply(data);
                if (value == null) return null;
                formatString = formatString.replaceAll(Pattern.quote(placeholder), value);
            }
            return formatString;
        }

        public String getFormattedValue(SchematicDiscordEmbedProvider.SchematicEmbedData data) {
            for (String pattern: this.patterns) {
                String formatted = this.formatValue(pattern, data);
                if (formatted != null) return formatted;
            }
            return null;
        }

    }
    public static class InvalidSchematicSyncConfigException extends Exception {
        private InvalidSchematicSyncConfigException(String message) {
            super(message);
        }
    }

}
