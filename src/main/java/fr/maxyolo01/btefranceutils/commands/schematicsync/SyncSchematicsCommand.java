package fr.maxyolo01.btefranceutils.commands.schematicsync;

import fr.maxyolo01.btefranceutils.commands.SegmentedCommandExecutor;

public class SyncSchematicsCommand extends SegmentedCommandExecutor {

    public SyncSchematicsCommand() {
        this.setPermissionNode("btefrance.schematicsync");
        this.addSegment("bulk", new BulkUpdateCommandSegment());
        this.addSegment("service", new ServiceCommandSegment());
    }

}
