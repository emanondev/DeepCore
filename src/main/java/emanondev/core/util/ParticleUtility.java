package emanondev.core.util;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class ParticleUtility {

    private static final int RATEO = 6;
    private static final int SEE_RADIUS = 16*6;

    public static void spawnParticle(Player p, Particle particle, double x, double y, double z) {
        spawnParticle(p, particle, x, y, z, 1, null);
    }

    public static void spawnParticle(Player p, Particle particle, double x, double y, double z, int count, Object data) {
        p.spawnParticle(particle, x, y, z, count, 0, 0, 0, 0, data);
    }

    public static void spawnParticle(Player p, Particle particle, double x, double y, double z, int count) {
        spawnParticle(p, particle, x, y, z, count, null);
    }

    public static void spawnParticleCircle(Player p, Particle particle, double x, double y, double z, double radius, boolean rotateHalf) {
        spawnParticleCircle(p, particle, x, y, z, radius, rotateHalf, null);
    }

    private static boolean canSee(Player p,double x,double z){
        Location loc = p.getLocation();
        return !(loc.getX() < x - SEE_RADIUS) && !(loc.getX() > x + SEE_RADIUS) && !(loc.getZ() < z - SEE_RADIUS) && !(loc.getZ() > z + SEE_RADIUS);
    }

    private static void spawnParticle(Player p, Particle particle, Object data, Location loc, int count,boolean checkDistance){
        if (checkDistance && p.getWorld().equals(loc.getWorld()) && canSee(p,loc.getX(),loc.getZ()))
            p.spawnParticle(particle, loc, count, 0, 0, 0, 0, data);
        p.spawnParticle(particle, loc, count, 0, 0, 0, 0, data);
    }

    private static void spawnParticle(Player p, Particle particle, Object data, double x, double y, double z, int count,boolean checkDistance){
        if (checkDistance && canSee(p,x,z))
            p.spawnParticle(particle, x, y, z, count, 0, 0, 0, 0, data);
        p.spawnParticle(particle, x, y, z, count, 0, 0, 0, 0, data);
    }

    public static void spawnParticleCircle(Player p, Particle particle, double x, double y, double z, double radius, boolean rotateHalf, Object data) {
        Location l = p.getLocation();
        if (x > l.getBlockX() - SEE_RADIUS && x < l.getBlockX() + SEE_RADIUS && z > l.getBlockZ() - SEE_RADIUS && z < l.getBlockZ() + SEE_RADIUS)
            for (int i = 0; i < 8; i++) {
                double degree = ((rotateHalf ? 0 : 0.5) + i) * Math.PI / 4;
                double xOffset = x + radius * Math.sin(degree);
                double zOffset = z + radius * Math.cos(degree);
                spawnParticle(p, particle, xOffset, y + 0.05, zOffset, 1, data);
            }
    }

    public static void spawnParticleBoxEdges(Player p, Particle particle, BoundingBox box) {
        spawnParticleBoxEdges(p, particle, box, null);
    }

    public static void spawnParticleBoxEdges(Player p, Particle particle, BoundingBox box, Object data) {
        markEdges(p, particle, box.getMin(), box.getMax().add(new Vector(-1, -1, -1)), data);
    }

    public static void spawnParticleLine(Player p, Particle particle, Location loc, double maxDistance, Object data){
        spawnParticleLine(p,particle,loc.getX(), loc.getY(), loc.getZ(), loc.getDirection(),maxDistance,data);
    }

    public static void spawnParticleLine(Player p, Particle particle, double x, double y, double z, Vector direction, double maxDistance,  Object data){
        markLine(p,particle,x, y, z, direction,maxDistance,data);
    }

    private static void markLine(Player p, Particle particle, double x, double y, double z, Vector direction, double maxDistance, Object data) {
        //TODO
    }

    private static void markEdges(Player p, Particle particle, Vector min, Vector max, Object data) {
        Location l = p.getLocation();
        int xMin = Math.max(l.getBlockX() - SEE_RADIUS, min.getBlockX()), xMax = Math.min(l.getBlockX() + SEE_RADIUS, max.getBlockX());
        int zMin = Math.max(l.getBlockZ() - SEE_RADIUS, min.getBlockZ()), zMax = Math.min(l.getBlockZ() + SEE_RADIUS, max.getBlockZ());
        for (int i = xMin; i <= xMax; i++) {
            spawnParticle(p, particle, i, min.getY(), min.getZ(), 1, data);
            spawnParticle(p, particle, i, max.getY() + 1, min.getZ(), 1, data);
            spawnParticle(p, particle, i, min.getY(), max.getZ() + 1, 1, data);
            spawnParticle(p, particle, i, max.getY() + 1, max.getZ() + 1, 1, data);
        }
        for (int i = min.getBlockY(); i <= max.getBlockY(); i++) {
            spawnParticle(p, particle, min.getX(), i, min.getZ(), 1, data);
            spawnParticle(p, particle, max.getX() + 1, i, min.getZ(), 1, data);
            spawnParticle(p, particle, min.getX(), i, max.getZ() + 1, 1, data);
            spawnParticle(p, particle, max.getX() + 1, i, max.getZ() + 1, 1, data);
        }
        for (int i = zMin; i <= zMax; i++) {
            spawnParticle(p, particle, min.getX(), min.getY(), i, 1, data);
            spawnParticle(p, particle, max.getX() + 1, min.getY(), i, 1, data);
            spawnParticle(p, particle, min.getX(), max.getY() + 1, i, 1, data);
            spawnParticle(p, particle, max.getX() + 1, max.getY() + 1, i, 1, data);
        }
    }

    public static void spawnParticleBoxFaces(Player p, int tick, Particle particle, BoundingBox box) {
        spawnParticleBoxFaces(p, tick, particle, box, null);
    }

    public static void spawnParticleBoxFaces(Player p, int tick, Particle particle, BoundingBox box, Object data) {
        markFaces(p, tick, particle, box.getMin(), box.getMax().add(new Vector(-1, -1, -1)), data);
    }

    private static void markFaces(Player p, int val, Particle particle, Vector min, Vector max, Object data) {
        Location l = p.getLocation();
        int xMin = Math.max(l.getBlockX() - SEE_RADIUS, min.getBlockX()), xMax = Math.min(l.getBlockX() + SEE_RADIUS, max.getBlockX());
        int zMin = Math.max(l.getBlockZ() - SEE_RADIUS, min.getBlockZ()), zMax = Math.min(l.getBlockZ() + SEE_RADIUS, max.getBlockZ());
        for (int x = xMin; x <= xMax; x++)
            for (int z = zMin; z <= zMax; z++) {
                if (Math.abs(x + min.getBlockY() + z) % RATEO == val % RATEO)
                    spawnParticle(p, particle, x, min.getBlockY(), z, data);
                if (Math.abs(x + max.getBlockY() + 1 + z) % RATEO == val % RATEO)
                    spawnParticle(p, particle, x, max.getBlockY() + 1, z, data);
            }
        for (int x = xMin; x <= xMax; x++)
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                if (Math.abs(x + y + min.getBlockZ()) % RATEO == val % RATEO)
                    spawnParticle(p, particle, x, y, min.getBlockZ(), data);
                if (Math.abs(x + y + max.getBlockZ() + 1) % RATEO == val % RATEO)
                    spawnParticle(p, particle, x, y, max.getBlockZ() + 1, data);
            }
        for (int z = zMin; z <= zMax; z++)
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                if (Math.abs(min.getBlockX() + y + z) % RATEO == val % RATEO)
                    spawnParticle(p, particle, min.getBlockX(), y, z, data);
                if (Math.abs(max.getBlockX() + 1 + y + z) % RATEO == val % RATEO)
                    spawnParticle(p, particle, max.getBlockX() + 1, y, z, data);
            }
    }

    public static void spawnParticle(Player p, Particle particle, double x, double y, double z, Object data) {
        spawnParticle(p, particle, x, y, z, 0, data);
    }

    public static boolean spawnParticleWorldEditRegionEdges(Player p, Particle particle) {
        return spawnParticleWorldEditRegionEdges(p, particle, null);
    }

    public static boolean spawnParticleWorldEditRegionEdges(Player p, Particle particle, Object data) {
        try {
            Region sel = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p))
                    .getSelection(BukkitAdapter.adapt(p.getWorld()));
            markEdges(p, particle, new Vector(sel.getMinimumPoint().getX(), sel.getMinimumPoint().getY(),
                    sel.getMinimumPoint().getZ()), new Vector(sel.getMaximumPoint().getX(), sel.getMaximumPoint().getY(),
                    sel.getMaximumPoint().getZ()), data);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
