package fr.maxyolo01.btefranceutils.events.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.Event;

import java.io.File;

/**
 * A World Edit event triggered when a a schematic has been saved.
 * This event is triggered by a custom world edit build.
 *
 * @author SmylerMC
 */
public class SchematicSavedEvent extends Event {

    private final Player player;
    private final File file;
    private final LocalSession session;

    public SchematicSavedEvent(Player player, File file, LocalSession session) {
        this.player = player;
        this.file = file;
        this.session = session;
    }

    public Player player() {
        return this.player;
    }

    public File file() {
        return this.file;
    }

    public LocalSession session() {
        return this.session;
    }

}
