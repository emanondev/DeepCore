package emanondev.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class UtilsMessages {
	
	UtilsMessages(){
		throw new UnsupportedOperationException();
	}

	/**
	 * Utility message for sending version independent actionbar messages as to be
	 * able to support versions from 1.8 and up without having to disable a simple
	 * feature such as this.
	 *
	 * @param player
	 *            the recipient of the actionbar message.
	 * @param message
	 *            the message to send. If it is empty ("") the actionbar is cleared.
	 */
	public static void sendActionbar(Player player, String message) {
		if (player == null || message == null)
			return;
		String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
		nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

		// 1.10 and up
		if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
			return;
		}

		// 1.8.x and 1.9.x
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
			Object craftPlayer = craftPlayerClass.cast(player);

			Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
			Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
			Object packetPlayOutChat;
			Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion
					+ (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
			Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

			Method method = null;
			if (nmsVersion.equalsIgnoreCase("v1_8_R1"))
				method = chat.getDeclaredMethod("a", String.class);

			Object object = nmsVersion.equalsIgnoreCase("v1_8_R1")
					? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}"))
					: chat.getConstructor(new Class[] { String.class }).newInstance(message);
			packetPlayOutChat = ppoc.getConstructor(new Class[] { chatBaseComponent, Byte.TYPE }).newInstance(object,
					(byte) 2);

			Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
			Object iCraftPlayer = handle.invoke(craftPlayer);
			Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
			Object playerConnection = playerConnectionField.get(iCraftPlayer);
			Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
			sendPacket.invoke(playerConnection, packetPlayOutChat);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * message is not send if message is null or empty
	 * @param target Who will reiceve the message
	 * @param message Message to send
	 */
	public static void sendMessage(CommandSender target,String message){
		if (message==null || message.isEmpty())
			return;
		target.sendMessage(message);
	}

	/**
	 * message is not send if message is null or empty
	 * @param target Who will reiceve the message
	 * @param message Message to send
	 */
	public static void sendMessage(CommandSender target,BaseComponent[] message){
		if (message==null || message.length==0)
			return;
		target.spigot().sendMessage(message);
	}

}
