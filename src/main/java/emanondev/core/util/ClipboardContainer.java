package emanondev.core.util;

import com.fastasyncworldedit.core.extent.processor.heightmap.HeightMapType;
import com.fastasyncworldedit.core.function.generator.GenBase;
import com.fastasyncworldedit.core.function.generator.Resource;
import com.fastasyncworldedit.core.function.visitor.Order;
import com.fastasyncworldedit.core.history.changeset.AbstractChangeSet;
import com.fastasyncworldedit.core.math.MutableBlockVector3;
import com.fastasyncworldedit.core.nbt.FaweCompoundTag;
import com.fastasyncworldedit.core.queue.Filter;
import com.fastasyncworldedit.core.queue.IBatchProcessor;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import emanondev.core.Hooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

public final class ClipboardContainer implements Clipboard {

    private final Clipboard parent;

    public ClipboardContainer(Clipboard contained) {
        this.parent = contained;
        track();
    }


    private void track() {
        if (parent instanceof ClipboardContainer)
            return;
        if (Hooks.isFAWEEnabled())
            FAWECleaner.track(parent);
    }

    @Override
    public Region getRegion() {
        return parent.getRegion();
    }

    @Override
    public BlockVector3 getDimensions() {
        return parent.getDimensions();
    }

    @Override
    public BlockVector3 getOrigin() {
        return parent.getOrigin();
    }

    @Override
    public void setOrigin(BlockVector3 origin) {
        parent.setOrigin(origin);
    }

    public boolean hasBiomes() {
        return parent.hasBiomes();
    }

    @Override
    public void removeEntity(Entity entity) {
        parent.removeEntity(entity);
    }

    public int getWidth() {
        return parent.getWidth();
    }

    public int getHeight() {
        return parent.getHeight();
    }

    public int getLength() {
        return parent.getLength();
    }

    public int getArea() {
        return parent.getArea();
    }

    public int getVolume() {
        return parent.getVolume();
    }

    public Iterator<BlockVector3> iterator(Order order) {
        return parent.iterator(order);
    }

    @Nonnull
    public Iterator<BlockVector3> iterator() {
        return parent.iterator();
    }

    public Iterator<BlockVector2> iterator2d() {
        return parent.iterator2d();
    }

    public URI getURI() {
        return parent.getURI();
    }

    public <T extends Filter> T apply(Region region, T filter, boolean full) {
        return parent.apply(region, filter, full);
    }

    public void close() {
        parent.close();
    }

    public void flush() {
        parent.flush();
    }

    public EditSession paste(World world, BlockVector3 to) {
        return parent.paste(world, to);
    }

    public void save(File file, ClipboardFormat format) throws IOException {
        parent.save(file, format);
    }

    public void save(OutputStream stream, ClipboardFormat format) throws IOException {
        parent.save(stream, format);
    }

    //TODO undo?
    public EditSession paste(World world, BlockVector3 to, boolean allowUndo, boolean pasteAir, @Nullable Transform transform) {
        return parent.paste(world, to, allowUndo, pasteAir, transform);
    }

    public EditSession paste(World world, BlockVector3 to, boolean allowUndo, boolean pasteAir, boolean pasteEntities, @Nullable Transform transform) {
        return parent.paste(world, to, allowUndo, pasteAir, pasteEntities, transform);
    }

    public void paste(Extent extent, BlockVector3 to, boolean pasteAir, @Nullable Transform transform) {
        parent.paste(extent, to, pasteAir, transform);
    }

    public void paste(Extent extent, BlockVector3 to, boolean pasteAir) {
        parent.paste(extent, to, pasteAir);
    }

    public void paste(Extent extent, BlockVector3 to, boolean pasteAir, boolean pasteEntities, boolean pasteBiomes) {
        parent.paste(extent, to, pasteAir, pasteEntities, pasteBiomes);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return parent.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return parent.getMaximumPoint();
    }

    public List<? extends Entity> getEntities(Region region) {
        return parent.getEntities(region);
    }

    public List<? extends Entity> getEntities() {
        return parent.getEntities();
    }

    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        return parent.createEntity(location, entity);
    }

    @Nullable
    public Entity createEntity(Location location, BaseEntity entity, UUID uuid) {
        return parent.createEntity(location, entity, uuid);
    }

    public void removeEntity(int x, int y, int z, UUID uuid) {
        parent.removeEntity(x, y, z, uuid);
    }

    public boolean isQueueEnabled() {
        return parent.isQueueEnabled();
    }

    public void enableQueue() {
        parent.enableQueue();
    }

    public void disableQueue() {
        parent.disableQueue();
    }

    public boolean isWorld() {
        return parent.isWorld();
    }

    public boolean regenerateChunk(int x, int z, @Nullable BiomeType type, @Nullable Long seed) {
        return parent.regenerateChunk(x, z, type, seed);
    }

    public int getHighestTerrainBlock(final int x, final int z, int minY, int maxY) {
        return parent.getHighestTerrainBlock(x, z, minY, maxY);
    }

    public int getHighestTerrainBlock(final int x, final int z, int minY, int maxY, Mask filter) {
        return parent.getHighestTerrainBlock(x, z, minY, maxY, filter);
    }

    public int getNearestSurfaceLayer(int x, int z, int y, int minY, int maxY) {
        return parent.getNearestSurfaceLayer(x, z, y, minY, maxY);
    }

    public int getNearestSurfaceTerrainBlock(int x, int z, int y, int minY, int maxY, int failedMin, int failedMax, Mask mask) {
        return parent.getNearestSurfaceTerrainBlock(x, z, y, minY, maxY, failedMin, failedMax, mask);
    }

    public int getNearestSurfaceTerrainBlock(int x, int z, int y, int minY, int maxY, boolean ignoreAir) {
        return parent.getNearestSurfaceTerrainBlock(x, z, y, minY, maxY, ignoreAir);
    }

    public int getNearestSurfaceTerrainBlock(int x, int z, int y, int minY, int maxY) {
        return parent.getNearestSurfaceTerrainBlock(x, z, y, minY, maxY);
    }

    public int getNearestSurfaceTerrainBlock(int x, int z, int y, int minY, int maxY, int failedMin, int failedMax) {
        return parent.getNearestSurfaceTerrainBlock(x, z, y, minY, maxY, failedMin, failedMax);
    }

    public int getNearestSurfaceTerrainBlock(int x, int z, int y, int minY, int maxY, int failedMin, int failedMax, boolean ignoreAir) {
        return parent.getNearestSurfaceTerrainBlock(x, z, y, minY, maxY, failedMin, failedMax, ignoreAir);
    }

    public void addCaves(Region region) throws WorldEditException {
        parent.addCaves(region);
    }

    public void generate(Region region, GenBase gen) throws WorldEditException {
        parent.generate(region, gen);
    }

    public void addSchems(Region region, Mask mask, List<ClipboardHolder> clipboards, int rarity, boolean rotate) throws WorldEditException {
        parent.addSchems(region, mask, clipboards, rarity, rotate);
    }

    public void spawnResource(Region region, Resource gen, int rarity, int frequency) throws WorldEditException {
        parent.spawnResource(region, gen, rarity, frequency);
    }

    public boolean contains(BlockVector3 pt) {
        return parent.contains(pt);
    }

    public boolean contains(int x, int y, int z) {
        return parent.contains(x, y, z);
    }

    public void addOre(Region region, Mask mask, Pattern material, int size, int frequency, int rarity, int minY, int maxY) throws WorldEditException {
        parent.addOre(region, mask, material, size, frequency, rarity, minY, maxY);
    }

    public void addOres(Region region, Mask mask) throws WorldEditException {
        parent.addOres(region, mask);
    }

    public List<Countable<BlockType>> getBlockDistribution(final Region region) {
        return parent.getBlockDistribution(region);
    }

    public List<Countable<BlockState>> getBlockDistributionWithData(final Region region) {
        return parent.getBlockDistributionWithData(region);
    }

    @Nullable
    public Operation commit() {
        return parent.commit();
    }

    public boolean cancel() {
        return parent.cancel();
    }

    public int getMinY() {
        return parent.getMinY();
    }

    public int getMaxY() {
        return parent.getMaxY();
    }

    public Clipboard lazyCopy(Region region) {
        return parent.lazyCopy(region);
    }

    public int countBlocks(Region region, Set<BaseBlock> searchBlocks) {
        return parent.countBlocks(region, searchBlocks);
    }

    public int countBlocks(Region region, Mask searchMask) {
        return parent.countBlocks(region, searchMask);
    }

    public <B extends BlockStateHolder<B>> int setBlocks(Region region, B block) throws MaxChangedBlocksException {
        return parent.setBlocks(region, block);
    }

    public int setBlocks(Region region, Pattern pattern) throws MaxChangedBlocksException {
        return parent.setBlocks(region, pattern);
    }

    public <B extends BlockStateHolder<B>> int replaceBlocks(Region region, Set<BaseBlock> filter, B replacement) throws MaxChangedBlocksException {
        return parent.replaceBlocks(region, filter, replacement);
    }

    public int replaceBlocks(Region region, Set<BaseBlock> filter, Pattern pattern) throws MaxChangedBlocksException {
        return parent.replaceBlocks(region, filter, pattern);
    }

    public int replaceBlocks(Region region, Mask mask, Pattern pattern) throws MaxChangedBlocksException {
        return parent.replaceBlocks(region, mask, pattern);
    }

    public int center(Region region, Pattern pattern) throws MaxChangedBlocksException {
        return parent.center(region, pattern);
    }

    public int setBlocks(final Set<BlockVector3> vset, final Pattern pattern) {
        return parent.setBlocks(vset, pattern);
    }

    public boolean relight(int x, int y, int z) {
        return parent.relight(x, y, z);
    }

    public boolean relightBlock(int x, int y, int z) {
        return parent.relightBlock(x, y, z);
    }

    public boolean relightSky(int x, int y, int z) {
        return parent.relightSky(x, y, z);
    }

    public Extent addProcessor(IBatchProcessor processor) {
        return parent.addProcessor(processor);
    }

    public Extent addPostProcessor(IBatchProcessor processor) {
        return parent.addPostProcessor(processor);
    }

    public Extent enableHistory(AbstractChangeSet changeSet) {
        return parent.enableHistory(changeSet);
    }

    public Extent disableHistory() {
        return parent.disableHistory();
    }

    public <T extends Filter> T apply(Iterable<BlockVector3> positions, T filter) {
        return parent.apply(positions, filter);
    }

    @Override
    protected void finalize() {
        try {
            clean();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void clean() {
        if (parent instanceof ClipboardContainer container)
            container.clean();
        else if (Hooks.isFAWEEnabled())
            FAWECleaner.clean(parent);
    }

    public BlockState getBlock(BlockVector3 position) {
        return parent.getBlock(position);
    }

    public BlockState getBlock(int x, int y, int z) {
        return parent.getBlock(x, y, z);
    }

    public BaseBlock getFullBlock(BlockVector3 position) {
        return parent.getFullBlock(position);
    }

    public BaseBlock getFullBlock(int x, int y, int z) {
        return parent.getFullBlock(x, y, z);
    }

    public BiomeType getBiome(BlockVector2 position) {
        return parent.getBiome(position);
    }

    public BiomeType getBiomeType(int x, int y, int z) {
        return parent.getBiomeType(x, y, z);
    }

    public BiomeType getBiome(BlockVector3 position) {
        return parent.getBiome(position);
    }

    public int getEmittedLight(BlockVector3 position) {
        return parent.getEmittedLight(position);
    }

    public int getEmittedLight(int x, int y, int z) {
        return parent.getEmittedLight(x, y, z);
    }

    public int getSkyLight(MutableBlockVector3 position) {
        return parent.getSkyLight(position);
    }

    public int getSkyLight(int x, int y, int z) {
        return parent.getSkyLight(x, y, z);
    }

    public int getBrightness(MutableBlockVector3 position) {
        return parent.getBrightness(position);
    }

    public int getBrightness(int x, int y, int z) {
        return parent.getBrightness(x, y, z);
    }

    public int getOpacity(MutableBlockVector3 position) {
        return parent.getOpacity(position);
    }

    public int getOpacity(int x, int y, int z) {
        return parent.getOpacity(x, y, z);
    }

    public int[] getHeightMap(HeightMapType type) {
        return parent.getHeightMap(type);
    }

    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        return parent.setBlock(position, block);
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(int x, int y, int z, B block) throws WorldEditException {
        return parent.setBlock(x, y, z, block);
    }

    @Override
    public boolean tile(int x, int y, int z, FaweCompoundTag tile) throws WorldEditException {
        return parent.tile(x,y,z,tile);
    }

    public boolean fullySupports3DBiomes() {
        return parent.fullySupports3DBiomes();
    }

    public boolean setBiome(BlockVector2 position, BiomeType biome) {
        return parent.setBiome(position, biome);
    }

    public boolean setBiome(int x, int y, int z, BiomeType biome) {
        return parent.setBiome(x, y, z, biome);
    }

    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        return parent.setBiome(position, biome);
    }

    public void setBlockLight(BlockVector3 position, int value) {
        parent.setBlockLight(position, value);
    }

    public void setBlockLight(int x, int y, int z, int value) {
        parent.setBlockLight(x, y, z, value);
    }

    public void setSkyLight(BlockVector3 position, int value) {
        parent.setSkyLight(position, value);
    }

    public void setSkyLight(int x, int y, int z, int value) {
        parent.setSkyLight(x, y, z, value);
    }

    public void setHeightMap(HeightMapType type, int[] heightMap) {
        parent.setHeightMap(type, heightMap);
    }

    public void forEach(Consumer<? super BlockVector3> action) {
        parent.forEach(action);
    }

    public Spliterator<BlockVector3> spliterator() {
        return parent.spliterator();
    }

}
