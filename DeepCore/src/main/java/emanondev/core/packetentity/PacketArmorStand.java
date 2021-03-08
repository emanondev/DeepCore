package emanondev.core.packetentity;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

public class PacketArmorStand extends PacketEntity {

	private boolean hasArms;
	private boolean hasBasePlate;
	private boolean isMarker;
	private boolean hasGravity;
	private boolean isSmall;
	private boolean isInvulnerable;
	private boolean isVisible;
	private EulerAngle rightArmPose;
	private EulerAngle headPose;
	private ItemStack helmet;
	private ItemStack mainhand;
	private String customName;
	private boolean custonNameVisible;
	private Vector velocity;

	public PacketArmorStand(Location location,PacketManager manager) {
		super(location,manager);
			this.hasArms = false;
			this.hasBasePlate = true;
			this.isMarker = false;
			this.hasGravity = true;
			this.isSmall = false;
			this.isInvulnerable = false;
			this.isVisible = true;
			this.rightArmPose = new EulerAngle(0.0, 0.0, 0.0);
			this.headPose = new EulerAngle(0.0, 0.0, 0.0);
			this.helmet = new ItemStack(Material.AIR);
			this.mainhand = new ItemStack(Material.AIR);
			this.customName = "";
			this.custonNameVisible = false;
			this.velocity = new Vector(0.0, 0.0, 0.0);
		}

	@Override
	public int cacheCode() {
		int prime = 17;
		int result = super.cacheCode();
		result = prime * result + ((hasArms) ? 5351 : 8923);
		result = prime * result + ((hasBasePlate) ? 2861 : 6607);
		result = prime * result + ((isMarker) ? 9199 : 3163);
		result = prime * result + ((hasGravity) ? 6719 : 2753);
		result = prime * result + ((isSmall) ? 1373 : 3037);
		result = prime * result + ((isInvulnerable) ? 2111 : 2251);
		result = prime * result + ((isVisible) ? 6779 : 6679);
		result = prime * result + ((rightArmPose == null) ? 0 : rightArmPose.hashCode());
		result = prime * result + ((headPose == null) ? 0 : headPose.hashCode());
		result = prime * result + ((helmet == null) ? 0 : helmet.hashCode());
		result = prime * result + ((mainhand == null) ? 0 : mainhand.hashCode());
		result = prime * result + ((customName == null) ? 0 : customName.hashCode());
		result = prime * result + ((custonNameVisible) ? 6199 : 8647);
		result = prime * result + ((velocity == null) ? 0 : velocity.hashCode());
		return result;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomNameVisible(boolean bool) {
		this.custonNameVisible = bool;
	}

	public boolean isCustomNameVisible() {
		return custonNameVisible;
	}

	public void setArms(boolean bool) {
		this.hasArms = bool;
	}

	public boolean hasArms() {
		return hasArms;
	}

	public void setBasePlate(boolean bool) {
		this.hasBasePlate = bool;
	}

	public boolean hasBasePlate() {
		return hasBasePlate;
	}

	public void setMarker(boolean bool) {
		this.isMarker = bool;
	}

	public boolean isMarker() {
		return isMarker;
	}

	public void setGravity(boolean bool) {
		this.hasGravity = bool;
	}

	public boolean hasGravity() {
		return hasGravity;
	}

	public void setSmall(boolean bool) {
		this.isSmall = bool;
	}

	public boolean isSmall() {
		return isSmall;
	}

	public void setInvulnerable(boolean bool) {
		this.isInvulnerable = bool;
	}

	public boolean isInvulnerable() {
		return isInvulnerable;
	}

	public void setVisible(boolean bool) {
		this.isVisible = bool;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setRightArmPose(EulerAngle angle) {
		if (this.isLocked()) {
			return;
		}
		this.rightArmPose = angle;
	}

	public EulerAngle getRightArmPose() {
		return rightArmPose;
	}

	public void setHeadPose(EulerAngle angle) {
		if (this.isLocked()) {
			return;
		}
		this.headPose = angle;
	}

	public EulerAngle getHeadPose() {
		return headPose;
	}

	public void setHelmet(ItemStack item) {
		if (this.isLocked()) {
			return;
		}
		this.helmet = item.clone();
	}

	public ItemStack getHelmet() {
		return helmet;
	}

	public void setItemInMainHand(ItemStack item) {
		if (this.isLocked()) {
			return;
		}
		this.mainhand = item.clone();
	}

	public ItemStack getItemInMainHand() {
		return mainhand;
	}

	public void setVelocity(Vector vector) {
		this.velocity = vector.clone();
	}

	public Vector getVelocity() {
		return velocity;
	}

	public WrappedDataWatcher getWrappedDataWatcher() {
		return WatchableCollection.getWatchableCollection(this);
	}

	public PacketArmorStand updateOnlyMeta() {
		return updateOnlyMeta(active);
	}
	
	public PacketArmorStand updateOnlyMeta(Collection<Player> players) {
		return updateOnlyMeta(players, false);
	}
	
	public PacketArmorStand updateOnlyMeta(Collection<Player> players, boolean bypasscache) {
		if (!bypasscache) {
			if (cache != null) {
				if (cache == this.cacheCode()) {
					return this;
				}
			}
		}
		
		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet1.getIntegers().write(0, this.getEntityId());	
        WrappedDataWatcher wpw = this.getWrappedDataWatcher();
        packet1.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        
        if (!getManager().getPlugin().isEnabled()) {
			return this;
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
        
        cache = this.cacheCode();
        return this;
	}

	@Override
	protected void handleRemovePackets(Collection<Player> players) {
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
	protected void handleSpawnPackets(Collection<Player> players) {
		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		packet1.getIntegers().write(0, this.getEntityId());
		packet1.getIntegers().write(1, 30);
		
		packet1.getIntegers().write(2, (int) (this.getVelocity().getX() * 8000));
		packet1.getIntegers().write(3, (int) (this.getVelocity().getY() * 8000));
		packet1.getIntegers().write(4, (int) (this.getVelocity().getZ() * 8000));		
		packet1.getDoubles().write(0, this.getLocation().getX());
		packet1.getDoubles().write(1, this.getLocation().getY());
		packet1.getDoubles().write(2, this.getLocation().getZ());
		packet1.getBytes().write(0, (byte)(int) (this.getLocation().getYaw() * 256.0F / 360.0F)); //Yaw
		packet1.getBytes().write(1, (byte)(int) (this.getLocation().getPitch() * 256.0F / 360.0F)); //Pitch
		packet1.getBytes().write(2, (byte)(int) (this.getLocation().getYaw() * 256.0F / 360.0F)); //Head
		
		PacketContainer packet2 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet2.getIntegers().write(0, this.getEntityId());	
        WrappedDataWatcher wpw = this.getWrappedDataWatcher();
        packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        
        if (!getManager().getPlugin().isEnabled()) {
			return;
		}
        Bukkit.getScheduler().runTask(getManager().getPlugin(), () -> {
	        try {
	        	for (Player player : players) {
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1);
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet2);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        });
	}

	@Override
	protected void handleUpdatePackets(Collection<Player> players) {

		PacketContainer packet1 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet1.getIntegers().write(0, this.getEntityId());
        packet1.getDoubles().write(0, this.getLocation().getX());
		packet1.getDoubles().write(1, this.getLocation().getY());
		packet1.getDoubles().write(2, this.getLocation().getZ());
		packet1.getBytes().write(0, (byte)(int) (this.getLocation().getYaw() * 256.0F / 360.0F));
		packet1.getBytes().write(1, (byte)(int) (this.getLocation().getPitch() * 256.0F / 360.0F));
			
		PacketContainer packet2 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet2.getIntegers().write(0, this.getEntityId());	
        WrappedDataWatcher wpw = this.getWrappedDataWatcher();
        packet2.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());

        PacketContainer packet3 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet3.getIntegers().write(0, this.getEntityId());
        packet3.getItemSlots().write(0, ItemSlot.MAINHAND);
        packet3.getItemModifier().write(0, this.getItemInMainHand());

        PacketContainer packet4 = PacketManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet4.getIntegers().write(0, this.getEntityId());
        packet4.getItemSlots().write(0, ItemSlot.HEAD);
        packet4.getItemModifier().write(0, this.getHelmet());
		/*
		PacketContainer packet5 = PackManager.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
		packet5.getIntegers().write(0, this.getEntityId());
		packet5.getIntegers().write(1, (int) (this.getVelocity().getX() * 8000));
		packet5.getIntegers().write(2, (int) (this.getVelocity().getY() * 8000));
		packet5.getIntegers().write(3, (int) (this.getVelocity().getZ() * 8000));
		*/
        
        if (!getManager().getPlugin().isEnabled()) {
			return;
		}
        Bukkit.getScheduler().runTask(getManager().getPlugin(), () -> {
	        try {
	        	for (Player player : players) {
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet1);
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet2);
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet3);
					PacketManager.PROTOCOL_MANAGER.sendServerPacket(player, packet4);
					//PackManager.PROTOCOL_MANAGER.sendServerPacket(player, packet5);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        });
	}
	

	public PacketArmorStand spawn(Collection<Player> players) {
		return (PacketArmorStand) super.spawn(players);
	}

	@Override
	public PacketArmorStand update() {
		return (PacketArmorStand) super.update();
	}

	@Override
	public PacketArmorStand update(Collection<Player> players) {
		return (PacketArmorStand) super.update(players);
	}

	@Override
	public PacketArmorStand update(Collection<Player> players, boolean bypasscache) {
		return (PacketArmorStand) super.update(players, bypasscache);
	}
	
	@Override
	public PacketArmorStand remove(Collection<Player> players) {
		return (PacketArmorStand) super.remove(players);
	}

	@Override
	public PacketArmorStand remove() {
		return (PacketArmorStand) super.remove();
	}

}