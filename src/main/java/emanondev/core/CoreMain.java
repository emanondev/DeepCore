package emanondev.core;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

import emanondev.core.events.CustomEventListener;
import emanondev.core.gui.GuiHandler;

public final class CoreMain extends CorePlugin {

	private static CoreMain inst;

	/**
	 * 
	 * @return the instance of the plugin
	 */
	public static @NotNull CoreMain get() {
		return inst;
	}

	public void load() {
		if (inst != null)
			throw new IllegalStateException("Plugin already loaded");
		inst = this;
		ConfigurationSerialization.registerClass(SoundInfo.class, "SoundInfo");
		this.logDone("Registered &aSoundInfo &fas ConfigurationSerializable");
		ConfigurationSerialization.registerClass(PlayerSnapshot.class, "PlayerSnapshot");
		this.logDone("Registered &aPlayerSnapshot &fas ConfigurationSerializable");
	}

	public void enable() {
		this.registerListener(new CustomEventListener());
		this.logDone("Enabled &aPlayerBuyMerchantRecipeEvent");
		this.registerListener(new GuiHandler());
		this.logDone("Enabled &aDeepCore Guis");
		this.registerListener(new SpawnReasonTracker());
		this.logDone("Enabled &aSpawnReasonTracker");
		this.registerListener(new EquipChangeListener());
		this.logDone("Enabled &aEquipmentChangeEvent");
	}

	public void reload() {
	}

	protected boolean registerReloadCommand() {
		return false;
	}

	@Override
	public void disable() {
		ConfigurationSerialization.unregisterClass(SoundInfo.class);
		this.logDone("Unregistered &eSoundInfo &ffrom ConfigurationSerializables");
	}
}