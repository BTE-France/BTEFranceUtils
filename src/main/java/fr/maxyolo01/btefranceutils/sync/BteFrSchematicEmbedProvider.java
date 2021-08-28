package fr.maxyolo01.btefranceutils.sync;

import fr.dudie.nominatim.model.Address;
import fr.maxyolo01.btefranceutils.util.formatting.ByteFormatter;
import fr.maxyolo01.btefranceutils.util.formatting.IECByteFormatter;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class BteFrSchematicEmbedProvider implements SchematicDiscordEmbedProvider {

    //TODO have that in the config
    private static final String DSCD_MSG_TITLE = "Nouvelle schematic!";
    private static final String DSCD_MSG_DESCRIPTION = "%s a créé un nouvelle schematic à %s";
    private static final String DSCD_MSG_THUMBNAIL = "https://i.imgur.com/1ZPB2Wt.png";
    private static final String DSCD_MSG_DOWNLOAD = "Lien de téléchargement: ";
    private static final String DSCD_MSG_SIZE = "Taille: ";

    private final ByteFormatter formatter = new IECByteFormatter();

    public BteFrSchematicEmbedProvider() {
        this.formatter.setSuffixes("O", "kiO", "MiO", "GiO", "TiO", "PiO", "EiO");
    }

    @Override
    public MessageEmbed provide(@Nullable URL schematicUrl, @Nullable String mcPlayerName, @Nullable String discordUserName, @Nullable Address address, long fileSize) {
        EmbedBuilder builder = new EmbedBuilder();
        String description = String.format(DSCD_MSG_DESCRIPTION, mcPlayerName, address.getDisplayName());
        builder.setTitle(DSCD_MSG_TITLE).setDescription(description).setThumbnail(DSCD_MSG_THUMBNAIL);
        builder.addField(DSCD_MSG_DOWNLOAD, schematicUrl.toString(), false);
        builder.addField(DSCD_MSG_SIZE, this.formatter.format(fileSize), true);
        builder.setColor(0x00c794);
        return builder.build();
    }

}
