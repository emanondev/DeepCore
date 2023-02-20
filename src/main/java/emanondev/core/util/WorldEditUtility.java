package emanondev.core.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import emanondev.core.CoreMain;
import emanondev.core.CorePlugin;
import emanondev.core.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class WorldEditUtility {

    private WorldEditUtility() {
        throw new AssertionError();
    }

    public static Clipboard copy(Location corner1, Location corner2, boolean copyEntity, boolean copyBiomes) {
        return copy(corner1, corner2, copyEntity, copyBiomes, true);
    }

    public static Clipboard copy(World w, BoundingBox area, boolean copyEntity, boolean copyBiomes) {
        return copy(w, area, copyEntity, copyBiomes, true);
    }

    @Deprecated
    public static boolean paste(Location dest, Clipboard clip) {
        return paste(dest, clip, true);
    }

    @Deprecated
    public static boolean paste(Location dest, Clipboard clip, double rotationDegree) {
        return paste(dest, clip, rotationDegree, true);
    }

    @Deprecated
    public static void clearArea(Location corner1, Location corner2) {
        clearArea(corner1, corner2, true);
    }

    @Deprecated
    public static void clearArea(World w, BoundingBox area) {
        clearArea(w, area, true);
    }

    public static Clipboard load(File dest) {
        ClipboardFormat format = ClipboardFormats.findByFile(dest);
        try (ClipboardReader reader = format.getReader(new FileInputStream(dest))) {
            return reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Clipboard copy(Location corner1, Location corner2, boolean copyEntity, boolean copyBiomes, boolean async) {
        if (!corner1.getWorld().equals(corner2.getWorld()))
            throw new IllegalArgumentException();
        return copy(corner1.getWorld(), BoundingBox.of(corner1, corner2), copyEntity, copyBiomes);
    }

    public static Clipboard copy(World w, BoundingBox area, boolean copyEntity, boolean copyBiomes, boolean async) {
        BlockVector3 min = BlockVector3.at((int) area.getMinX(), (int) area.getMinY(), (int) area.getMinZ());

        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(w), min,
                BlockVector3.at((int) area.getMaxX(), (int) area.getMaxY(), (int) area.getMaxZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(min);
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(w))
                .maxBlocks(-1).build();

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
                region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(copyEntity);
        forwardExtentCopy.setCopyingBiomes(copyBiomes);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
            return null;
        }
        return clipboard;
    }

    @Deprecated
    public static boolean paste(Location dest, Clipboard clip, boolean async) {
        /*if (async && mayAsync())
            FaweAPI.getTaskManager().async(() -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                        .world(BukkitAdapter.adapt(dest.getWorld())).maxBlocks(-1).build()) {
                    Operation operation = new ClipboardHolder(clip).createPaste(editSession)
                            .to(BlockVector3.at(dest.getBlockX(), dest.getBlockY(), dest.getBlockZ())).ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            });
        else*/
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(dest.getWorld())).maxBlocks(-1).build()) {
                Operation operation = new ClipboardHolder(clip).createPaste(editSession)
                        .to(BlockVector3.at(dest.getBlockX(), dest.getBlockY(), dest.getBlockZ())).ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }


    @Deprecated
    public static boolean paste(Location dest, Clipboard clip, double rotationDegree, boolean async) {
        if (async && mayAsync())
            Bukkit.getScheduler().runTaskAsynchronously(CoreMain.get(), () -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                        .world(BukkitAdapter.adapt(dest.getWorld())).maxBlocks(-1).build()) {
                    ClipboardHolder holder = new ClipboardHolder(clip);
                    if (rotationDegree != 0) {
                        holder.setTransform(new AffineTransform().rotateY(-rotationDegree));
                    }
                    Operation operation = holder.createPaste(editSession)
                            .to(BlockVector3.at(dest.getBlockX(), dest.getBlockY(), dest.getBlockZ())).ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            });
        else try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(BukkitAdapter.adapt(dest.getWorld())).maxBlocks(-1).build()) {
            ClipboardHolder holder = new ClipboardHolder(clip);
            if (rotationDegree != 0) {
                holder.setTransform(new AffineTransform().rotateY(-rotationDegree));
            }
            Operation operation = holder.createPaste(editSession)
                    .to(BlockVector3.at(dest.getBlockX(), dest.getBlockY(), dest.getBlockZ())).ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean save(File dest, Clipboard clip) {
        dest.getParentFile().mkdirs();
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(dest))) {
            writer.write(clip);
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }


    @Deprecated
    public static void clearArea(Location corner1, Location corner2, boolean async) {
        if (!corner1.getWorld().equals(corner2.getWorld()))
            throw new IllegalArgumentException();
        clearArea(corner1.getWorld(), BoundingBox.of(corner1, corner2));
    }

    @Deprecated
    public static void clearArea(World w, BoundingBox area, boolean async) {
        BukkitWorld world = new BukkitWorld(w);
        BlockVector3 pos1 = BlockVector3.at(area.getMinX(), Math.max(w.getMinHeight(), area.getMinY()), area.getMinZ());
        BlockVector3 pos2 = BlockVector3.at(area.getMaxX() - 1, Math.min(w.getMaxHeight(), area.getMaxY() - 1), area.getMaxZ() - 1);
        CuboidRegion region = new CuboidRegion(world, pos1, pos2);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            //EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
            BaseBlock block = BlockTypes.AIR.getDefaultState().toBaseBlock();
            if (async && mayAsync())
                Bukkit.getScheduler().runTaskAsynchronously(CoreMain.get(), () -> {
                    try {
                        editSession.setBlocks((Region) region, block);
                    } catch (MaxChangedBlocksException e) {
                        e.printStackTrace();
                    }
                });
            else try {
                editSession.setBlocks((Region) region, block);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean mayAsync() {
        return Hooks.isEnabled("FastAsyncWorldEdit") || Hooks.isEnabled("AsyncWorldEdit");
    }

    public static CompletableFuture<EditSession> paste(Location location, Clipboard clipboard, boolean async, CorePlugin plugin,
                                                         boolean ignoreAir,boolean copyEntities, boolean copyBiomes) {
        CompletableFuture<EditSession> future = new CompletableFuture<>();
        Runnable pasteTask = () -> {
            try {
                EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), -1);
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .copyBiomes(copyBiomes).copyEntities(copyEntities).ignoreAirBlocks(ignoreAir).build();
                Operations.complete(operation);
                future.complete(editSession);
                Optional.ofNullable(editSession).ifPresent(EditSession::close);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        };
        if (mayAsync() && async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin,pasteTask);
        } else {
            Bukkit.getScheduler().runTask(plugin,pasteTask);
        }
        return future;
    }

    public static CompletableFuture<EditSession> pasteAir(BoundingBox box, World world, boolean async, CorePlugin plugin) {
        CompletableFuture<EditSession> future = new CompletableFuture<>();
        Runnable pasteTask = () -> {
            try {
                EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1);
                BaseBlock block = BlockTypes.AIR.getDefaultState().toBaseBlock();
                Region region = new CuboidRegion(new BukkitWorld(world),
                        BlockVector3.at(box.getMinX(), box.getMinY(), box.getMinZ()),
                        BlockVector3.at(box.getMaxX(), box.getMaxY(), box.getMaxZ()));
                editSession.setBlocks(region,block);
                future.complete(editSession);
                Optional.ofNullable(editSession).ifPresent(EditSession::close);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        };
        if (mayAsync() && async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin,pasteTask);
        } else {
            Bukkit.getScheduler().runTask(plugin,pasteTask);
        }
        return future;
    }
}
