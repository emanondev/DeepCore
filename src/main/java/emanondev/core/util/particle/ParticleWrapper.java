package emanondev.core.util.particle;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

@Getter
public class ParticleWrapper {

    private final Particle particle;
    private final Object data;

    private ParticleWrapper(Particle particle, Object data) {
        this.particle = particle;
        this.data = data;
    }

    public ParticleWrapper of(Particle particle) {
        if (particle.getDataType() != Void.class) {
            throw new RuntimeException();
        }
        return new ParticleWrapper(particle, null);
    }

    // --- GEYSER ---
    public ParticleWrapper ofGeyser(int data) {
        return new ParticleWrapper(Particle.GEYSER, data);
    }

    public ParticleWrapper ofGeyserBase(Particle.GeyserBase data) {
        return new ParticleWrapper(Particle.GEYSER_BASE, data);
    }

    public ParticleWrapper ofGeyserPoof(Particle.GeyserBase data) {
        return new ParticleWrapper(Particle.GEYSER_POOF, data);
    }

    public ParticleWrapper ofGeyserPlume(int data) {
        return new ParticleWrapper(Particle.GEYSER_PLUME, data);
    }

    // --- SPELL / EFFECT ---
    public ParticleWrapper ofEffect(Color color, float power) {
        return new ParticleWrapper(Particle.EFFECT, new Particle.Spell(color, power));
    }

    public ParticleWrapper ofEffect(DyeColor color, float power) {
        return ofEffect(color.getColor(), power);
    }

    public ParticleWrapper ofInstantEffect(Color color, float power) {
        return new ParticleWrapper(Particle.INSTANT_EFFECT, new Particle.Spell(color, power));
    }

    public ParticleWrapper ofInstantEffect(DyeColor color, float power) {
        return ofEffect(color.getColor(), power);
    }

    public ParticleWrapper ofEntityEffect(Color data) {
        return new ParticleWrapper(Particle.ENTITY_EFFECT, data);
    }

    public ParticleWrapper ofEntityEffect(DyeColor data) {
        return ofEntityEffect(data.getColor());
    }

    // --- DUST ---
    public ParticleWrapper ofDust(Color color, float size) {
        return new ParticleWrapper(Particle.DUST, new Particle.DustOptions(color, size));
    }

    public ParticleWrapper ofDust(Color color) {
        return ofDust(color, 1F);
    }

    public ParticleWrapper ofDust(DyeColor color, float size) {
        return ofDust(color.getColor(), size);
    }

    public ParticleWrapper ofDust(DyeColor color) {
        return ofDust(color.getColor(), 1F);
    }

    // --- ITEM ---
    public ParticleWrapper ofItem(ItemStack data) {
        return new ParticleWrapper(Particle.ITEM, data);
    }

    public ParticleWrapper ofItem(Material data) {
        return new ParticleWrapper(Particle.ITEM, new ItemStack(data));
    }

    // --- BLOCK / DUST VARIANTS ---
    public ParticleWrapper ofBlock(BlockData data) {
        return new ParticleWrapper(Particle.BLOCK, data);
    }

    public ParticleWrapper ofBlock(Material data) {
        return ofBlock(data.createBlockData());
    }

    public ParticleWrapper ofBlockMarker(BlockData data) {
        return new ParticleWrapper(Particle.BLOCK_MARKER, data);
    }

    public ParticleWrapper ofBlockMarker(Material blockData) {
        return ofBlockMarker(blockData.createBlockData());
    }

    public ParticleWrapper ofFallingDust(BlockData data) {
        return new ParticleWrapper(Particle.FALLING_DUST, data);
    }

    public ParticleWrapper ofDustPillar(BlockData data) {
        return new ParticleWrapper(Particle.DUST_PILLAR, data);
    }

    public ParticleWrapper ofBlockCrumble(BlockData data) {
        return new ParticleWrapper(Particle.BLOCK_CRUMBLE, data);
    }

    // --- OTHER PARTICLES ---
    public ParticleWrapper ofDragonBreath(float data) {
        return new ParticleWrapper(Particle.DRAGON_BREATH, data);
    }

    public ParticleWrapper ofFlash(Color data) {
        return new ParticleWrapper(Particle.FLASH, data);
    }

    public ParticleWrapper ofFlash(DyeColor data) {
        return ofFlash(data.getColor());
    }

    public ParticleWrapper ofDustTransition(Color from, Color to, float size) {
        return new ParticleWrapper(Particle.DUST_COLOR_TRANSITION,
                new Particle.DustTransition(from, to, size));
    }

    public ParticleWrapper ofDustTransition(DyeColor from, DyeColor to, float size) {
        return ofDustTransition(from.getColor(), to.getColor(), size);
    }

    public ParticleWrapper ofDustTransition(Color from, Color to) {
        return ofDustTransition(from, to, 1f);
    }

    public ParticleWrapper ofDustTransition(DyeColor from, DyeColor to) {
        return ofDustTransition(from, to, 1f);
    }

    public ParticleWrapper ofVibration(Vibration data) {
        return new ParticleWrapper(Particle.VIBRATION, data);
    }

    public ParticleWrapper ofSculkCharge(float data) {
        return new ParticleWrapper(Particle.SCULK_CHARGE, data);
    }

    public ParticleWrapper ofShriek(int data) {
        return new ParticleWrapper(Particle.SHRIEK, data);
    }

    public ParticleWrapper ofTintedLeaves(Color data) {
        return new ParticleWrapper(Particle.TINTED_LEAVES, data);
    }

    public ParticleWrapper ofTintedLeaves(DyeColor data) {
        return ofTintedLeaves(data.getColor());
    }

    public ParticleWrapper ofTrail(Particle.Trail data) {
        return new ParticleWrapper(Particle.TRAIL, data);
    }
}
