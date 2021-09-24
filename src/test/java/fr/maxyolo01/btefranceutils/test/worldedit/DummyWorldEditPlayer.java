package fr.maxyolo01.btefranceutils.test.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class DummyWorldEditPlayer implements Player {

    private final String name;
    private final UUID uuid;

    public DummyWorldEditPlayer(UUID uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public boolean isHoldingPickAxe() {
        return false;
    }

    @Override
    public PlayerDirection getCardinalDirection(int yawOffset) {
        return null;
    }

    @Override
    public int getItemInHand() {
        return 0;
    }

    @Override
    public BaseBlock getBlockInHand() {
        return null;
    }

    @Override
    public void giveItem(int type, int amount) {

    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return null;
    }

    @Override
    public boolean hasCreativeMode() {
        return false;
    }

    @Override
    @Deprecated
    public void findFreePosition(WorldVector searchPos) {

    }

    @Override
    @Deprecated
    public void setOnGround(WorldVector searchPos) {

    }

    @Override
    public void findFreePosition() {

    }

    @Override
    public boolean ascendLevel() {
        return false;
    }

    @Override
    public boolean descendLevel() {
        return false;
    }

    @Override
    public boolean ascendToCeiling(int clearance) {
        return false;
    }

    @Override
    public boolean ascendToCeiling(int clearance, boolean alwaysGlass) {
        return false;
    }

    @Override
    public boolean ascendUpwards(int distance) {
        return false;
    }

    @Override
    public boolean ascendUpwards(int distance, boolean alwaysGlass) {
        return false;
    }

    @Override
    public void floatAt(int x, int y, int z, boolean alwaysGlass) {

    }

    @Override
    @Deprecated
    public WorldVector getBlockIn() {
        return null;
    }

    @Override
    @Deprecated
    public WorldVector getBlockOn() {
        return null;
    }

    @Override
    @Deprecated
    public WorldVector getBlockTrace(int range, boolean useLastBlock) {
        return null;
    }

    @Override
    @Deprecated
    public WorldVectorFace getBlockTraceFace(int range, boolean useLastBlock) {
        return null;
    }

    @Override
    @Deprecated
    public WorldVector getBlockTrace(int range) {
        return null;
    }

    @Override
    @Deprecated
    public WorldVector getSolidBlockTrace(int range) {
        return null;
    }

    @Override
    public PlayerDirection getCardinalDirection() {
        return null;
    }

    @Override
    @Deprecated
    public WorldVector getPosition() {
        return null;
    }

    @Override
    public double getPitch() {
        return 0;
    }

    @Override
    public double getYaw() {
        return 0;
    }

    @Override
    public boolean passThroughForwardWall(int range) {
        return false;
    }

    @Override
    public void setPosition(Vector pos, float pitch, float yaw) {

    }

    @Override
    public void setPosition(Vector pos) {

    }

    @Nullable
    @Override
    public BaseEntity getState() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public Extent getExtent() {
        return null;
    }

    @Override
    public boolean remove() {
        return false;
    }

    @Override
    public void printRaw(String msg) {

    }

    @Override
    public void printDebug(String msg) {

    }

    @Override
    public void print(String msg) {

    }

    @Override
    public void printError(String msg) {

    }

    @Override
    public boolean canDestroyBedrock() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public File openFileOpenDialog(String[] extensions) {
        return null;
    }

    @Override
    public File openFileSaveDialog(String[] extensions) {
        return null;
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {

    }

    @Override
    public SessionKey getSessionKey() {
        return null;
    }

    @Nullable
    @Override
    public <T> T getFacet(Class<? extends T> cls) {
        return null;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public void checkPermission(String permission) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

}
