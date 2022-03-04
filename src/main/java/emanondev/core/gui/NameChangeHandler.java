package emanondev.core.gui;

import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import emanondev.core.CoreMain;

public class NameChangeHandler {

	public NameChangeHandler() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(
				new PacketAdapter(CoreMain.get(), ListenerPriority.NORMAL, PacketType.Play.Client.ITEM_NAME) {
					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof TextGui gui))
							return;
						new BukkitRunnable() { //need to be sync
							public void run() {
								gui.onTextChange(event.getPacket().getStrings().read(0));
							}
						}.runTask(getPlugin());
					}
				});
	}

}
