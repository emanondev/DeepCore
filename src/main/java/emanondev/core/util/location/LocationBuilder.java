package emanondev.core.util.location;


import com.palmergames.bukkit.towny.regen.block.BlockLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@Getter
public class LocationBuilder {

    private World world;
    private String worldName;
    private double x;
    private double y;
    private double z;

    private LocationBuilder() {
    }

    public static LocationBuilder of(Location loc) {
        return new LocationBuilder().x(loc.getX()).y(loc.getY()).z(loc.getY());
    }

    public static LocationBuilder of(Vector loc) {
        return new LocationBuilder().x(loc.getX()).y(loc.getY()).z(loc.getY());
    }

    public static LocationBuilder of(double x, double y, double z) {
        return new LocationBuilder().x(x).y(y).z(z);
    }

    public static LocationBuilder of() {
        return new LocationBuilder();
    }

    public LocationBuilder loc(Location loc) {
        return world(loc.getWorld()).x(loc.getX()).y(loc.getY()).z(loc.getZ());
    }

    public LocationBuilder x(double x) {
        this.x = x;
        return this;
    }

    public LocationBuilder y(double y) {
        this.y = y;
        return this;
    }

    public LocationBuilder z(double z) {
        this.z = z;
        return this;
    }

    public LocationBuilder world(World world) {
        this.world = world;
        if (world != null) {
            this.worldName = world.getName();
        }
        return this;
    }

    public LocationBuilder world(String world) {
        this.worldName = world;
        this.world = Bukkit.getWorld(world);
        return this;
    }

    public LocationBuilder round() {
        return x(Math.floor(this.x)).y(Math.floor(this.y)).z(Math.floor(this.z));
    }

    public LocationBuilder centerXZ() {
        return x(Math.floor(this.x) + 0.5).z(Math.floor(this.z) + 0.5);
    }

    public LocationBuilder roundY() {
        return y(Math.floor(this.y));
    }

    public LocationBuilder incY(double inc) {
        return y(this.y + inc);
    }

    public LocationBuilder incX(double inc) {
        return x(this.x + inc);
    }

    public LocationBuilder incZ(double inc) {
        return z(this.z + inc);
    }

    public LocationBuilder inc(double inc) {
        return incX(inc).incY(inc).incZ(inc);
    }

    public Location toLoc() {
        return new Location(world, x, y, z);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public BlockLocation toBlockLoc() {
        return new BlockLocation(toLoc());
    }

    public LocationBuilder clone() {
        return new LocationBuilder().x(x).y(y).z(z).world(world).world(worldName);
    }

}
