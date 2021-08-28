package fr.maxyolo01.btefranceutils.sync;

import fr.dudie.nominatim.model.Address;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;

import javax.annotation.Nullable;
import java.net.URL;

/**
 * Provides a {@link MessageEmbed} to send when a schematic is saved.
 *
 * @author SmylerMC
 */
public interface SchematicDiscordEmbedProvider {

    /**
     * Provide a message to send based on the given information.
     * Looking up these pieces of information may have failed,
     * and the implementation shall therefore be able to handle null arguments.
     *
     * @param schematicUrl - the url at which the schematic is now available for download, or null if something went wrong
     * @param mcPlayerName - the name of the Minecraft player that saved the schematic, or null if something went wrong
     * @param discordUserName - the Discord id of the player that saved the schematic, or null if it was not found
     * @param address - the address of the center of the are where the schematic, or null if something went wrong.
     *                The address may be incomplete.
     * @param fileSize - The file size of the schematic that was saved, or -1 if something went wrong
     *
     * @return the embed to send in response to the event
     */
    MessageEmbed provide(
            @Nullable URL schematicUrl,
            @Nullable String mcPlayerName,
            @Nullable String discordUserName,
            @Nullable Address address,
            long fileSize);

}
