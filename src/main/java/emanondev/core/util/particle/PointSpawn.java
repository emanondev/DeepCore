package emanondev.core.util.particle;

import emanondev.core.util.location.LocationBuilder;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PointSpawn {

    private ParticleWrapper particle;
    private LocationBuilder loc;
    private LocationBuilder offset;
    private int count;
    private int extra;

    public PointSpawn(ParticleWrapper particle,
                      LocationBuilder loc, LocationBuilder offset,
                      int count, int extra) {
        this.particle = particle;
        this.loc = loc;
        this.offset = offset;
        this.count = count;
        this.extra = extra;
    }

    public PointSpawn(ParticleWrapper particle,
                      LocationBuilder loc, LocationBuilder offset,
                      int count) {
        this(particle, loc, offset, count, 0);
    }

    public PointSpawn(ParticleWrapper particle,
                      LocationBuilder loc, LocationBuilder offset) {
        this(particle, loc, offset, 1, 0);
    }

    public PointSpawn(ParticleWrapper particle,
                      LocationBuilder loc,
                      int count, int extra) {
        this(particle, loc, LocationBuilder.of(), count, extra);
    }

    public PointSpawn(ParticleWrapper particle,
                      LocationBuilder loc,
                      int count) {
        this(particle, loc, LocationBuilder.of(), count, 0);
    }

    public PointSpawn(ParticleWrapper particle,
                      LocationBuilder loc) {
        this(particle, loc, LocationBuilder.of(), 1, 0);
    }


    public void spawn(@NotNull Player p) {
        p.spawnParticle(particle.getParticle(),
                loc.getX(), loc.getY(), loc.getZ(), count,
                offset.getX(), offset.getY(), offset.getZ(),
                extra, particle.getData());
    }

    public void spawn(@NotNull Player p, World w) {
        if (Objects.equals(p.getWorld(), w)) {
            spawn(p);
        }
    }
}
