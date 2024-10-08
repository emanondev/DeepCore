package emanondev.core;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs(value = "SoundInfo")
public class SoundInfo implements ConfigurationSerializable {

    private Sound sound;
    private float volume;
    private float pitch;
    private boolean selfSound;

    /**
     * Constructor for deserialization.
     *
     * @param map Map containing the object fields
     */
    public SoundInfo(@NotNull Map<String, Object> map) {
        this(getStringSound((String) map.get("sound")), (double) map.getOrDefault("volume", 1D),
                (double) map.getOrDefault("pitch", 1D), (boolean) map.getOrDefault("selfSound", false));
    }

    public SoundInfo(@Nullable Sound sound, double volume, double pitch, boolean selfSound) {
        this.sound = sound;
        this.volume = (float) Math.max(0.001, volume);
        this.pitch = (float) Math.max(0.001, pitch);
        this.selfSound = selfSound;
    }

    private static Sound getStringSound(String obj) {
        try {
            if (obj == null || obj.isEmpty())
                return null;
            return Sound.valueOf(obj);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static SoundInfo getSelfVillagerNo() {
        return new SoundInfo(Sound.ENTITY_VILLAGER_NO, 1, 1, true);
    }

    public static SoundInfo getSelfExperiencePickup() {
        return new SoundInfo(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1, true);
    }

    public void play(@NotNull Player p) {
        play(p.getLocation(), p);
    }

    public void play(@NotNull Location loc, @Nullable Player p) {
        if (sound == null)
            return;
        if (p == null)
            loc.getWorld().playSound(loc, sound, volume, pitch);
        else if (selfSound)
            p.playSound(p.getLocation(), sound, volume, pitch);
        else if (loc.getWorld().equals(p.getWorld()))
            loc.getWorld().playSound(loc, sound, volume, pitch);
    }

    public void play(@NonNull Location loc) {
        play(loc, null);
    }

    public @Nullable Sound getSound() {
        return sound;
    }

    public void setSound(@Nullable Sound sound) {
        this.sound = sound;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = (float) Math.max(0.001, volume);
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = (float) Math.max(0.001, pitch);
    }

    public boolean getSelfSound() {
        return selfSound;
    }

    public void setSelfSound(boolean selfSound) {
        this.selfSound = selfSound;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("sound", sound == null ? "" : sound.toString());
        map.put("volume", volume);
        map.put("pitch", pitch);
        map.put("selfSound", selfSound);
        return map;
    }

}
