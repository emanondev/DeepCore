package emanondev.core.packetentity;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PacketItem extends PacketEntity {

	private ItemStack item;
	  
	  private boolean hasGravity;
	  
	  private boolean isGlowing;
	  
	  private int pickupDelay;
	  
	  private BaseComponent customName;
	  
	  private boolean custonNameVisible;
	  
	  private Vector velocity;
	  
	  PacketItem(Location location,PacketManager manager) {
	    super(location,manager);
	    this.item = new ItemStack(Material.STONE);
	    this.hasGravity = false;
	    this.pickupDelay = 0;
	    this.customName = (BaseComponent)new TextComponent();
	    this.custonNameVisible = false;
	    this.isGlowing = false;
	    this.velocity = new Vector(0.0D, 0.0D, 0.0D);
	  }
	  
	  public int cacheCode() {
	    int prime = 17;
	    int result = super.cacheCode();
	    result = prime * result + (this.hasGravity ? 5351 : 8923);
	    result = prime * result + this.pickupDelay;
	    result = prime * result + (this.hasGravity ? 6719 : 2753);
	    result = prime * result + ((this.item == null) ? 0 : this.item.hashCode());
	    result = prime * result + ((this.customName == null) ? 0 : this.customName.hashCode());
	    result = prime * result + (this.custonNameVisible ? 6199 : 8647);
	    result = prime * result + ((this.velocity == null) ? 0 : this.velocity.hashCode());
	    return result;
	  }
	  
	  public PacketItem setCustomName(String customName) {
	    this.customName = (BaseComponent)new TextComponent(customName);
	    return this;
	  }
	  
	  public PacketItem setCustomName(BaseComponent customName) {
	    this.customName = customName;
	    return this;
	  }
	  
	  public BaseComponent getCustomName() {
	    return this.customName;
	  }
	  
	  public PacketItem setGlowing(boolean bool) {
	    this.isGlowing = bool;
	    return this;
	  }
	  
	  public boolean isGlowing() {
	    return this.isGlowing;
	  }
	  
	  public PacketItem setCustomNameVisible(boolean bool) {
	    this.custonNameVisible = bool;
	    return this;
	  }
	  
	  public boolean isCustomNameVisible() {
	    return this.custonNameVisible;
	  }
	  
	  public EntityType getType() {
	    return EntityType.DROPPED_ITEM;
	  }
	  
	  public PacketItem setItemStack(ItemStack item, boolean force) {
	    if (this.lock && !force)
		    return this;
	    if (item.getType().equals(Material.AIR)) {
	      this.item = new ItemStack(Material.STONE);
		    return this;
	    } 
	    this.item = item.clone();
	    return this;
	  }
	  
	  public PacketItem setItemStack(ItemStack item) {
	    if (this.lock)
		    return this;
	    if (item.getType().equals(Material.AIR)) {
	      this.item = new ItemStack(Material.STONE);
		    return this;
	    } 
	    this.item = item.clone();
	    return this;
	  }
	  
	  public ItemStack getItemStack() {
	    return this.item.clone();
	  }
	  
	  public PacketItem setGravity(boolean bool) {
	    this.hasGravity = bool;
	    return this;
	  }
	  
	  public boolean hasGravity() {
	    return this.hasGravity;
	  }
	  
	  public PacketItem setVelocity(Vector vector) {
	    this.velocity = vector.clone();
	    return this;
	  }
	  
	  public Vector getVelocity() {
	    return this.velocity;
	  }
	  
	  public PacketItem setPickupDelay(int pickupDelay) {
	    this.pickupDelay = pickupDelay;
	    return this;
	  }
	  
	  public int getPickupDelay() {
	    return this.pickupDelay;
	  }
	  
	  public WrappedDataWatcher getWrappedDataWatcher() {
	    return WatchableCollection.getWatchableCollection(this);
	  }
	  
	  public double getHeight() {
	    return 0.25D;
	  }

	@Override
	protected void handleRemovePackets(Collection<? extends Player> players) {
		getManager().removeItem(players, this);
	}

	@Override
	protected void handleSpawnPackets(Collection<? extends Player> players) {
		getManager().spawnItem(players, this);
	}

	@Override
	protected void handleUpdatePackets(Collection<? extends Player> players) {
		getManager().updateItem(players, this);
	}
	}
	
	
	/*
	private ItemStack item;
	private boolean hasGravity;
	private boolean isGlowing;
	private int pickupDelay;
	private String customName;
	private boolean customNameVisible;
	private Vector velocity;

	public PacketItem(Location location, PacketManager manager) {
		super(location, manager);
		this.item = new ItemStack(Material.STONE);
		this.hasGravity = false;
		this.pickupDelay = 0;
		this.customName = "";
		this.customNameVisible = false;
		this.isGlowing = false;
		this.velocity = new Vector(0.0, 0.0, 0.0);
	}

	@Override
	public int cacheCode() {
		int prime = 17;
		int result = super.cacheCode();
		result = prime * result + ((hasGravity) ? 5351 : 8923);
		result = prime * result + pickupDelay;
		result = prime * result + ((hasGravity) ? 6719 : 2753);
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((customName == null) ? 0 : customName.hashCode());
		result = prime * result + ((customNameVisible) ? 6199 : 8647);
		result = prime * result + ((velocity == null) ? 0 : velocity.hashCode());
		return result;
	}

	public PacketItem setCustomName(String customName) {
		this.customName = customName;
		return this;
	}

	public String getCustomName() {
		return customName;
	}

	public PacketItem setGlowing(boolean bool) {
		this.isGlowing = bool;
		return this;
	}

	public boolean isGlowing() {
		return isGlowing;
	}

	public PacketItem setCustomNameVisible(boolean bool) {
		this.customNameVisible = bool;
		return this;
	}

	public boolean isCustomNameVisible() {
		return customNameVisible;
	}

	public EntityType getType() {
		return EntityType.DROPPED_ITEM;
	}

	public PacketItem setItemStack(ItemStack item, boolean force) {
		if (this.isLocked() && !force) {
			return this;
		}
		if (item.getType().equals(Material.AIR)) {
			this.item = new ItemStack(Material.STONE);
			return this;
		}
		this.item = item.clone();
		return this;
	}

	public PacketItem setItemStack(ItemStack item) {
		if (this.isLocked()) {
			return this;
		}
		if (item.getType().equals(Material.AIR)) {
			this.item = new ItemStack(Material.STONE);
			return this;
		}
		this.item = item.clone();
		return this;
	}

	public ItemStack getItemStack() {
		return item.clone();
	}

	public PacketItem setGravity(boolean bool) {
		this.hasGravity = bool;
		return this;
	}

	public boolean hasGravity() {
		return hasGravity;
	}

	public PacketItem setVelocity(Vector vector) {
		this.velocity = vector.clone();
		return this;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public PacketItem setPickupDelay(int pickupDelay) {
		this.pickupDelay = pickupDelay;
		return this;
	}

	public int getPickupDelay() {
		return pickupDelay;
	}

	public WrappedDataWatcher getWrappedDataWatcher() {
		return WatchableCollection.getWatchableCollection(this);
	}

	/*
	 * @Override public PacketItem spawn(Collection<? extends Player> players) {
	 * active.addAll(players); if
	 * (this.getItemStack().getType().equals(Material.AIR)) { return this; }
	 * 
	 * PacketContainer packet1 =
	 * PackManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.SPAWN_ENTITY
	 * ); packet1.getIntegers().write(0, this.getEntityId());
	 * packet1.getIntegers().write(1, (int) (this.getVelocity().getX() * 8000));
	 * packet1.getIntegers().write(2, (int) (this.getVelocity().getY() * 8000));
	 * packet1.getIntegers().write(3, (int) (this.getVelocity().getZ() * 8000));
	 * packet1.getIntegers().write(4, (int) (this.getLocation().getPitch() * 256.0F
	 * / 360.0F)); packet1.getIntegers().write(5, (int) (this.getLocation().getYaw()
	 * * 256.0F / 360.0F)); if (PackManager.VERSION.contains("1_13")) {// &&
	 * !PackManager.VERSION.contains("1_13_R2")) { packet1.getIntegers().write(6,
	 * 2); packet1.getIntegers().write(7, 1); } else {
	 * packet1.getEntityTypeModifier().write(0, this.getType());
	 * packet1.getIntegers().write(6, 1); } packet1.getUUIDs().write(0,
	 * this.getUniqueId()); Location location = this.getLocation();
	 * packet1.getDoubles().write(0, location.getX()); packet1.getDoubles().write(1,
	 * location.getY()); packet1.getDoubles().write(2, location.getZ());
	 * 
	 * if (!this.getManager().getPlugin().isEnabled()) { return this; }
	 * Bukkit.getScheduler().runTask(this.getManager().getPlugin(), () -> { try {
	 * for (Player player : players) {
	 * PackManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1); } } catch
	 * (InvocationTargetException e) { e.printStackTrace(); } }); return this; }
	 *//*

	public PacketItem spawn(Collection<? extends Player> players) {
		return (PacketItem) super.spawn(players);
	}

	@Override
	public PacketItem update() {
		return (PacketItem) super.update();
	}

	@Override
	public PacketItem update(Collection<? extends Player> players) {
		return (PacketItem) super.update(players);
	}

	@Override
	public PacketItem update(Collection<? extends Player> players, boolean bypasscache) {
		return (PacketItem) super.update(players, bypasscache);
	}

	@Override
	public PacketItem remove(Collection<? extends Player> players) {
		return (PacketItem) super.remove(players);
	}

	@Override
	public PacketItem remove() {
		return (PacketItem) super.remove();
	}

	protected void handleSpawnPackets(Collection<? extends Player> players) {
		if (this.getItemStack().getType().equals(Material.AIR)) {
			return;
		}

		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		packet1.getIntegers().write(0, this.getEntityId());
		packet1.getIntegers().write(1, (int) (this.getVelocity().getX() * 8000));
		packet1.getIntegers().write(2, (int) (this.getVelocity().getY() * 8000));
		packet1.getIntegers().write(3, (int) (this.getVelocity().getZ() * 8000));
		packet1.getIntegers().write(4, (int) (this.getLocation().getPitch() * 256.0F / 360.0F));
		packet1.getIntegers().write(5, (int) (this.getLocation().getYaw() * 256.0F / 360.0F));
		if (PacketManager.VERSION.contains("1_13")) {// && !PackManager.VERSION.contains("1_13_R2")) {
			packet1.getIntegers().write(6, 2);
			packet1.getIntegers().write(7, 1);
		} else {
			packet1.getEntityTypeModifier().write(0, this.getType());
			packet1.getIntegers().write(6, 1);
		}
		packet1.getUUIDs().write(0, this.getUniqueId());
		Location location = this.getLocation();
		packet1.getDoubles().write(0, location.getX());
		packet1.getDoubles().write(1, location.getY());
		packet1.getDoubles().write(2, location.getZ());

		if (!this.getManager().getPlugin().isEnabled()) {
			return;
		}
		Bukkit.getScheduler().runTask(this.getManager().getPlugin(), () -> {
			try {
				for (Player player : players) {
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void handleUpdatePackets(Collection<? extends Player> players) {
		if (this.getItemStack().getType().equals(Material.AIR)) {
			return;
		}

		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet1.getIntegers().write(0, this.getEntityId());
		WrappedDataWatcher wpw = this.getWrappedDataWatcher();
		packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());

		PacketContainer packet2 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet2.getIntegers().write(0, this.getEntityId());
		packet2.getDoubles().write(0, this.getLocation().getX());
		packet2.getDoubles().write(1, this.getLocation().getY());
		packet2.getDoubles().write(2, this.getLocation().getZ());
		packet2.getBytes().write(0, (byte) (int) (this.getLocation().getYaw() * 256.0F / 360.0F));
		packet2.getBytes().write(1, (byte) (int) (this.getLocation().getPitch() * 256.0F / 360.0F));

		PacketContainer packet3 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
		packet3.getIntegers().write(0, this.getEntityId());
		packet3.getIntegers().write(1, (int) (this.getVelocity().getX() * 8000));
		packet3.getIntegers().write(2, (int) (this.getVelocity().getY() * 8000));
		packet3.getIntegers().write(3, (int) (this.getVelocity().getZ() * 8000));

		if (!this.getManager().getPlugin().isEnabled()) {
			return;
		}
		Bukkit.getScheduler().runTask(this.getManager().getPlugin(), () -> {
			try {
				for (Player player : players) {
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1);
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet2);
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet3);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	protected void handleRemovePackets(Collection<? extends Player> players) {
		if (this.getItemStack().getType().equals(Material.AIR)) {
			return;
		}

		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet1.getIntegerArrays().write(0, new int[] { this.getEntityId() });

		if (!getManager().getPlugin().isEnabled()) {
			return;
		}
		Bukkit.getScheduler().runTask(getManager().getPlugin(), () -> {
			try {
				for (Player player : players) {
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});

	}

}*/