package emanondev.core.util.particle;

import emanondev.core.util.location.LocationBuilder;
import org.bukkit.util.Vector;


public class CuboidSpawn {

    private ParticleWrapper borders;
    private Vector min;
    private Vector max;
    private ParticleWrapper internal;
    private double bordersFrequency;


    private CuboidSpawn(ParticleWrapper borders,
                        Vector min,
                        Vector max,
                        ParticleWrapper internal,
                        double bordersFrequency) {
        this.borders = borders;
        this.min = min;
        this.max = max;
        this.internal = internal;
        this.bordersFrequency = bordersFrequency;

    }

    private CuboidSpawn of(ParticleWrapper borders,
                           LocationBuilder min,
                           LocationBuilder max,
                           ParticleWrapper internal,
                           double bordersFrequency) {
        return new CuboidSpawn(borders, min.toVector(), max.toVector(), internal, bordersFrequency);
    }
}
