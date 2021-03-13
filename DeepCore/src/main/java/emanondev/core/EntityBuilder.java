package emanondev.core;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Deprecated
public class EntityBuilder {

	private EntityType type;
	private EnumMap<Attribute, Double> attrubuteBase = new EnumMap<>(Attribute.class);
	private Boolean canPickupItems;
	private Boolean collidable;
	private Entity holder;
	private HashMap<PotionEffectType,PotionEffect> effects = new HashMap<>();
	private EntityData data;
	
	
	public EntityBuilder addPotionEffect(PotionEffect effect) {
		effects.put(effect.getType(), effect);
		return this;
	}

	public Entity spawn(Location loc) {
		Entity e = loc.getWorld().spawnEntity(loc, type);
		if (e instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) e;
			for (Attribute attr : attrubuteBase.keySet())
				le.getAttribute(attr).setBaseValue(attrubuteBase.get(attr));
			if (canPickupItems != null)
				le.setCanPickupItems(canPickupItems);
			if (collidable != null)
				le.setCollidable(collidable);
			if (holder != null)
				le.setLeashHolder(holder);
			if (maximumAir != null) {
				le.setMaximumAir(maximumAir);
				if (remainingAir == null)
					le.setRemainingAir(maximumAir);
				else
					le.setRemainingAir(remainingAir);
			}
			le.setRemainingAir((remainingAir == null) ? le.getMaximumAir() : remainingAir);
			le.setHealth((health == null) ? le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : health);

			if (ai != null)
				le.setAI(ai);
			if (removeWhenFarAway!=null)
				le.setRemoveWhenFarAway(removeWhenFarAway);
			le.setCustomName(customName);
			if (visibleCustomName!=null)
				le.setCustomNameVisible(visibleCustomName);
			for (PotionEffect effect:effects.values())
				le.addPotionEffect(effect, true);
			}
		if (data!=null)
			data.apply(e);
		return e;
	}

	/**
	 * incomplete / too much effort
	 * @param type
	 */
	public EntityBuilder(EntityType type) {
		if (type == null)
			throw new NullPointerException();
		if (!type.isAlive() || type == EntityType.PLAYER)
			throw new IllegalArgumentException();
		this.type = type;
		switch (type) {
		case BLAZE:
		case CAVE_SPIDER:
		case COD:
		case ELDER_GUARDIAN:
		case DOLPHIN:
		case GHAST:
		case GIANT:
		case GUARDIAN:
		case SALMON:
		case SPIDER:
		case SKELETON:
		case WITHER_SKELETON:
		case SILVERFISH:
		case SQUID:
		case STRAY:
			break;
		case ARMOR_STAND:
			//TODO
			break;
		case BAT:
			this.data = new BatData();
			break;
		case CAT:
			this.data = new CatData();
			break;
		case CHICKEN:
		case COW:
		case OCELOT:
		case POLAR_BEAR:
		case TURTLE:
			this.data = new AgeableData();
			break;
		case CREEPER:
			//TODO
			break;
		case DONKEY:
			//TODO
			break;
		case DROWNED:
		case ZOMBIE:
		case HUSK:
			//TODO
			break;
		case ENDERMAN:
			//TODO
			break;
		case ENDERMITE:
			//TODO
			break;
		case ENDER_DRAGON:
			//TODO
			break;
		case EVOKER:
		case ILLUSIONER:
			//TODO
			break;
		case FOX:
			this.data = new FoxData();
			break;
		case HORSE:
			//TODO
			break;
		case IRON_GOLEM:
			//TODO
			break;
		case LLAMA:
			//TODO
			break;
		case MAGMA_CUBE:
			//TODO
			break;
		case MULE:
			//TODO
			break;
		case MUSHROOM_COW:
			this.data = new MushroomCowData();
			break;
		case PANDA:
			this.data = new PandaData();
			break;
		case PARROT:
			//TODO
			break;
		case PHANTOM:
			//TODO
			break;
		case PIG:
			this.data = new PigData();
			break;
		case PILLAGER:
			break;
		case PUFFERFISH:
			//TODO
			break;
		case RABBIT:
			this.data = new RabbitData();
			break;
		case SHEEP:
			//TODO
			break;
		case SHULKER:
			//TODO
			break;
		case SKELETON_HORSE:
			break;
		case SLIME:
			break;
		case SNOWMAN:
			break;
		case TRADER_LLAMA:
			break;
		case TROPICAL_FISH:
			break;
		case VEX:
			break;
		case VILLAGER:
			break;
		case RAVAGER:
		case VINDICATOR:
		case WITCH:
			//TODO
			break;
		case WANDERING_TRADER:
			break;
		case WITHER:
			break;
		case WOLF:
			new WolfData();
			break;
		case ZOMBIE_HORSE:
			break;
		case ZOMBIE_VILLAGER:
			break;
		default:
			break;
		
		}
	}

	public EntityBuilder setAttributeBase(Attribute attr, double value) {
		attrubuteBase.put(attr, value);
		return this;
	}

	public EntityBuilder setCanPickupItems(Boolean value) {
		canPickupItems = value;
		return this;
	}

	public EntityBuilder setCollidable(Boolean value) {
		collidable = value;
		return this;
	}

	public EntityBuilder setLeashHolder(Entity holder) {
		this.holder = holder;
		return this;
	}

	private Integer maximumAir;

	public EntityBuilder setMaximumAir(Integer maximumAir) {
		this.maximumAir = maximumAir;
		return this;
	}

	private Boolean ai;

	public EntityBuilder setAI(Boolean ai) {
		this.ai = ai;
		return this;
	}

	private Integer remainingAir;

	public EntityBuilder setRemainingAir(Integer remainingAir) {
		this.remainingAir = remainingAir;
		return this;
	}

	private Boolean removeWhenFarAway;

	public EntityBuilder setRemoveWhenFarAway(Boolean removeWhenFarAway) {
		this.removeWhenFarAway = removeWhenFarAway;
		return this;
	}
/*
	private Double absorption;

	public EntityBuilder setAbsorptionAmount(Double absorption) {
		this.absorption = absorption;
		return this;
	}*/

	private Double health;

	public EntityBuilder setHealth(Double health) {
		this.health = health;
		return this;
	}

	private String customName;

	public EntityBuilder setCustomName(String customName) {
		this.customName = customName;
		return this;
	}

	private Boolean visibleCustomName;

	public EntityBuilder setCustomName(Boolean visibleCustomName) {
		this.visibleCustomName = visibleCustomName;
		return this;
	}
	
	public EntityBuilder setPigSaddle(Boolean value) {
		if (data instanceof PigData)
			((PigData) data).setSaddle(value);
		return this;
	}
	public EntityBuilder setBreed(Boolean value) {
		if (data instanceof AgeableData)
			((AgeableData) data).setBreed(value);
		return this;
	}
	public EntityBuilder setBaby() {
		if (data instanceof AgeableData)
			((AgeableData) data).setBaby();
		return this;
	}
	public EntityBuilder setAdult() {
		if (data instanceof AgeableData)
			((AgeableData) data).setAdult();
		return this;
	}
	
	private abstract class EntityData {
		public abstract void apply(Entity e);
	}
	private class AgeableData extends EntityData {
		private Boolean breed;
		private Boolean baby;
		
		public void setBreed(Boolean breed) {
			this.breed = breed;
		}
		public void setBaby() {
			baby = true;
		}
		public void setAdult() {
			baby = false;
		}
		public void apply(Entity e) {
			if (e instanceof Ageable) {
				if (breed!=null)
					((Ageable) e).setBreed(breed);
				if (baby!=null)
					if (baby)
						((Ageable) e).setBaby();
					else
						((Ageable) e).setAdult();
			}
		}
	}
	private class PigData extends AgeableData {
		private Boolean saddle;
		public void setSaddle(Boolean saddle) {
			this.saddle = saddle;
		}
		public void apply(Entity e) {
			super.apply(e);
			if ((e instanceof Pig)&&saddle!=null)
				((Pig) e).setSaddle(true);
		}
	}
	public EntityBuilder setSitting(Boolean value) {
		if (data instanceof SittableData)
			((SittableData) data).setSitting(value);
		return this;
	}
	private class SittableData extends AgeableData {
		private Boolean sitting;
		public void setSitting(Boolean sitting) {
			this.sitting = sitting;
		}
		public void apply(Entity e) {
			super.apply(e);
			if ((e instanceof Sittable)&&sitting!=null)
				((Sittable) e).setSitting(sitting);
		}
	}
	public EntityBuilder setFoxType(Fox.Type type) {
		if (data instanceof FoxData)
			((FoxData) data).setType(type);
		return this;
	}
	public EntityBuilder setFoxSleeping(Boolean value) {
		if (data instanceof FoxData)
			((FoxData) data).setSleeping(value);
		return this;
	}
	public EntityBuilder setFoxCrouching(Boolean value) {
		if (data instanceof FoxData)
			((FoxData) data).setCrouching(value);
		return this;
	}
	private class FoxData extends SittableData {
		private Fox.Type type;
		private Boolean crouching;
		private Boolean sleeping;
		public void setCrouching (Boolean crouching) {
			this.crouching = crouching;
		}
		public void setType(Fox.Type type) {
			this.type = type;
		}
		public void setSleeping(Boolean sleeping) {
			this.sleeping = sleeping;
		}
		public void apply(Entity e) {
			super.apply(e);
			if (e instanceof Fox) {
				if(type!=null)
					((Fox) e).setFoxType(type);
				if(crouching!=null)
					((Fox) e).setCrouching(crouching);
				if(sleeping!=null)
					((Fox) e).setSleeping(sleeping);
			}
		}
	}
	public EntityBuilder setCatType(Cat.Type type) {
		if (data instanceof CatData)
			((CatData) data).setType(type);
		return this;
	}
	private class CatData extends SittableData {
		private Cat.Type type;
		private DyeColor color;
		public void setCollarColor(DyeColor color) {
			this.color = color;
		}
		public void setType(Cat.Type type) {
			this.type = type;
		}
		public void apply(Entity e) {
			super.apply(e);
			if (e instanceof Cat) {
				if (type!=null)
					((Cat) e).setCatType(type);
				if (color!=null)
					((Cat) e).setCollarColor(color);
			}
		}
	}
 	public EntityBuilder setCollarColor(DyeColor color) {
 		if (data instanceof CatData)
 			((CatData) data).setCollarColor(color);
 		else if (data instanceof WolfData)
 			((WolfData) data).setCollarColor(color);
 		return this;
 	}
	public EntityBuilder setBatAwake(Boolean awake) {
		if (data instanceof BatData)
			((BatData) data).setAwake(awake);
		return this;
	}
	private class BatData extends EntityData {
		private Boolean awake;
		public void setAwake(Boolean awake) {
			this.awake = awake;
		}
		public void apply(Entity e) {
			if ((e instanceof Bat)&&awake!=null)
				((Bat) e).setAwake(awake);
			
		}
	}
	public EntityBuilder setWolfAngry(Boolean angry) {
		if (data instanceof WolfData)
			((WolfData) data).setAngry(angry);
		return this;
	}
	private class WolfData extends SittableData {
		private Boolean angry;
		private DyeColor color;
		public void setCollarColor(DyeColor color) {
			this.color = color;
		}
		public void setAngry(Boolean angry) {
			this.angry = angry;
		}
		public void apply(Entity e) {
			super.apply(e);
			if (e instanceof Wolf) {
				if (type!=null)
					((Wolf) e).setAngry(angry);
				if (color!=null)
					((Wolf) e).setCollarColor(color);
			}
		}
	}

	public EntityBuilder setVariant(MushroomCow.Variant type) {
		if (data instanceof MushroomCow)
			((MushroomCowData) data).setVariant(type);
		return this;
	}
	private class MushroomCowData extends AgeableData {
		private MushroomCow.Variant type;
		public void setVariant(MushroomCow.Variant type) {
			this.type = type;
		}
		public void apply(Entity e) {
			super.apply(e);
			if ((e instanceof MushroomCow)&&type!=null)
					((MushroomCow) e).setVariant(type);
		}
	}
	public EntityBuilder setRabbitType(Rabbit.Type type) {
		if (data instanceof Rabbit)
			((RabbitData) data).setType(type);
		return this;
	}
	private class RabbitData extends AgeableData {
		private Rabbit.Type type;
		public void setType(Rabbit.Type type) {
			this.type = type;
		}
		public void apply(Entity e) {
			super.apply(e);
			if ((e instanceof Rabbit)&&type!=null)
					((Rabbit) e).setRabbitType(type);
		}
	}
	public EntityBuilder setPandaMainGene(Panda.Gene type) {
		if (data instanceof Panda)
			((PandaData) data).setMainGene(type);
		return this;
	}
	public EntityBuilder setPandaHiddenGene(Panda.Gene type) {
		if (data instanceof Panda)
			((PandaData) data).setHiddenGene(type);
		return this;
	}
	private class PandaData extends AgeableData {
		private Panda.Gene main;
		private Panda.Gene hidden;
		public void setMainGene(Panda.Gene type) {
			this.main = type;
		}
		public void setHiddenGene(Panda.Gene type) {
			this.hidden = type;
		}
		public void apply(Entity e) {
			super.apply(e);
			if (e instanceof Panda) {
				if (main!=null)
					((Panda) e).setMainGene(main);
				if (hidden!=null)
					((Panda) e).setHiddenGene(hidden);
			}
		}
	}

}
