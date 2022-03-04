package emanondev.core.packetentity;

import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PacketArmorStandBlock extends PacketArmorStand {

    PacketArmorStandBlock(Location location, PacketManager manager, ItemStack block) {
        super(location, manager);
        this.setItem(EquipmentSlot.HEAD,block);
        this.setInvulnerable(true);
        this.setVisible(false);
    }


}
