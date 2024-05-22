package emanondev.core.util;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
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
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import emanondev.core.CoreMain;
import emanondev.core.CorePlugin;
import emanondev.core.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WorldEditUtility {

    /**
     * One time generated ID.
     */
    private static final UUID DEFAULT_ID = UUID.fromString("a233eb4b-4cab-42cd-9fd9-7e7b9a3f74be");

    private WorldEditUtility() {
        throw new AssertionError();
    }

    public static CompletableFuture<Clipboard> load(@NotNull File dest, @NotNull Plugin plugin, boolean async) {
        CompletableFuture<Clipboard> future = new CompletableFuture<>();
        BukkitRunnable loadTask = new BukkitRunnable() {
            public void run() {
                ClipboardFormat format = ClipboardFormats.findByFile(dest);
                try (ClipboardReader reader = format.getReader(new FileInputStream(dest))) {
                    future.complete(new ClipboardContainer(reader.read()));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        };
        if (Hooks.isWorldEditAsync() && async) {
            loadTask.runTaskAsynchronously(plugin);
        } else {
            loadTask.runTask(plugin);
        }
        return future;
    }

    @Deprecated
    public static Clipboard copy(@NotNull Location corner1, @NotNull Location corner2, boolean copyEntity, boolean copyBiomes, boolean async, @NotNull Plugin plugin) {
        if (!Objects.equals(corner1.getWorld(), corner2.getWorld()))
            throw new IllegalArgumentException();
        return copy(corner1.getWorld(), BoundingBox.of(corner1, corner2), copyEntity, copyBiomes, async, plugin);
    }

    @Deprecated
    public static Clipboard copy(@NotNull World w, @NotNull BoundingBox area, boolean copyEntity, boolean copyBiomes, boolean async, @NotNull Plugin plugin) {
        BlockVector3 min = BlockVector3.at((int) area.getMinX(), (int) area.getMinY(), (int) area.getMinZ());

        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(w), min,
                BlockVector3.at((int) area.getMaxX(), (int) area.getMaxY(), (int) area.getMaxZ()));
        Clipboard clipboard = new ClipboardContainer(new BlockArrayClipboard(region));
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().fastMode(true).world(BukkitAdapter.adapt(w))
                .maxBlocks(-1).actor(getActor(false)).build();
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
                region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(copyEntity);
        forwardExtentCopy.setCopyingBiomes(copyBiomes);
        try {
            Operations.complete(forwardExtentCopy);
            editSession.close();
        } catch (WorldEditException e) {
            e.printStackTrace();
            return null;
        }
        return clipboard;
    }

    public static CompletableFuture<Clipboard> copy(@NotNull BoundingBox area, @NotNull World w, boolean copyEntity, boolean copyBiomes, boolean async, @NotNull Plugin plugin) {
        CompletableFuture<Clipboard> future = new CompletableFuture<>();
        BlockVector3 min = BlockVector3.at((int) area.getMinX(), (int) area.getMinY(), (int) area.getMinZ());

        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(w), min,
                BlockVector3.at((int) area.getMaxX(), (int) area.getMaxY(), (int) area.getMaxZ()));
        Clipboard clipboard = new ClipboardContainer(new BlockArrayClipboard(region));
        Runnable pasteTask = () -> {
            try {
                try {
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().fastMode(true).world(BukkitAdapter.adapt(w))
                            .maxBlocks(-1).actor(getActor(false)).build();
                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
                            region.getMinimumPoint());
                    forwardExtentCopy.setCopyingEntities(copyEntity);
                    forwardExtentCopy.setCopyingBiomes(copyBiomes);
                    Operations.complete(forwardExtentCopy);
                    future.complete(clipboard);
                    editSession.close();
                } catch (WorldEditException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            } catch (NullPointerException | WorldEditException e) {
                throw new RuntimeException(e);
            }
        };
        if (Hooks.isWorldEditAsync() && async)
            Bukkit.getScheduler().runTaskAsynchronously(plugin, pasteTask);
        else
            Bukkit.getScheduler().runTask(plugin, pasteTask);

        return future;
    }

    private static Actor getActor(boolean persistent) {
        if (persistent)
            return BukkitAdapter.adapt(Bukkit.getConsoleSender());
        return new BukkitCommandSender((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"), Bukkit.getConsoleSender()) {

            @Override
            public SessionKey getSessionKey() {
                return new SessionKey() {
                    @Override
                    public @NotNull String getName() {
                        return Bukkit.getConsoleSender().getName();
                    }

                    @Override
                    public boolean isActive() {
                        return false;
                    }

                    @Override
                    public boolean isPersistent() {
                        return false;
                    }

                    @Override
                    public UUID getUniqueId() {
                        return DEFAULT_ID;
                    }
                };
            }
        };
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

    public static CompletableFuture<EditSession> paste(Location location, Clipboard clipboard, boolean async, CorePlugin plugin,
                                                       boolean ignoreAir, boolean copyEntities, boolean copyBiomes) {
        return paste(location, clipboard, async, plugin, ignoreAir, copyEntities, copyBiomes, false);
    }

    public static CompletableFuture<EditSession> paste(Location location, Clipboard clipboard, boolean async, CorePlugin plugin,
                                                       boolean ignoreAir, boolean copyEntities, boolean copyBiomes, boolean persistent) {
        CompletableFuture<EditSession> future = new CompletableFuture<>();
        Runnable pasteTask = () -> {
            try {
                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                        .world(new BukkitWorld(location.getWorld())).actor(getActor(persistent)).fastMode(true)
                        .maxBlocks(-1).build();
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .copyBiomes(copyBiomes).copyEntities(copyEntities).ignoreAirBlocks(ignoreAir).build();
                Operations.complete(operation);
                future.complete(editSession);
                editSession.close();
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                plugin.logIssue("WorldEdit (pasting clipboard) was aborted, is server turning offline?");
            }
        };
        if (Hooks.isWorldEditAsync() && async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, pasteTask);
        } else {
            Bukkit.getScheduler().runTask(plugin, pasteTask);
        }
        return future;
    }

    public static @Nullable Region getSelectionRegion(@NotNull Player player) {
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(player.getWorld());
        if (!session.isSelectionDefined(world))
            return null;
        return session.getSelection(BukkitAdapter.adapt(player.getWorld()));
    }

    /**
     * Note: do NOT use this for isInside or intersection checks on BoundingBox
     *
     * @param player
     * @return bounding box, faces of corner of maximus point ARE NOT included on volume
     */
    public static @Nullable BoundingBox getSelectionBox(@NotNull Player player) {
        Region region = getSelectionRegion(player);
        if (region == null)
            return null;
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        return new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    /**
     * Note: do NOT use this for copy and paste operation with WorldEdit
     *
     * @param player
     * @return bounding box, faces of corner of maximus point ARE included on volume
     */
    public static @Nullable BoundingBox getSelectionBoxExpanded(@NotNull Player player) {
        Region region = getSelectionRegion(player);
        if (region == null)
            return null;
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        return new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
    }


    public static void clearSelection(@NotNull Player player) {
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(player.getWorld());
        if (!session.isSelectionDefined(world))
            return;
        session.getRegionSelector(world).clear();
    }


    public static CompletableFuture<EditSession> pasteAir(BoundingBox box, World world, boolean async, CorePlugin plugin) {
        CompletableFuture<EditSession> future = new CompletableFuture<>();
        Runnable pasteTask = () -> {
            try {
                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                        .world(new BukkitWorld(world)).actor(getActor(false)).fastMode(true)
                        .maxBlocks(-1).build();
                //EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1);
                BaseBlock block = BlockTypes.AIR.getDefaultState().toBaseBlock();
                Region region = new CuboidRegion(new BukkitWorld(world),
                        BlockVector3.at(box.getMinX(), box.getMinY(), box.getMinZ()),
                        BlockVector3.at(box.getMaxX(), box.getMaxY(), box.getMaxZ()));
                editSession.setBlocks(region, block);
                future.complete(editSession);
                editSession.close();
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                if (e.getMessage().contains(".getHandle()\" because \"chunk\" is null"))
                    plugin.logIssue("WorldEdit (paste air) was aborted, is server turning offline?");
                else
                    throw new RuntimeException(e);
            }
        };
        if (Hooks.isWorldEditAsync() && async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, pasteTask);
        } else {
            Bukkit.getScheduler().runTask(plugin, pasteTask);
        }
        return future;
    }

    @Deprecated
    public static boolean paste(Location dest, Clipboard clip) {
        return paste(dest, clip, true);
    }

    @Deprecated
    public static boolean paste(Location dest, Clipboard clip, boolean async) {
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
    public static boolean paste(Location dest, Clipboard clip, double rotationDegree) {
        return paste(dest, clip, rotationDegree, true);
    }

    @Deprecated
    public static boolean paste(Location dest, Clipboard clip, double rotationDegree, boolean async) {
        if (async && Hooks.isWorldEditAsync())
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

    @Deprecated
    public static void clearArea(Location corner1, Location corner2) {
        clearArea(corner1, corner2, true);
    }

    @Deprecated
    public static void clearArea(Location corner1, Location corner2, boolean async) {
        if (!Objects.equals(corner1.getWorld(), corner2.getWorld()))
            throw new IllegalArgumentException();
        clearArea(corner1.getWorld(), BoundingBox.of(corner1, corner2));
    }

    @Deprecated
    public static void clearArea(World w, BoundingBox area) {
        clearArea(w, area, true);
    }

    @Deprecated
    public static void clearArea(World w, BoundingBox area, boolean async) {
        BukkitWorld world = new BukkitWorld(w);
        BlockVector3 pos1 = BlockVector3.at(area.getMinX(), Math.max(w.getMinHeight(), area.getMinY()), area.getMinZ());
        BlockVector3 pos2 = BlockVector3.at(area.getMaxX() - 1, Math.min(w.getMaxHeight(), area.getMaxY() - 1), area.getMaxZ() - 1);
        CuboidRegion region = new CuboidRegion(world, pos1, pos2);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            BaseBlock block = BlockTypes.AIR.getDefaultState().toBaseBlock();
            if (async && Hooks.isWorldEditAsync())
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

    @Deprecated
    public static Clipboard copy(@NotNull Location corner1, @NotNull Location corner2, boolean copyEntity, boolean copyBiomes) {
        return copy(corner1, corner2, copyEntity, copyBiomes, true);
    }

    @Deprecated
    public static Clipboard copy(@NotNull Location corner1, @NotNull Location corner2, boolean copyEntity, boolean copyBiomes, boolean async) {
        if (!Objects.equals(corner1.getWorld(), corner2.getWorld()))
            throw new IllegalArgumentException();
        return copy(corner1.getWorld(), BoundingBox.of(corner1, corner2), copyEntity, copyBiomes);
    }

    @Deprecated
    public static Clipboard copy(@NotNull World w, @NotNull BoundingBox area, boolean copyEntity, boolean copyBiomes) {
        return copy(w, area, copyEntity, copyBiomes, true);
    }

    @Deprecated
    public static Clipboard copy(@NotNull World w, @NotNull BoundingBox area, boolean copyEntity, boolean copyBiomes, boolean async) {
        return copy(w, area, copyEntity, copyBiomes, async, CoreMain.get());
    }

    @Deprecated
    public static Clipboard load(@NotNull File dest) {
        return load(dest, CoreMain.get());
    }

    @Deprecated
    public static Clipboard load(@NotNull File dest, @NotNull Plugin plugin) {
        ClipboardFormat format = ClipboardFormats.findByFile(dest);
        try (ClipboardReader reader = format.getReader(new FileInputStream(dest))) {
            return new ClipboardContainer(reader.read());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
