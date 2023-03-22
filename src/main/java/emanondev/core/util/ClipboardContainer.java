package emanondev.core.util;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import emanondev.core.Hooks;

public class ClipboardContainer implements Clipboard {

    private final Clipboard parent;

    public ClipboardContainer(Clipboard contained) {
        this.parent = contained;
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

    @Override
    public void removeEntity(Entity entity) {
        parent.removeEntity(entity);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return parent.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return parent.getMaximumPoint();
    }

    @Override
    public boolean setTile(int x, int y, int z, CompoundTag tile) throws WorldEditException {
        return parent.setTile(x, y, z, tile);
    }

    protected void finalize(){
        try{
            clean();
            System.out.println("Cleaned Clipboard "+ this);
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    private void clean() {
        if (parent instanceof ClipboardContainer container)
            container.clean();
        else if (Hooks.isFAWEEnabled())
            FAWECleaner.clean(parent);
    }
}
