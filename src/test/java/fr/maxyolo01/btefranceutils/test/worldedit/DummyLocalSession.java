package fr.maxyolo01.btefranceutils.test.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;

public class DummyLocalSession extends LocalSession {

    private Region region;

    public DummyLocalSession(Region region) {
        this.region = region;
    }

    @Override
    public Region getSelection(World world) throws IncompleteRegionException {
        return this.region;
    }
}
