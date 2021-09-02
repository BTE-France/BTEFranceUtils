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
import github.scarsz.discordsrv.util.DiscordUtil;
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

    //TODO have that in the config
    private static final String DSCD_MSG_TITLE = "Nouvelle schematic!";
    private static final String DSCD_MSG_DESCRIPTION = "%s a créé un nouvelle schematic à %s";
    private static final String DSCD_MSG_THUMBNAIL = "https://i.imgur.com/1ZPB2Wt.png";
    private static final String DSCD_MSG_DOWNLOAD = "Lien de téléchargement: ";
    private static final String DSCD_MSG_SIZE = "Taille: ";

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
        String description = String.format(DSCD_MSG_DESCRIPTION, mcPlayerName, address.getDisplayName());
        builder.setTitle(DSCD_MSG_TITLE).setDescription(description).setThumbnail(DSCD_MSG_THUMBNAIL);
        builder.addField(DSCD_MSG_DOWNLOAD, schematicUrl.toString(), false);
        builder.addField(DSCD_MSG_SIZE, this.formatter.format(fileSize), true);
        builder.setColor(0x00c794);
        return builder.build();
    }

    public static class InvalidSchematicSyncConfigException extends Exception {
        private InvalidSchematicSyncConfigException(String message) {
            super(message);
        }
    }

}
