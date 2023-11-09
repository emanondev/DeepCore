package emanondev.core;

import emanondev.core.actions.*;
import emanondev.core.condition.*;
import emanondev.core.events.CustomEventListener;
import emanondev.core.events.EquipChangeListener;
import emanondev.core.gui.GuiHandler;
import emanondev.core.util.FAWECleaner;
import emanondev.core.util.ItemUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

public final class CoreMain extends CorePlugin {

    private static CoreMain inst;

    /**
     * @return the instance of the plugin
     */
    public static @NotNull CoreMain get() {
        return inst;
    }

    protected boolean registerReloadCommand() {
        return false;
    }

    @Override
    public void disable() {
        ConfigurationSerialization.unregisterClass(SoundInfo.class);
        this.logDone("Unregistered &eSoundInfo&f from ConfigurationSerialization");
        if (Hooks.isFAWEEnabled()) {
            this.logDone("Cleaning WorldEditCache");
            FAWECleaner.cleanAll();
            this.logDone("Cleaned");
        }
    }

    public void reload() {
    }

    public void enable() {
        this.registerListener(new CustomEventListener());
        this.logDone("Enabled &aMerchantCraftEvent");
        this.registerListener(new GuiHandler());
        this.logDone("Enabled &aDeepCore Guis");
        this.registerListener(new SpawnReasonTracker());
        this.logDone("Enabled &aSpawnReasonTracker");
        this.registerListener(new EquipChangeListener());
        this.logDone("Enabled &aEquipmentChangeEvent");
        ActionHandler.clearActions(); //required for plugman reload
        ActionHandler.registerAction(new DelayedAction());
        ActionHandler.registerAction(new PermissionAction());
        ActionHandler.registerAction(new PlayerCommandAction());
        ActionHandler.registerAction(new PlayerAsOpCommandAction());
        ActionHandler.registerAction(new ServerCommandAction());
        ActionHandler.registerAction(new SoundAction());
        ActionHandler.registerAction(new ConditionalAction());
        ActionHandler.registerAction(new SequenceAction());
        ActionHandler.registerAction(new MessageAction());
        ActionHandler.registerAction(new CooldownAction());
        ActionHandler.registerAction(new PayMoneyAction());
        this.logDone("Enabled &aActions API");
        ConditionHandler.registerCondition(new WorldCondition());
        ConditionHandler.registerCondition(new OrCondition());
        ConditionHandler.registerCondition(new NoCooldownCondition());

        ConditionHandler.registerCondition(new HasMoneyCondition());

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Hooks.isEnabled("DeepQuests5")) {
                ConditionHandler.registerCondition(new CompletedMissionCondition());
                ConditionHandler.registerCondition(new UnstartedMissionCondition());
                ConditionHandler.registerCondition(new OngoingMissionCondition());
            }
        }, 1L);
        this.logDone("Enabled &aConditions API");
        ItemUtility.initialize();

        try {
            this.registerCommand(new TestCommand());
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
}
