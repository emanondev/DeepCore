package emanondev.core;

import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerSnapshot implements ConfigurationSerializable, Cloneable {

    /**
     * LOCATION should be loaded before anything else for compatibility with plugins
     * which do manage inventory, EFFECTS should be loaded before ABSORBITION and HEALTH
     */
    private final FieldType[] LOAD_ORDER = new FieldType[]{FieldType.LOCATION, FieldType.EFFECTS,
            FieldType.ABSORBITION, FieldType.ARMOR, FieldType.EXTRACONTENTS, FieldType.INVENTORY, FieldType.ENDERCHEST, FieldType.LEVEL,
            FieldType.EXPERIENCE, FieldType.FOODLEVEL, FieldType.EXHAUSTION, FieldType.HEALTH, FieldType.ALLOWFLIGHT,
            FieldType.GOD, FieldType.GAMEMODE, FieldType.FLYSPEED, FieldType.FIRETICKS, FieldType.AIR,
            FieldType.WALKSPEED, FieldType.SATURATION, /*FieldType.INVISIBLE,*/ FieldType.COLLIDABLE, FieldType.CANPICKUPITEMS,
            FieldType.HELDITEMSLOT, FieldType.FREEZETICKS, FieldType.GLOWING};
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
    private Boolean collidable;
    private Boolean canPickupItems;
    private GameMode gameMode;
    private Float flySpeed;
    private Integer fireTicks;
    private Integer remainingAir;
    private Float walkSpeed;
    private Boolean flying;
    private List<ItemStack> extraContents;
    //private Boolean invisible;
    private Integer heldItemSlot;
    private Integer freezeTicks;
    private Boolean glowing;

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
        snapshot.extraContents = (List<ItemStack>) map.get("extraContents");
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
        try {
            String tmp = (String) map.get("gameMode");
            snapshot.gameMode = tmp == null ? null : GameMode.valueOf(tmp);
        } catch (Exception e) {
            snapshot.gameMode = null;
        }
        snapshot.flySpeed = map.containsKey("flySpeed") ? ((Number) map.get("flySpeed")).floatValue() : null;
        snapshot.fireTicks = (Integer) map.get("fireTicks");
        snapshot.remainingAir = map.containsKey("remainingAir") ? (Integer) map.get("remainingAir") : null;
        snapshot.walkSpeed = map.containsKey("walkSpeed") ? ((Number) map.get("walkSpeed")).floatValue() : null;
        snapshot.flying = map.containsKey("flying") ? (Boolean) map.get("flying") : null;
        //snapshot.invisible = map.containsKey("invisible") ? (Boolean) map.get("invisible") : null;
        snapshot.heldItemSlot = map.containsKey("heldItemSlot") ? (Integer) map.get("heldItemSlot") : null;
        snapshot.freezeTicks = map.containsKey("freezeTicks") ? (Integer) map.get("freezeTicks") : null;
        snapshot.glowing = map.containsKey("glowing") ? (Boolean) map.get("glowing") : null;
        snapshot.collidable = map.containsKey("collidable") ? (Boolean) map.get("collidable") : null;
        snapshot.canPickupItems = map.containsKey("canPickupItems") ? (Boolean) map.get("canPickupItems") : null;
        return snapshot;
    }

    public void loadFrom(Player who, Collection<FieldType> fields) {
        for (FieldType type : fields)
            loadFrom(who, type);
    }

    public void loadFrom(Player who, FieldType type) {
        switch (type) {
            case ABSORBITION -> this.absorbition = who.getAbsorptionAmount();
            case AIR -> this.remainingAir = who.getRemainingAir();
            case ALLOWFLIGHT -> this.allowFlight = who.getAllowFlight();
            case ARMOR -> {
                this.armor = new ArrayList<>(Arrays.asList(who.getInventory().getArmorContents()));
                for (int i = 0; i < this.armor.size(); i++) {
                    ItemStack el = this.armor.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.armor.set(i, null);
                }
            }
            case EXTRACONTENTS -> {
                this.extraContents = new ArrayList<>(Arrays.asList(who.getInventory().getExtraContents()));
                for (int i = 0; i < this.extraContents.size(); i++) {
                    ItemStack el = this.extraContents.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.extraContents.set(i, null);
                }
            }
            case EFFECTS -> this.effects = new ArrayList<>(who.getActivePotionEffects());
            case ENDERCHEST -> {
                this.enderChest = new ArrayList<>(Arrays.asList(who.getEnderChest().getStorageContents()));
                for (int i = 0; i < this.enderChest.size(); i++) {
                    ItemStack el = this.enderChest.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.enderChest.set(i, null);
                }
            }
            case EXHAUSTION -> this.exhaustion = who.getExhaustion();
            case EXPERIENCE -> this.experience = who.getExp();
            case FIRETICKS -> this.fireTicks = who.getFireTicks();
            case FLYING -> this.flying = who.isFlying();
            case FLYSPEED -> this.flySpeed = who.getFlySpeed();
            case FOODLEVEL -> this.foodLevel = who.getFoodLevel();
            case GAMEMODE -> this.gameMode = who.getGameMode();
            case GOD -> this.god = who.isInvulnerable();
            case HEALTH -> this.health = who.getHealth();
            case INVENTORY -> {
                this.inventory = new ArrayList<>(Arrays.asList(who.getInventory().getStorageContents()));
                for (int i = 0; i < this.inventory.size(); i++) {
                    ItemStack el = this.inventory.get(i);
                    if (el == null)
                        continue;
                    if (el.getType() == Material.AIR)
                        this.inventory.set(i, null);
                }
            }
            case LEVEL -> this.level = who.getLevel();
            case LOCATION -> {
                Location loc = who.getLocation();
                this.locationWorldName = loc.getWorld().getName();
                this.locationX = loc.getX();
                this.locationY = loc.getY();
                this.locationZ = loc.getZ();
                this.locationYaw = loc.getYaw();
                this.locationPitch = loc.getPitch();
            }
            case SATURATION -> this.saturation = who.getSaturation();
            case WALKSPEED -> this.walkSpeed = who.getWalkSpeed();
            //case INVISIBLE -> this.invisible = who.isInvisible();
            case HELDITEMSLOT -> this.heldItemSlot = who.getInventory().getHeldItemSlot();
            case FREEZETICKS -> this.freezeTicks = who.getFreezeTicks();
            case GLOWING -> this.glowing = who.isGlowing();
            case COLLIDABLE -> this.collidable = who.isCollidable();
            case CANPICKUPITEMS -> this.canPickupItems = who.getCanPickupItems();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public void loadFrom(Player who, FieldType... fields) {
        EnumSet<FieldType> savedTypes = EnumSet.noneOf(FieldType.class);
        for (FieldType type : fields)
            if (type != null)
                savedTypes.add(type);
        loadFrom(who, savedTypes);
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
        if (extraContents != null)
            map.put("extraContents", extraContents);
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
            map.put("gameMode", gameMode.name());
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
        if (heldItemSlot != null)
            map.put("heldItemSlot", heldItemSlot);
        if (freezeTicks != null)
            map.put("freezeTicks", freezeTicks);
        if (glowing != null)
            map.put("glowing", glowing);
        if (collidable != null)
            map.put("collidable", collidable);
        if (canPickupItems != null)
            map.put("canPickupItems", canPickupItems);
        return map;
    }

    public void apply(Player who) {
        apply(who, EnumSet.allOf(FieldType.class));
    }

    public void apply(Player who, Collection<FieldType> fields) {
        for (FieldType type : LOAD_ORDER)
            if (fields.contains(type))
                apply(who, type);
    }

    public void apply(Player who, FieldType type) {
        switch (type) {
            case ABSORBITION -> {
                if (this.absorbition != null)
                    who.setAbsorptionAmount(this.absorbition);
            }
            case AIR -> {
                if (this.remainingAir != null)
                    who.setRemainingAir(remainingAir);
            }
            case ALLOWFLIGHT -> {
                if (this.allowFlight != null)
                    who.setAllowFlight(allowFlight);
            }
            case ARMOR -> {
                if (this.armor != null)
                    who.getInventory().setArmorContents(this.armor.toArray(new ItemStack[0]));
            }
            case EXTRACONTENTS -> {
                if (this.extraContents != null)
                    who.getInventory().setExtraContents(this.extraContents.toArray(new ItemStack[0]));
            }
            case EFFECTS -> {
                if (this.effects != null) {
                    for (PotionEffect value : new ArrayList<>(who.getActivePotionEffects())) //TODO maybe no need of copy, test this
                        who.removePotionEffect(value.getType());
                    who.addPotionEffects(effects);
                }
            }
            case ENDERCHEST -> {
                if (this.enderChest != null)
                    who.getEnderChest().setStorageContents(this.enderChest.toArray(new ItemStack[0]));
            }
            case EXHAUSTION -> {
                if (this.exhaustion != null)
                    who.setExhaustion(exhaustion);
            }
            case EXPERIENCE -> {
                if (this.experience != null)
                    who.setExp(this.experience);
            }
            case FIRETICKS -> {
                if (this.fireTicks != null)
                    who.setFireTicks(this.fireTicks);
            }
            case FLYING -> {
                if (this.flying != null)
                    who.setFlying(this.flying);
            }
            case FLYSPEED -> {
                if (this.flySpeed != null)
                    who.setFlySpeed(this.flySpeed);
            }
            case FOODLEVEL -> {
                if (this.foodLevel != null)
                    who.setFoodLevel(foodLevel);
            }
            case GAMEMODE -> {
                if (this.gameMode != null)
                    who.setGameMode(this.gameMode);
            }
            case GOD -> {
                if (god != null)
                    who.setInvulnerable(god);
            }
            case HEALTH -> {
                if (this.health != null)
                    who.setHealth(health);
            }
            case INVENTORY -> {
                if (this.inventory != null)
                    who.getInventory().setStorageContents(inventory.toArray(new ItemStack[0]));
            }
            case LEVEL -> {
                if (level != null)
                    who.setLevel(level);
            }
            case LOCATION -> {
                if (this.locationWorldName != null)
                    try {
                        who.teleport(new Location(Bukkit.getWorld(locationWorldName), this.locationX, this.locationY,
                                this.locationZ, this.locationYaw, this.locationPitch));
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO try fallback location?
                    }
            }
            case SATURATION -> {
                if (this.saturation != null)
                    who.setSaturation(this.saturation);
            }
            case WALKSPEED -> {
                if (this.walkSpeed != null)
                    who.setWalkSpeed(walkSpeed);
            }
            case HELDITEMSLOT -> {
                if (this.heldItemSlot != null)
                    who.getInventory().setHeldItemSlot(this.heldItemSlot);
            }
            case FREEZETICKS -> {
                if (this.freezeTicks != null)
                    who.setFreezeTicks(this.freezeTicks);
            }
            case GLOWING -> {
                if (this.glowing != null)
                    who.setGlowing(this.glowing);
            }
            case COLLIDABLE -> {
                if (this.collidable != null)
                    who.setCollidable(this.collidable);
            }
            case CANPICKUPITEMS -> {
                if (this.canPickupItems != null)
                    who.setCanPickupItems(this.canPickupItems);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public void apply(Player who, FieldType... fields) {
        EnumSet<FieldType> loadedTypes = EnumSet.noneOf(FieldType.class);
        for (FieldType type : fields)
            if (type != null)
                loadedTypes.add(type);
        apply(who, loadedTypes);
    }

    public void loadFrom(Player who) {
        loadFrom(who, EnumSet.allOf(FieldType.class));
    }

    public Object get(FieldType type) {
        return switch (type) {
            case ABSORBITION -> getAbsorbition();
            case AIR -> getRemainingAir();
            case ALLOWFLIGHT -> getAllowFlight();
            case ARMOR -> getArmor();
            case EXTRACONTENTS -> getExtraContents();
            case EFFECTS -> getEffects();
            case ENDERCHEST -> getEnderChest();
            case EXHAUSTION -> getExhaustion();
            case EXPERIENCE -> getExperience();
            case FIRETICKS -> getFireTicks();
            case FLYING -> getFlying();
            case FLYSPEED -> getFlySpeed();
            case FOODLEVEL -> getFoodLevel();
            case GAMEMODE -> getGameMode();
            case GOD -> getGod();
            case HEALTH -> getHealth();
            case INVENTORY -> getInventory();
            case LEVEL -> getLevel();
            case LOCATION -> getLocation();
            case SATURATION -> getSaturation();
            case WALKSPEED -> getWalkSpeed();
            //case INVISIBLE -> getInvisible();
            case HELDITEMSLOT -> getHeldItemSlot();
            case FREEZETICKS -> getFreezeTicks();
            case GLOWING -> getGlowing();
            case COLLIDABLE -> getCollidable();
            case CANPICKUPITEMS -> getCanPickupItems();
            default -> throw new IllegalStateException();
        };

    }

    public Double getAbsorbition() {
        return this.absorbition;
    }

    public void setAbsorbition(Double value) {
        this.absorbition = value;
    }

    public Integer getRemainingAir() {
        return this.remainingAir;
    }

    public void setRemainingAir(Integer value) {
        this.remainingAir = value;
    }

    public Boolean getAllowFlight() {
        return this.allowFlight;
    }

    public void setAllowFlight(Boolean value) {
        this.allowFlight = value;
    }

    public List<ItemStack> getArmor() {
        if (this.armor == null)
            return null;
        return Collections.unmodifiableList(this.armor);
    }

    public void setArmor(List<ItemStack> value) {
        if (value != null && value.size() != 4)
            throw new IllegalArgumentException();
        this.armor = value == null ? null : new ArrayList<>(value);
    }

    public List<ItemStack> getExtraContents() {
        if (this.extraContents == null)
            return null;
        return Collections.unmodifiableList(this.extraContents);
    }

    public void setExtraContents(List<ItemStack> value) {
        if (value != null && value.size() != 1)
            throw new IllegalArgumentException();
        this.extraContents = value == null ? null : new ArrayList<>(value);
    }

    public Collection<PotionEffect> getEffects() {
        if (this.effects == null)
            return null;
        return Collections.unmodifiableCollection(this.effects);
    }

    public void setEffects(Collection<PotionEffect> value) {
        this.effects = value == null ? null : new ArrayList<>(value);
    }

    public List<ItemStack> getEnderChest() {
        if (this.enderChest == null)
            return null;
        return Collections.unmodifiableList(this.enderChest);
    }

    public void setEnderChest(List<ItemStack> value) {
        if (value != null && value.size() != 3 * 9)
            throw new IllegalArgumentException();
        this.enderChest = value == null ? null : new ArrayList<>(value);
    }

    public Float getExhaustion() {
        return this.exhaustion;
    }

    public void setExhaustion(Float value) {
        this.exhaustion = value;
    }

    public Float getExperience() {
        return this.experience;
    }

    public void setExperience(Float value) {
        this.experience = value;
    }

    public Integer getFireTicks() {
        return this.fireTicks;
    }

    public void setFireTicks(Integer value) {
        this.fireTicks = value;
    }

    public Boolean getFlying() {
        return this.flying;
    }

    public void setFlying(Boolean value) {
        this.flying = value;
    }

    public Float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(Float value) {
        this.flySpeed = value;
    }

    public Integer getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(Integer value) {
        this.foodLevel = value;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode value) {
        this.gameMode = value;
    }

    public Boolean getGod() {
        return this.god;
    }

    public void setGod(Boolean value) {
        this.god = value;
    }

    public Double getHealth() {
        return this.health;
    }

    public void setHealth(Double value) {
        this.health = value;
    }

    public List<ItemStack> getInventory() {
        if (this.inventory == null)
            return null;
        return Collections.unmodifiableList(this.inventory);
    }

    public void setInventory(List<ItemStack> value) {
        if (value != null && value.size() != 4 * 9)
            throw new IllegalArgumentException();
        this.inventory = value == null ? null : new ArrayList<>(value);
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer value) {
        this.level = value;
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

    public void setLocation(Location value) {
        World w = value == null ? null : value.getWorld();
        boolean isNull = value == null || w == null;
        this.locationWorldName = isNull ? null : w.getName();
        this.locationX = isNull ? null : value.getX();
        this.locationY = isNull ? null : value.getY();
        this.locationZ = isNull ? null : value.getZ();
        this.locationYaw = isNull ? null : value.getYaw();
        this.locationPitch = isNull ? null : value.getPitch();
    }

    public Float getSaturation() {
        return this.saturation;
    }

    public void setSaturation(Float value) {
        this.saturation = value;
    }

    public Float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setWalkSpeed(Float value) {
        this.walkSpeed = value;
    }

    public Integer getHeldItemSlot() {
        return heldItemSlot;
    }

    public void setHeldItemSlot(Integer value) { //check on number validity
        this.heldItemSlot = value;
    }

    public Integer getFreezeTicks() {
        return this.freezeTicks;
    }

    public void setFreezeTicks(Integer value) {
        this.freezeTicks = value;
    }

    public Boolean getGlowing() {
        return this.glowing;
    }

    public void setGlowing(Boolean value) {
        this.glowing = value;
    }

    public Boolean getCollidable() {
        return this.collidable;
    }

    public void setCollidable(Boolean value) {
        this.collidable = value;
    }

    public Boolean getCanPickupItems() {
        return this.canPickupItems;
    }

    public void setCanPickupItems(Boolean value) {
        this.canPickupItems = value;
    }

    @SuppressWarnings("unchecked")
    public void set(FieldType type, Object value) {
        switch (type) {
            case ABSORBITION -> setAbsorbition((Double) value);
            case AIR -> setRemainingAir((Integer) value);
            case ALLOWFLIGHT -> setAllowFlight((Boolean) value);
            case ARMOR -> setArmor((List<ItemStack>) value);
            case EXTRACONTENTS -> setExtraContents((List<ItemStack>) value);
            case EFFECTS -> setEffects((Collection<PotionEffect>) value);
            case ENDERCHEST -> setEnderChest((List<ItemStack>) value);
            case EXHAUSTION -> setExhaustion((Float) value);
            case EXPERIENCE -> setExperience((Float) value);
            case FIRETICKS -> setFireTicks((Integer) value);
            case FLYING -> setFlying((Boolean) value);
            case FLYSPEED -> setFlySpeed((Float) value);
            case FOODLEVEL -> setFoodLevel((Integer) value);
            case GAMEMODE -> setGameMode((GameMode) value);
            case GOD -> setGod((Boolean) value);
            case HEALTH -> setHealth((Double) value);
            case INVENTORY -> setInventory((List<ItemStack>) value);
            case LEVEL -> setLevel((Integer) value);
            case LOCATION -> setLocation((Location) value);
            case SATURATION -> setSaturation((Float) value);
            case WALKSPEED -> setWalkSpeed((Float) value);
            //case INVISIBLE -> setInvisible((Boolean) value);
            case HELDITEMSLOT -> setHeldItemSlot((Integer) value);
            case FREEZETICKS -> setFreezeTicks((Integer) value);
            case GLOWING -> setGlowing((Boolean) value);
            case COLLIDABLE -> setCollidable((Boolean) value);
            case CANPICKUPITEMS -> setCanPickupItems((Boolean) value);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
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
        return switch (type) {
            case ABSORBITION -> this.absorbition != null;
            case AIR -> this.remainingAir != null;
            case ALLOWFLIGHT -> this.allowFlight != null;
            case ARMOR -> this.armor != null;
            case EXTRACONTENTS -> this.extraContents != null;
            case EFFECTS -> this.effects != null;
            case ENDERCHEST -> this.enderChest != null;
            case EXHAUSTION -> this.exhaustion != null;
            case EXPERIENCE -> this.experience != null;
            case FIRETICKS -> this.fireTicks != null;
            case FLYING -> this.flying != null;
            case FLYSPEED -> this.flySpeed != null;
            case FOODLEVEL -> this.foodLevel != null;
            case GAMEMODE -> this.gameMode != null;
            case GOD -> this.god != null;
            case HEALTH -> this.health != null;
            case INVENTORY -> this.inventory != null;
            case LEVEL -> this.level != null;
            case LOCATION -> this.locationWorldName != null;
            case SATURATION -> this.saturation != null;
            case WALKSPEED -> this.walkSpeed != null;
            //case INVISIBLE -> this.invisible != null;
            case GLOWING -> this.glowing != null;
            case FREEZETICKS -> this.freezeTicks != null;
            case HELDITEMSLOT -> this.heldItemSlot != null;
            case COLLIDABLE -> this.collidable != null;
            case CANPICKUPITEMS -> this.canPickupItems != null;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    @SuppressWarnings("unchecked")
    public void setDefault(FieldType type) {
        switch (type) {
            case ABSORBITION -> this.absorbition = (Double) getDefault(type);
            case AIR -> this.remainingAir = (Integer) getDefault(type);
            case ALLOWFLIGHT -> this.allowFlight = (Boolean) getDefault(type);
            case ARMOR -> this.armor = (List<ItemStack>) getDefault(type);
            case EXTRACONTENTS -> this.extraContents = (List<ItemStack>) getDefault(type);
            case EFFECTS -> this.effects = (Collection<PotionEffect>) getDefault(type);
            case ENDERCHEST -> this.enderChest = (List<ItemStack>) getDefault(type);
            case EXHAUSTION -> this.exhaustion = (Float) getDefault(type);
            case EXPERIENCE -> this.experience = (Float) getDefault(type);
            case FIRETICKS -> this.fireTicks = (Integer) getDefault(type);
            case FLYING -> this.flying = (Boolean) getDefault(type);
            case FLYSPEED -> this.flySpeed = (Float) getDefault(type);
            case FOODLEVEL -> this.foodLevel = (Integer) getDefault(type);
            case GAMEMODE -> this.gameMode = (GameMode) getDefault(type);
            case GOD -> this.god = (Boolean) getDefault(type);
            case HEALTH -> this.health = (Double) getDefault(type);
            case INVENTORY -> this.inventory = (List<ItemStack>) getDefault(type);
            case LEVEL -> this.level = (Integer) getDefault(type);
            case LOCATION -> {
                Location loc = (Location) getDefault(type);
                this.locationWorldName = loc == null ? null : loc.getWorld().getName();
                this.locationX = loc == null ? null : loc.getX();
                this.locationY = loc == null ? null : loc.getY();
                this.locationZ = loc == null ? null : loc.getZ();
                this.locationYaw = loc == null ? null : loc.getYaw();
                this.locationPitch = loc == null ? null : loc.getPitch();
            }
            case SATURATION -> this.saturation = (Float) getDefault(type);
            case WALKSPEED -> this.walkSpeed = (Float) getDefault(type);
            //case INVISIBLE -> this.invisible = (Boolean) getDefault(type);
            case GLOWING -> this.glowing = (Boolean) getDefault(type);
            case COLLIDABLE -> this.collidable = (Boolean) getDefault(type);
            case CANPICKUPITEMS -> this.canPickupItems = (Boolean) getDefault(type);
            case FREEZETICKS -> this.freezeTicks = (Integer) getDefault(type);
            case HELDITEMSLOT -> this.heldItemSlot = (Integer) getDefault(type);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public Object getDefault(FieldType type) {
        return switch (type) {
            case ABSORBITION -> 0D;
            case FIRETICKS, LEVEL, FREEZETICKS -> 0;
            case EXPERIENCE -> 0F;
            case EXHAUSTION, SATURATION -> 1F;
            case HEALTH -> 20D;
            case AIR -> 300;
            case FOODLEVEL -> 20;
            case ALLOWFLIGHT, GOD, FLYING, GLOWING/*, INVISIBLE*/ -> false;
            case COLLIDABLE, CANPICKUPITEMS -> true;
            case ARMOR -> Collections.nCopies(4, (ItemStack) null);
            case EXTRACONTENTS -> Collections.nCopies(1, (ItemStack) null);
            case EFFECTS -> new ArrayList<PotionEffect>(0);
            case ENDERCHEST -> Collections.nCopies(9 * 3, (ItemStack) null);
            case WALKSPEED, FLYSPEED -> 0.2F;
            case HELDITEMSLOT -> 1;
            case GAMEMODE -> GameMode.SURVIVAL;
            case INVENTORY -> Collections.nCopies(9 * 4, (ItemStack) null);
            case LOCATION -> null;// TODO fallback?
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public enum FieldType {
        LOCATION, ARMOR, INVENTORY, ENDERCHEST, LEVEL, EXPERIENCE, EFFECTS, FOODLEVEL, EXHAUSTION, HEALTH, ABSORBITION,
        ALLOWFLIGHT, GOD, GAMEMODE, FLYSPEED, FIRETICKS, AIR, WALKSPEED, SATURATION, FLYING, EXTRACONTENTS, /*INVISIBLE,*/ HELDITEMSLOT,
        FREEZETICKS, COLLIDABLE, CANPICKUPITEMS, GLOWING
    }

}
