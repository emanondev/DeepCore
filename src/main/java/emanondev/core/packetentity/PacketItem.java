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
	  
	  private boolean customNameVisible;
	  
	  private Vector velocity;
	  
	  PacketItem(Location location,PacketManager manager) {
	    super(location,manager);
	    this.item = new ItemStack(Material.STONE);
	    this.hasGravity = false;
	    this.pickupDelay = 0;
	    this.customName = new TextComponent();
	    this.customNameVisible = false;
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
	    result = prime * result + (this.customNameVisible ? 6199 : 8647);
	    result = prime * result + ((this.velocity == null) ? 0 : this.velocity.hashCode());
	    return result;
	  }
	  
	  public PacketItem setCustomName(String customName) {
	    this.customName = new TextComponent(customName);
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
	    this.customNameVisible = bool;
	    return this;
	  }
	  
	  public boolean isCustomNameVisible() {
	    return this.customNameVisible;
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