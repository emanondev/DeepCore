package emanondev.core;

import java.util.*;

import javax.annotation.*;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs(value = "SoundInfo")
public class SoundInfo implements ConfigurationSerializable {
	
	public static SoundInfo getSelfVillagerNo() {
		return new SoundInfo(Sound.ENTITY_VILLAGER_NO,1,1,true);
	}
	
	private Sound sound;
	private float volume;
	private float pitch;
	private boolean selfSound;

	/**
	 * Constructor for deserialization.
	 * 
	 * @param map Map containing the object fields
	 */
	public SoundInfo(@Nonnull Map<String, Object> map) {
		this(getStringSound((String) map.get("sound")), (double) map.getOrDefault("volume", 1D),
				(double) map.getOrDefault("pitch", 1D), (boolean) map.getOrDefault("selfSound", false));
	}

	private static Sound getStringSound(String obj) {
		Sound sound = null;
		try {
			if (obj == null || obj.isEmpty())
				return sound;
			sound = Sound.valueOf(obj);
		} catch (Exception e) {
		}
		return sound;
	}

	public SoundInfo(@Nullable Sound sound, double volume, double pitch, boolean selfSound) {
		this.sound = sound;
		this.volume = (float) Math.max(0.001, volume);
		this.pitch = (float) Math.max(0.001, pitch);
		this.selfSound = selfSound;
	}

	public void play(@Nullable Player p) {
		play(p.getLocation(), p);
	}

	public void play(@Nonnull Location loc, @Nullable Player p) {
		if (loc == null || sound == null)
			return;
		if (p == null)
			loc.getWorld().playSound(loc, sound, volume, pitch);
		else if (selfSound)
			p.playSound(p.getLocation(), sound, volume, pitch);
		else if (loc.getWorld().equals(p.getWorld()))
			loc.getWorld().playSound(loc, sound, volume, pitch);
	}

	public void play(@Nullable Location loc) {
		play(loc, null);
	}

	public @Nullable Sound getSound() {
		return sound;
	}

	public double getVolume() {
		return volume;
	}

	public double getPitch() {
		return pitch;
	}

	public boolean getSelfSound() {
		return selfSound;
	}

	public void setSound(@Nullable Sound sound) {
		this.sound = sound;
	}

	public void setVolume(double volume) {
		this.volume = (float) Math.max(0.001, volume);
	}

	public void setPitch(double pitch) {
		this.pitch = (float) Math.max(0.001, pitch);
	}

	public void setSelfSound(boolean selfSound) {
		this.selfSound = selfSound;
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("sound", sound == null ? "" : sound.toString());
		map.put("volume", volume);
		map.put("pitch", pitch);
		map.put("selfSound", selfSound);
		return map;
	}

}
