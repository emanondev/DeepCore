package emanondev.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UtilsPlayer {

	public final PlayerBackup[] INVENTORIES = new PlayerBackup[] { PlayerBackup.ARMOR, PlayerBackup.INVENTORY,
			PlayerBackup.ENDERCHEST };

	public final PlayerBackup[] EFFECTS = new PlayerBackup[] { PlayerBackup.EFFECTS, PlayerBackup.ABSORBITION };
	public final PlayerBackup[] ALL_BUT_LOCATION = new PlayerBackup[] { PlayerBackup.ARMOR, PlayerBackup.INVENTORY,
			PlayerBackup.ENDERCHEST, PlayerBackup.LEVEL, PlayerBackup.EXPERIENCE, PlayerBackup.EFFECTS,
			PlayerBackup.FOODLEVEL, PlayerBackup.EXHAUSTION, PlayerBackup.HEALTH, PlayerBackup.ABSORBITION,
			PlayerBackup.ALLOWFLIGHT, PlayerBackup.GOD, PlayerBackup.GAMEMODE, PlayerBackup.FLYSPEED,
			PlayerBackup.FIRETICKS, PlayerBackup.AIR, PlayerBackup.WALKSPEED, PlayerBackup.SATURATION
	};

	public static void storePlayerStuff(YMLSection section, Player who) {
		storePlayerStuff(section,who,PlayerBackup.values());
	}
	
	public static void storePlayerStuff(YMLSection section, Player who, PlayerBackup[] types) {
		for (PlayerBackup type: types)
			type.store(section, who);
		section.save();
	}

	@SuppressWarnings("unchecked")
	public static void loadPlayerStuff(YMLSection section, Player who, boolean teleport) {
	}

	public static void clearPlayerStuffs(Player player) {
		for (PotionEffectType type : PotionEffectType.values())
			player.removePotionEffect(type);
		player.getInventory().clear();
		player.getEquipment().clear();
		player.setHealth(20);
		player.setLevel(0);
		player.setExp(0);
		player.setFoodLevel(20);
		player.setExhaustion(1);
		player.setSaturation(1);
	}

	public enum PlayerBackup {
		LOCATION("location"), // LOCATION must be first
		ARMOR("inventory.armor"), INVENTORY("inventory.content"), ENDERCHEST("inventory.enderchest"), LEVEL("level"),
		EXPERIENCE("experience"), EFFECTS("effects"), FOODLEVEL("foodlevel"), EXHAUSTION("exhaustion"),
		HEALTH("health"), ABSORBITION("absorbition"), ALLOWFLIGHT("allowflight"), GOD("god"), GAMEMODE("gamemode"),
		FLYSPEED("flyspeed"), FIRETICKS("fireticks"), AIR("air"), WALKSPEED("walkspeed"), SATURATION("saturation");

		private String subPath;

		private PlayerBackup(String path) {
			this.subPath = path;
		}

		@SuppressWarnings({ "deprecation", "unchecked" })
		public void load(YMLSection section, Player who) {
			switch (this) {
			case ABSORBITION:
				who.setExhaustion((float) section.getDouble(this.subPath, 1));
				return;
			case AIR:
				who.setRemainingAir(section.getInt(this.subPath, 20));
				return;
			case ALLOWFLIGHT:
				who.setAllowFlight(section.getBoolean(this.subPath, false));
				return;
			case ARMOR: {
				ItemStack[] list = null;
				try {
					list = (ItemStack[]) section.get(this.subPath);
				} catch (Exception ex) {
					try {
						list = ((List<ItemStack>) section.get(this.subPath)).toArray(new ItemStack[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (list == null)
					list = new ItemStack[who.getEquipment().getArmorContents().length];
				who.getInventory().setArmorContents(list);
				return;
			}
			case EFFECTS:
				Collection<PotionEffect> effects = null;
				try {
					effects = (Collection<PotionEffect>) section.get(this.subPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (effects != null)
					who.addPotionEffects(effects);
				else
					who.getActivePotionEffects().clear();
				return;
			case ENDERCHEST: {
				ItemStack[] list = null;
				try {
					list = (ItemStack[]) section.get(this.subPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (list == null)
					list = new ItemStack[who.getEnderChest().getStorageContents().length];
				who.getEnderChest().setStorageContents(list);
				return;
			}
			case EXHAUSTION:
				who.setExhaustion((float) section.getDouble(this.subPath, 1));
				return;
			case EXPERIENCE:
				who.setExp((float) section.getDouble(this.subPath, 0));
				return;
			case FIRETICKS:
				who.setFireTicks(section.getInt(this.subPath, 0));
				return;
			case FLYSPEED:
				who.setFlySpeed((float) section.getDouble(this.subPath, 1));
				return;
			case FOODLEVEL:
				who.setFoodLevel(section.getInteger(this.subPath, 20));
				return;
			case GAMEMODE:
				try {
					who.setGameMode((GameMode) section.get(this.subPath, GameMode.SURVIVAL));
				} catch (Exception e) {
					who.setGameMode(GameMode.SURVIVAL);
				}
				return;
			case GOD:
				who.setInvulnerable(section.getBoolean(this.subPath, false));
				return;
			case HEALTH:
				who.setHealth((double) section.getDouble(this.subPath, 20));
				return;
			case INVENTORY:{
				ItemStack[] list = null;
				try {
					list = (ItemStack[]) section.get(this.subPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (list == null)
					list = new ItemStack[who.getInventory().getStorageContents().length];
				who.getInventory().setStorageContents(list);
				return;
			}
			case LEVEL:
				who.setLevel(section.getInteger(this.subPath, 0));
				return;
			case LOCATION:
				who.teleport(section.getLocation(this.subPath));
				return;
			case WALKSPEED:
				who.setWalkSpeed((float) section.getDouble(this.subPath, 1));
				return;
			case SATURATION:
				who.setSaturation((float) section.getDouble(this.subPath, 1));
			default:
				throw new IllegalStateException();
			}
		}

		public void store(YMLSection section, Player who) {
			switch (this) {
			case ABSORBITION:
				section.set(this.subPath, who.getAbsorptionAmount());
				return;
			case AIR:
				section.set(this.subPath, who.getRemainingAir());
				return;
			case ALLOWFLIGHT:
				section.set(this.subPath, who.getAllowFlight());
				return;
			case ARMOR:
				section.set(this.subPath, who.getEquipment().getArmorContents());
				return;
			case EFFECTS:
				section.set(this.subPath, who.getActivePotionEffects());
				return;
			case ENDERCHEST:
				section.set(this.subPath, who.getEnderChest().getStorageContents());
				return;
			case EXHAUSTION:
				section.set(this.subPath, who.getExhaustion());
				return;
			case EXPERIENCE:
				section.set(this.subPath, who.getExp());
				return;
			case FIRETICKS:
				section.set(this.subPath, who.getFireTicks());
				return;
			case FLYSPEED:
				section.get(this.subPath, who.getFlySpeed());
				return;
			case FOODLEVEL:
				section.set(this.subPath, who.getFoodLevel());
				return;
			case GAMEMODE:
				section.set(this.subPath, who.getGameMode());
				return;
			case GOD:
				section.set(this.subPath, who.isInvulnerable());
				return;
			case HEALTH:
				section.set(this.subPath, who.getHealth());
				return;
			case INVENTORY:
				section.set(this.subPath, who.getInventory().getStorageContents());
				return;
			case LEVEL:
				section.set(this.subPath, who.getLevel());
				return;
			case LOCATION:
				section.set(this.subPath, who.getLocation());
				return;
			case WALKSPEED:
				section.set(this.subPath, who.getWalkSpeed());
				return;
			case SATURATION:
				section.set(this.subPath, who.getSaturation());
			default:
				throw new IllegalStateException("invalid type " + this.name());
			}
		}
		
		public void setDefault(Player who) {
			switch (this) {
			case ABSORBITION:
				who.setExhaustion( 1);
				return;
			case AIR:
				who.setRemainingAir( 20);
				return;
			case ALLOWFLIGHT:
				who.setAllowFlight(false);
				return;
			case ARMOR: 
				who.getInventory().clear();
				return;
			case EFFECTS:
					who.getActivePotionEffects().clear();
				return;
			case ENDERCHEST: 
				who.getEnderChest().clear();
				return;
			case EXHAUSTION:
				who.setExhaustion( 1);
				return;
			case EXPERIENCE:
				who.setExp( 0);
				return;
			case FIRETICKS:
				who.setFireTicks( 0);
				return;
			case FLYSPEED:
				who.setFlySpeed( 1);
				return;
			case FOODLEVEL:
				who.setFoodLevel( 20);
				return;
			case GAMEMODE:
				who.setGameMode(GameMode.SURVIVAL);
				return;
			case GOD:
				who.setInvulnerable( false);
				return;
			case HEALTH:
				who.setHealth( 20);
				return;
			case INVENTORY:
				who.getInventory().clear();
				return;
			case LEVEL:
				who.setLevel( 0);
				return;
			case LOCATION:
				who.teleport(C.fallbackLocation());//TODO
				return;
			case WALKSPEED:
				who.setWalkSpeed(1);
				return;
			case SATURATION:
				who.setSaturation(1);
			default:
				throw new IllegalStateException();
			}
		}
	}

}
