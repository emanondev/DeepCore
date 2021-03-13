package emanondev.core.packetentity;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class PacketItemFrame extends PacketEntity {
	  private ItemStack item;
	  
	  private BlockFace facing;
	  
	  private int framerotation;
	  
	  public PacketItemFrame(Location location,PacketManager manager) {
	    super(location,manager);
	    this.item = new ItemStack(Material.AIR);
	    this.facing = BlockFace.SOUTH;
	    this.framerotation = 0;
	  }
	  
	  public int cacheCode() {
	    int prime = 17;
	    int result = super.cacheCode();
	    result = prime * result + ((this.item == null) ? 0 : this.item.hashCode());
	    result = prime * result + ((this.facing == null) ? 0 : this.facing.hashCode());
	    result = prime * result + this.framerotation;
	    return result;
	  }
	  
	  public EntityType getType() {
	    return EntityType.ITEM_FRAME;
	  }
	  
	  public BlockFace getAttachedFace() {
	    return this.facing;
	  }
	  
	  public float getYaw() {
	    switch (this.facing) {
	      case DOWN:
	        return 0.0F;
	      case EAST:
	        return -90.0F;
	      case NORTH:
	        return 180.0F;
	      case SOUTH:
	        return 0.0F;
	      case UP:
	        return 0.0F;
	      case WEST:
	        return 90.0F;
		default:
			break;
	    } 
	    return 0.0F;
	  }
	  
	  public float getPitch() {
	    switch (this.facing) {
	      case DOWN:
	        return 90.0F;
	      case EAST:
	        return 0.0F;
	      case NORTH:
	        return 0.0F;
	      case SOUTH:
	        return 0.0F;
	      case UP:
	        return -90.0F;
	      case WEST:
	        return 0.0F;
		default:
			break;
	    } 
	    return 0.0F;
	  }
	  
	  public PacketItemFrame setItem(ItemStack item) {
	    this.item = item.clone();
	    return this;
	  }
	  
	  public ItemStack getItem() {
	    return this.item;
	  }
	  
	  public PacketItemFrame setFacingDirection(BlockFace facing) {
	    this.facing = facing;
	    return this;
	  }
	  
	  public BlockFace getFacingDirection() {
	    return this.facing;
	  }
	  
	  public PacketItemFrame setFrameRotation(int rotation) {
	    if (rotation >= 0 && rotation < 8) {
	      this.framerotation = rotation;
	    } else {
	      Bukkit.getLogger().severe("Item Frame Rotation must be between 0 and 7");
	    }
	    return this;
	  }
	  
	  public int getFrameRotation() {
	    return this.framerotation;
	  }
	  
	  public WrappedDataWatcher getWrappedDataWatcher() {
	    return WatchableCollection.getWatchableCollection(this);
	  }
	  
	  public double getHeight() {
	    return 0.75D;
	  }

	@Override
	protected void handleRemovePackets(Collection<? extends Player> players) {
		getManager().removeItemFrame(players, this);
	}

	@Override
	protected void handleSpawnPackets(Collection<? extends Player> players) {
		getManager().spawnItemFrame(players, this);
	}

	@Override
	protected void handleUpdatePackets(Collection<? extends Player> players) {
		getManager().updateItemFrame(players, this);
	}
	}
	
	
	
	
	/*
	
	private ItemStack item;
	private BlockFace facing;
	private int framerotation;
	
	PacketItemFrame(Location location,PacketManager manager) {
		super(location,manager);
		this.item = new ItemStack(Material.AIR);
		this.facing = BlockFace.SOUTH;
		this.framerotation = 0;
	}
	
	@Override
	public int cacheCode() {
		int prime = 17;
		int result = super.cacheCode();
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((facing == null) ? 0 : facing.hashCode());
		result = prime * result + framerotation;
		return result;
	}
	
	public EntityType getType() {
		return EntityType.ITEM_FRAME;
	}
	
	public BlockFace getAttachedFace() {
		return facing;
	}
	
	public float getYaw() {
		switch (facing) {
		case DOWN:
			return 0.0F;
		case EAST:
			return -90.0F;
		case NORTH:
			return 180.0F;
		case SOUTH:
			return 0.0F;
		case UP:
			return 0.0F;
		case WEST:
			return 90.0F;
		default:
			return 0.0F;	
		}
	}
	
	public float getPitch() {
		switch (facing) {
		case DOWN:
			return 90.0F;
		case EAST:
			return 0.0F;
		case NORTH:
			return 0.0F;
		case SOUTH:
			return 0.0F;
		case UP:
			return -90.0F;
		case WEST:
			return 0.0F;
		default:
			return 0.0F;	
		}
	}
	
	public void setItem(ItemStack item) {
		this.item = item.clone();
	}
	public ItemStack getItem() {
		return item;
	}
	
	public void setFacingDirection(BlockFace facing) {
		this.facing = facing;
	}
	
	public BlockFace getFacingDirection() {
		return facing;
	}
	
	public void setFrameRotation(int rotation) {
		if (rotation >= 0 && rotation < 8) {
			this.framerotation = rotation;
		} else {
			Bukkit.getLogger().severe("Item Frame Rotation must be between 0 and 7");
		}
	}
	
	public int getFrameRotation() {
		return framerotation;
	}
	
	public WrappedDataWatcher getWrappedDataWatcher() {
		return WatchableCollection.getWatchableCollection(this);
	}
	
	private int getItemFrameData() {
		switch (getAttachedFace()) {
		case DOWN:
			return 0;
		case UP:
			return 1;
		case NORTH:
			return 2;
		case SOUTH:
			return 3;
		case WEST:
			return 4;
		case EAST:
			return 5;
		default:
			return 0;	
		}
	}
	

	protected void handleSpawnPackets(Collection<? extends Player> players) {
		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet1.getIntegers().write(0, this.getEntityId());
        packet1.getIntegers().write(1, 0);
        packet1.getIntegers().write(2, 0);
        packet1.getIntegers().write(3, 0);
        packet1.getIntegers().write(4, (int) (this.getPitch() * 256.0F / 360.0F));
        packet1.getIntegers().write(5, (int) (this.getYaw() * 256.0F / 360.0F));
        if (PacketManager.VERSION.contains("1_13")) {// || InteractionVisualizer.version.equals("1.13.1") || InteractionVisualizer.version.contains("legacy")) {
            packet1.getIntegers().write(6, 33);
            packet1.getIntegers().write(7, getItemFrameData());
        } else {
            packet1.getEntityTypeModifier().write(0, this.getType());
            packet1.getIntegers().write(6, getItemFrameData());
        }
        packet1.getUUIDs().write(0, this.getUniqueId());
        Location location = this.getLocation();
        packet1.getDoubles().write(0, location.getX());
        packet1.getDoubles().write(1, location.getY());
        packet1.getDoubles().write(2, location.getZ());
        
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

/*
	@Override
	public PacketItem spawn(Collection<? extends Player> players) {
		active.addAll(players);
		if (this.getItemStack().getType().equals(Material.AIR)) {
			return this;
		}

		PacketContainer packet1 = PackManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		packet1.getIntegers().write(0, this.getEntityId());
		packet1.getIntegers().write(1, (int) (this.getVelocity().getX() * 8000));
		packet1.getIntegers().write(2, (int) (this.getVelocity().getY() * 8000));
		packet1.getIntegers().write(3, (int) (this.getVelocity().getZ() * 8000));
		packet1.getIntegers().write(4, (int) (this.getLocation().getPitch() * 256.0F / 360.0F));
		packet1.getIntegers().write(5, (int) (this.getLocation().getYaw() * 256.0F / 360.0F));
		if (PackManager.VERSION.contains("1_13")) {// && !PackManager.VERSION.contains("1_13_R2")) {
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
			return this;
		}
		Bukkit.getScheduler().runTask(this.getManager().getPlugin(), () -> {
			try {
				for (Player player : players) {
					PackManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});
		return this;
	}*//*

	@Override
	public PacketItemFrame update() {
		return (PacketItemFrame) super.update();
	}

	@Override
	public PacketItemFrame update(Collection<? extends Player> players) {
		return (PacketItemFrame) super.update(players);
	}

	@Override
	public PacketItemFrame update(Collection<? extends Player> players, boolean bypasscache) {
		return (PacketItemFrame) super.update(players, bypasscache);
	}
	
	protected void handleUpdatePackets(Collection<? extends Player> players) {
		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet1.getIntegers().write(0, this.getEntityId());
        WrappedDataWatcher wpw = this.getWrappedDataWatcher();
        packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        
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

	protected void handleRemovePackets(Collection<? extends Player> players) {
		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet1.getIntegerArrays().write(0, new int[]{this.getEntityId()});
		
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

	@Override
	public PacketItemFrame remove(Collection<? extends Player> players) {
		return (PacketItemFrame) super.remove(players);
	}
	

	@Override
	public PacketItemFrame remove() {
		return (PacketItemFrame) super.remove();
	}
	
	public PacketItemFrame spawn(Collection<? extends Player> players) {
		return (PacketItemFrame) super.spawn(players);
	}

}*/