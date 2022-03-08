package emanondev.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class PlayerSnapshot implements ConfigurationSerializable, Cloneable {

    private String locationWorldName;
    private Double locationX;
    private Double locationY;
    private Double locationZ;
    private Float locationYaw;
    private Float locationPitch;

    private List<ItemStack> armor;
    private List<ItemStack> inventory;
    private List<ItemStack> enderChest;
    private Integer level;
    private Float experience;
    private Collection<PotionEffect> effects;
    private Integer foodLevel;
    private Float exhaustion;
    private Float saturation;
    private Double health;
    private Double absorbition;
    private Boolean allowFlight;
    private Boolean god;
    private GameMode gameMode;
    private Float flySpeed;
    private Integer fireTicks;
    private Integer remainingAir;
    private Float walkSpeed;
    private Boolean flying;

    public PlayerSnapshot() {
    }

    public PlayerSnapshot(Player p, Collection<FieldType> types) {
        loadFrom(p, types);
    }

    public PlayerSnapshot(Player p) {
        this(p, FieldType.values());
    }

    public PlayerSnapshot(Player p, FieldType... types) {
        loadFrom(p, types);
    }

    public PlayerSnapshot clone() {
        //laziness
        return deserialize(serialize());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (locationWorldName != null)
            map.put("locationWorldName", locationWorldName);
        if (locationX != null)
            map.put("locationX", locationX);
        if (locationY != null)
            map.put("locationY", locationY);
        if (locationZ != null)
            map.put("locationZ", locationZ);
        if (locationYaw != null)
            map.put("locationYaw", locationYaw);
        if (locationPitch != null)
            map.put("locationPitch", locationPitch);
        if (armor != null)
            map.put("armor", armor);
        if (inventory != null)
            map.put("inventory", inventory);
        if (enderChest != null)
            map.put("enderChest", enderChest);
        if (level != null)
            map.put("level", level);
        if (experience != null)
            map.put("experience", experience);
        if (effects != null)
            map.put("effects", effects);
        if (foodLevel != null)
            map.put("foodLevel", foodLevel);
        if (exhaustion != null)
            map.put("exhaustion", exhaustion);
        if (saturation != null)
            map.put("saturation", saturation);
        if (health != null)
            map.put("health", health);
        if (absorbition != null)
            map.put("absorbition", absorbition);
        if (allowFlight != null)
            map.put("allowFlight", allowFlight);
        if (god != null)
            map.put("god", god);
        if (gameMode != null)
            map.put("gameMode", gameMode);
        if (flySpeed != null)
            map.put("flySpeed", flySpeed);
        if (fireTicks != null)
            map.put("fireTicks", fireTicks);
        if (remainingAir != null)
            map.put("remainingAir", remainingAir);
        if (walkSpeed != null)
            map.put("walkSpeed", walkSpeed);
        if (flying != null)
            map.put("flying", flying);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static PlayerSnapshot deserialize(Map<String, Object> map) {
        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.locationWorldName = (String) map.get("locationWorldName");
        snapshot.locationX = map.containsKey("locationX") ? ((Number) map.get("locationX")).doubleValue() : null;
        snapshot.locationY = map.containsKey("locationY") ? ((Number) map.get("locationY")).doubleValue() : null;
        snapshot.locationZ = map.containsKey("locationZ") ? ((Number) map.get("locationZ")).doubleValue() : null;
        snapshot.locationYaw = map.containsKey("locationYaw") ? ((Number) map.get("locationYaw")).floatValue() : null;
        snapshot.locationPitch = map.containsKey("locationPitch") ? ((Number) map.get("locationPitch")).floatValue()
                : null;
        snapshot.armor = (List<ItemStack>) map.get("armor");
        snapshot.inventory = (List<ItemStack>) map.get("inventory");
        snapshot.enderChest = (List<ItemStack>) map.get("enderChest");
        snapshot.level = (Integer) map.get("level");
        snapshot.experience = map.containsKey("experience") ? ((Number) map.get("experience")).floatValue() : null;
        snapshot.effects = (Collection<PotionEffect>) map.get("effects");
        snapshot.foodLevel = (Integer) map.get("foodLevel");
        snapshot.exhaustion = map.containsKey("exhaustion") ? ((Number) map.get("exhaustion")).floatValue() : null;
        snapshot.saturation = map.containsKey("saturation") ? ((Number) map.get("saturation")).floatValue() : null;
        snapshot.health = (Double) map.get("health");
        snapshot.absorbition = (Double) map.get("absorbition");
        snapshot.allowFlight = (Boolean) map.get("allowFlight");
        snapshot.god = (Boolean) map.get("god");
        snapshot.gameMode = (GameMode) map.get("gameMode");
        snapshot.flySpeed = map.containsKey("flySpeed") ? ((Number) map.get("flySpeed")).floatValue() : null;
        snapshot.fireTicks = (Integer) map.get("fireTicks");
        snapshot.remainingAir = map.containsKey("remainingAir") ? (Integer) map.get("remainingAir") : null;
        snapshot.walkSpeed = map.containsKey("walkSpeed") ? ((Number) map.get("walkSpeed")).floatValue() : null;
        snapshot.flying = map.containsKey("flying") ? (Boolean) map.get("flying") : null;
        return snapshot;
    }

    /**
     * LOCATION should be loaded before anything else for compatibility with plugins
     * which do manage inventory, EFFECTS should be loaded before ABSORBITION and HEALTH
     */
    private final FieldType[] LOAD_ORDER = new FieldType[]{FieldType.LOCATION, FieldType.EFFECTS,
            FieldType.ABSORBITION, FieldType.ARMOR, FieldType.INVENTORY, FieldType.ENDERCHEST, FieldType.LEVEL,
            FieldType.EXPERIENCE, FieldType.FOODLEVEL, FieldType.EXHAUSTION, FieldType.HEALTH, FieldType.ALLOWFLIGHT,
            FieldType.GOD, FieldType.GAMEMODE, FieldType.FLYSPEED, FieldType.FIRETICKS, FieldType.AIR,
            FieldType.WALKSPEED, FieldType.SATURATION};

    public void apply(Player who) {
        apply(who, EnumSet.allOf(FieldType.class));
    }

    public void apply(Player who, FieldType... fields) {
        EnumSet<FieldType> loadedTypes = EnumSet.noneOf(FieldType.class);
        for (FieldType type : fields)
            if (type != null)
                loadedTypes.add(type);
        apply(who, loadedTypes);
    }

    public void apply(Player who, Collection<FieldType> fields) {
        for (FieldType type : LOAD_ORDER)
            if (fields.contains(type))
                apply(who, type);
    }

    public void apply(Player who, FieldType type) {
        switch (type) {
            case ABSORBITION:
                if (this.absorbition != null)
                    who.setAbsorptionAmount(this.absorbition);
                return;
            case AIR:
                if (this.remainingAir != null)
                    who.setRemainingAir(remainingAir);
                return;
            case ALLOWFLIGHT:
                if (this.allowFlight != null)
                    who.setAllowFlight(allowFlight);
                return;
            case ARMOR:
                if (this.armor != null)
                    who.getInventory().setArmorContents(this.armor.toArray(new ItemStack[0]));
                return;
            case EFFECTS:
                if (this.effects != null) {
                    who.getActivePotionEffects().clear();
                    who.addPotionEffects(effects);
                }
                return;
            case ENDERCHEST:
                if (this.enderChest != null)
                    who.getEnderChest().setStorageContents(this.enderChest.toArray(new ItemStack[0]));
                return;
            case EXHAUSTION:
                if (this.exhaustion != null)
                    who.setExhaustion(exhaustion);
                return;
            case EXPERIENCE:
                if (this.experience != null)
                    who.setExp(this.experience);
                return;
            case FIRETICKS:
                if (this.fireTicks != null)
                    who.setFireTicks(this.fireTicks);
                return;
            case FLYING:
                if (this.flying != null)
                    who.setFlying(this.flying);
                return;
            case FLYSPEED:
                if (this.flySpeed != null)
                    who.setFlySpeed(this.flySpeed);
                return;
            case FOODLEVEL:
                if (this.foodLevel != null)
                    who.setFoodLevel(foodLevel);
                return;
            case GAMEMODE:
                if (this.gameMode != null)
                    who.setGameMode(this.gameMode);
                return;
            case GOD:
                if (god != null)
                    who.setInvulnerable(god);
                return;
            case HEALTH:
                if (this.health != null)
                    who.setHealth(health);
                return;
            case INVENTORY:
                if (this.inventory != null)
                    who.getInventory().setStorageContents(inventory.toArray(new ItemStack[0]));
                return;
            case LEVEL:
                if (level != null)
                    who.setLevel(level);
                return;
            case LOCATION:
                if (this.locationWorldName != null)
                    try {
                        who.teleport(new Location(Bukkit.getWorld(locationWorldName), this.locationX, this.locationY,
                                this.locationZ, this.locationYaw, this.locationPitch));
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO try fallback location?
                    }
                return;
            case SATURATION:
                if (this.saturation != null)
                    who.setSaturation(this.saturation);
                return;
            case WALKSPEED:
                if (this.walkSpeed != null)
                    who.setWalkSpeed(walkSpeed);
                return;
        }
    }

    public void loadFrom(Player who) {
        loadFrom(who, EnumSet.allOf(FieldType.class));
    }

    public void loadFrom(Player who, FieldType... fields) {
        EnumSet<FieldType> savedTypes = EnumSet.noneOf(FieldType.class);
        for (FieldType type : fields)
            if (type != null)
                savedTypes.add(type);
        loadFrom(who, savedTypes);
    }

    public void loadFrom(Player who, Collection<FieldType> fields) {
        for (FieldType type : fields)
            loadFrom(who, type);
    }

    public void loadFrom(Player who, FieldType type) {
        switch (type) {
            case ABSORBITION:
                this.absorbition = who.getAbsorptionAmount();
                return;
            case AIR:
                this.remainingAir = who.getRemainingAir();
                return;
            case ALLOWFLIGHT:
                this.allowFlight = who.getAllowFlight();
                return;
            case ARMOR:
                this.armor = new ArrayList<>(Arrays.asList(who.getInventory().getArmorContents()));
                for (int i = 0; i < this.armor.size(); i++) {
                    ItemStack el = this.armor.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.armor.set(i, null);
                }
                return;
            case EFFECTS:
                this.effects = new ArrayList<>(who.getActivePotionEffects());
                return;
            case ENDERCHEST:
                this.enderChest = new ArrayList<>(Arrays.asList(who.getEnderChest().getStorageContents()));
                for (int i = 0; i < this.enderChest.size(); i++) {
                    ItemStack el = this.enderChest.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.enderChest.set(i, null);
                }
                return;
            case EXHAUSTION:
                this.exhaustion = who.getExhaustion();
                return;
            case EXPERIENCE:
                this.experience = who.getExp();
                return;
            case FIRETICKS:
                this.fireTicks = who.getFireTicks();
                return;
            case FLYING:
                this.flying = who.isFlying();
                return;
            case FLYSPEED:
                this.flySpeed = who.getFlySpeed();
                return;
            case FOODLEVEL:
                this.foodLevel = who.getFoodLevel();
                return;
            case GAMEMODE:
                this.gameMode = who.getGameMode();
                return;
            case GOD:
                this.god = who.isInvulnerable();
                return;
            case HEALTH:
                this.health = who.getHealth();
                return;
            case INVENTORY:
                this.inventory = new ArrayList<>(Arrays.asList(who.getInventory().getStorageContents()));
                for (int i = 0; i < this.inventory.size(); i++) {
                    ItemStack el = this.inventory.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.inventory.set(i, null);
                }
                return;
            case LEVEL:
                this.level = who.getLevel();
                return;
            case LOCATION: {
                Location loc = who.getLocation();
                this.locationWorldName = loc.getWorld().getName();
                this.locationX = loc.getX();
                this.locationY = loc.getY();
                this.locationZ = loc.getZ();
                this.locationYaw = loc.getYaw();
                this.locationPitch = loc.getPitch();
            }
            return;
            case SATURATION:
                this.saturation = who.getSaturation();
                return;
            case WALKSPEED:
                this.walkSpeed = who.getWalkSpeed();
                return;
        }
    }

    public Object get(FieldType type) {
        switch (type) {
            case ABSORBITION:
                return getAbsorbition();
            case AIR:
                return getRemainingAir();
            case ALLOWFLIGHT:
                return getAllowFlight();
            case ARMOR:
                return getArmor();
            case EFFECTS:
                return getEffects();
            case ENDERCHEST:
                return getEnderChest();
            case EXHAUSTION:
                return getExhaustion();
            case EXPERIENCE:
                return getExperience();
            case FIRETICKS:
                return getFireTicks();
            case FLYING:
                return getFlying();
            case FLYSPEED:
                return getFlySpeed();
            case FOODLEVEL:
                return getFoodLevel();
            case GAMEMODE:
                return getGameMode();
            case GOD:
                return getGod();
            case HEALTH:
                return getHealth();
            case INVENTORY:
                return getInventory();
            case LEVEL:
                return getLevel();
            case LOCATION:
                return getLocation();
            case SATURATION:
                return getSaturation();
            case WALKSPEED:
                return getWalkSpeed();
        }
        throw new IllegalStateException();
    }

    @SuppressWarnings("unchecked")
    public void set(FieldType type, Object value) {
        switch (type) {
            case ABSORBITION:
                setAbsorbition((Double) value);
            case AIR:
                setRemainingAir((Integer) value);
            case ALLOWFLIGHT:
                setAllowFlight((Boolean) value);
            case ARMOR:
                setArmor((List<ItemStack>) value);
            case EFFECTS:
                setEffects((Collection<PotionEffect>) value);
            case ENDERCHEST:
                setEnderChest((List<ItemStack>) value);
            case EXHAUSTION:
                setExhaustion((Float) value);
            case EXPERIENCE:
                setExperience((Float) value);
            case FIRETICKS:
                setFireTicks((Integer) value);
            case FLYING:
                setFlying((Boolean) value);
            case FLYSPEED:
                setFlySpeed((Float) value);
            case FOODLEVEL:
                setFoodLevel((Integer) value);
            case GAMEMODE:
                setGameMode((GameMode) value);
            case GOD:
                setGod((Boolean) value);
            case HEALTH:
                setHealth((Double) value);
            case INVENTORY:
                setInventory((List<ItemStack>) value);
            case LEVEL:
                setLevel((Integer) value);
            case LOCATION:
                setLocation((Location) value);
            case SATURATION:
                setSaturation((Float) value);
            case WALKSPEED:
                setWalkSpeed((Float) value);
        }
        throw new IllegalStateException();
    }

    public void setAbsorbition(Double value) {
        this.absorbition = value;
    }

    public void setRemainingAir(Integer value) {
        this.remainingAir = value;
    }

    public void setAllowFlight(Boolean value) {
        this.allowFlight = value;
    }

    public void setArmor(List<ItemStack> value) {
        if (value != null && value.size() != 5)
            throw new IllegalArgumentException();
        this.armor = value == null ? null : new ArrayList<>(value);
    }

    public void setEffects(Collection<PotionEffect> value) {
        this.effects = value == null ? null : new ArrayList<>(value);
    }

    public void setEnderChest(List<ItemStack> value) {
        if (value != null && value.size() != 3 * 9)
            throw new IllegalArgumentException();
        this.enderChest = value == null ? null : new ArrayList<>(value);
    }

    public void setExhaustion(Float value) {
        this.exhaustion = value;
    }

    public void setExperience(Float value) {
        this.experience = value;
    }

    public void setFireTicks(Integer value) {
        this.fireTicks = value;
    }

    public void setFlying(Boolean value) {
        this.flying = value;
    }

    public void setFlySpeed(Float value) {
        this.flySpeed = value;
    }

    public void setFoodLevel(Integer value) {
        this.foodLevel = value;
    }

    public void setGameMode(GameMode value) {
        this.gameMode = value;
    }

    public void setGod(Boolean value) {
        this.god = value;
    }

    public void setHealth(Double value) {
        this.health = value;
    }

    public void setInventory(List<ItemStack> value) {
        if (value != null && value.size() != 4 * 9)
            throw new IllegalArgumentException();
        this.inventory = value == null ? null : new ArrayList<>(value);
    }

    public void setLevel(Integer value) {
        this.level = value;
    }

    public void setLocation(Location value) {
        this.locationWorldName = value.getWorld().getName();
        this.locationX = value.getX();
        this.locationY = value.getY();
        this.locationZ = value.getZ();
        this.locationYaw = value.getYaw();
        this.locationPitch = value.getPitch();
    }

    public void setSaturation(Float value) {
        this.saturation = value;
    }

    public void setWalkSpeed(Float value) {
        this.walkSpeed = value;
    }


    public Double getAbsorbition() {
        return this.absorbition;
    }

    public Integer getRemainingAir() {
        return this.remainingAir;
    }

    public Boolean getAllowFlight() {
        return this.allowFlight;
    }

    public List<ItemStack> getArmor() {
        if (this.armor == null)
            return null;
        return Collections.unmodifiableList(this.armor);
    }

    public Collection<PotionEffect> getEffects() {
        if (this.effects == null)
            return null;
        return Collections.unmodifiableCollection(this.effects);
    }

    public List<ItemStack> getEnderChest() {
        if (this.enderChest == null)
            return null;
        return Collections.unmodifiableList(this.enderChest);
    }

    public Float getExhaustion() {
        return this.exhaustion;
    }

    public Float getExperience() {
        return this.experience;
    }

    public Integer getFireTicks() {
        return this.fireTicks;
    }

    public Boolean getFlying() {
        return this.flying;
    }

    public Float getFlySpeed() {
        return this.flySpeed;
    }

    public Integer getFoodLevel() {
        return this.foodLevel;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public Boolean getGod() {
        return this.god;
    }

    public Double getHealth() {
        return this.health;
    }

    public List<ItemStack> getInventory() {
        if (this.inventory == null)
            return null;
        return Collections.unmodifiableList(this.inventory);
    }

    public Integer getLevel() {
        return this.level;
    }

    public Location getLocation() {
        if (this.locationWorldName == null)
            return null;
        World w = Bukkit.getWorld(this.locationWorldName);
        if (w == null)
            return null;
        return new Location(w, locationX,
                this.locationY,
                this.locationZ,
                this.locationYaw,
                this.locationPitch);
    }

    public Float getSaturation() {
        return this.saturation;
    }

    public Float getWalkSpeed() {
        return this.walkSpeed;
    }

    public enum FieldType {
        LOCATION, ARMOR, INVENTORY, ENDERCHEST, LEVEL, EXPERIENCE, EFFECTS, FOODLEVEL, EXHAUSTION, HEALTH, ABSORBITION,
        ALLOWFLIGHT, GOD, GAMEMODE, FLYSPEED, FIRETICKS, AIR, WALKSPEED, SATURATION, FLYING
    }

    public void fillEmpty() {
        fillEmpty(FieldType.values());
    }

    public void fillEmpty(FieldType... values) {
        for (FieldType type : values) {
            if (!contains(type))
                this.setDefault(type);
        }
    }

    public boolean contains(FieldType type) {
        switch (type) {
            case ABSORBITION:
                return this.absorbition != null;
            case AIR:
                return this.remainingAir != null;
            case ALLOWFLIGHT:
                return this.allowFlight != null;
            case ARMOR:
                return this.armor != null;
            case EFFECTS:
                return this.effects != null;
            case ENDERCHEST:
                return this.enderChest != null;
            case EXHAUSTION:
                return this.exhaustion != null;
            case EXPERIENCE:
                return this.experience != null;
            case FIRETICKS:
                return this.fireTicks != null;
            case FLYING:
                return this.flying != null;
            case FLYSPEED:
                return this.flySpeed != null;
            case FOODLEVEL:
                return this.foodLevel != null;
            case GAMEMODE:
                return this.gameMode != null;
            case GOD:
                return this.god != null;
            case HEALTH:
                return this.health != null;
            case INVENTORY:
                return this.inventory != null;
            case LEVEL:
                return this.level != null;
            case LOCATION:
                return this.locationWorldName != null;
            case SATURATION:
                return this.saturation != null;
            case WALKSPEED:
                return this.walkSpeed != null;
        }
        throw new IllegalStateException();
    }

    @SuppressWarnings("unchecked")
    public void setDefault(FieldType type) {
        switch (type) {
            case ABSORBITION:
                this.absorbition = (Double) getDefault(type);
                return;
            case AIR:
                this.remainingAir = (Integer) getDefault(type);
                return;
            case ALLOWFLIGHT:
                this.allowFlight = (Boolean) getDefault(type);
                return;
            case ARMOR:
                this.armor = (List<ItemStack>) getDefault(type);
                return;
            case EFFECTS:
                this.effects = (Collection<PotionEffect>) getDefault(type);
                return;
            case ENDERCHEST:
                this.enderChest = (List<ItemStack>) getDefault(type);
                return;
            case EXHAUSTION:
                this.exhaustion = (Float) getDefault(type);
                return;
            case EXPERIENCE:
                this.experience = (Float) getDefault(type);
                return;
            case FIRETICKS:
                this.fireTicks = (Integer) getDefault(type);
                return;
            case FLYING:
                this.flying = (Boolean) getDefault(type);
                return;
            case FLYSPEED:
                this.flySpeed = (Float) getDefault(type);
                return;
            case FOODLEVEL:
                this.foodLevel = (Integer) getDefault(type);
                return;
            case GAMEMODE:
                this.gameMode = (GameMode) getDefault(type);
                return;
            case GOD:
                this.god = (Boolean) getDefault(type);
                return;
            case HEALTH:
                this.health = (Double) getDefault(type);
                return;
            case INVENTORY:
                this.inventory = (List<ItemStack>) getDefault(type);
                return;
            case LEVEL:
                this.level = (Integer) getDefault(type);
                return;
            case LOCATION:
                Location loc = (Location) getDefault(type);
                this.locationWorldName = loc == null ? null : loc.getWorld().getName();
                this.locationX = loc == null ? null : loc.getX();
                this.locationY = loc == null ? null : loc.getY();
                this.locationZ = loc == null ? null : loc.getZ();
                this.locationYaw = loc == null ? null : loc.getYaw();
                this.locationPitch = loc == null ? null : loc.getPitch();
                return;
            case SATURATION:
                this.saturation = (Float) getDefault(type);
            case WALKSPEED:
                this.walkSpeed = (Float) getDefault(type);
        }
    }

    public Object getDefault(FieldType type) {
        switch (type) {
            case ABSORBITION:
            case EXPERIENCE:
            case FIRETICKS:
            case LEVEL:
                return 0;
            case AIR:
            case HEALTH:
            case FOODLEVEL:
                return 20;
            case ALLOWFLIGHT:
            case GOD:
            case FLYING:
                return false;
            case ARMOR:
                return Collections.nCopies(5, (ItemStack) null);
            case EFFECTS:
                return new ArrayList<PotionEffect>(0);
            case ENDERCHEST:
                return Collections.nCopies(9 * 3, (ItemStack) null);
            case EXHAUSTION:
            case WALKSPEED:
            case FLYSPEED:
            case SATURATION:
                return 1;
            case GAMEMODE:
                return GameMode.SURVIVAL;
            case INVENTORY:
                return Collections.nCopies(9 * 4, (ItemStack) null);
            case LOCATION:
                return null;// TODO fallback?
        }
        throw new IllegalStateException();
    }

}
