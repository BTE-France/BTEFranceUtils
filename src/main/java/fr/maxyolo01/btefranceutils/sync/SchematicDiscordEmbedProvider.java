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
     * @return the embed to send in response to the event
     */
    MessageEmbed provide(SchematicEmbedData data);

    final class SchematicEmbedData {

        private final URL schematicUrl;
        private final String mcPlayerName;
        private final String discordPlayerId;
        private final Address address;
        private final long fileSize;

        /**
         * @param schematicUrl - the url at which the schematic is now available for download, or null if something went wrong
         * @param mcPlayerName - the name of the Minecraft player that saved the schematic, or null if something went wrong
         * @param discordPlayerId - the Discord id of the player that saved the schematic, or null if it was not found
         * @param address - the address of the center of the are where the schematic, or null if something went wrong.
         *                The address may be incomplete.
         * @param fileSize - The file size of the schematic that was saved, or -1 if something went wrong
         */
        public SchematicEmbedData(
                @Nullable URL schematicUrl,
                @Nullable String mcPlayerName,
                @Nullable String discordPlayerId,
                @Nullable Address address,
                long fileSize) {
            this.schematicUrl = schematicUrl;
            this.mcPlayerName = mcPlayerName;
            this.discordPlayerId = discordPlayerId;
            this.address = address;
            this.fileSize = fileSize;
        }

        public URL getSchematicUrl() {
            return this.schematicUrl;
        }

        public String getMcPlayerName() {
            return this.mcPlayerName;
        }

        public String getDiscordPlayerId() {
            return this.discordPlayerId;
        }

        public Address getAddress() {
            return this.address;
        }

        public long getFileSize() {
            return this.fileSize;
        }

    }

}
