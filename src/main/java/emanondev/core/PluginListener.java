package emanondev.core;

import emanondev.core.actions.ActionHandler;
import emanondev.core.actions.CompleteMissionAction;
import emanondev.core.actions.ProgressTaskAction;
import emanondev.core.condition.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public final class PluginListener implements Listener {

    @EventHandler
    private void event(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("DeepQuests5")) {
            CoreMain.get().logInfo("Registering conditions for DeepQuests5");
            ConditionHandler.registerCondition(new CompletedMissionCondition());
            ConditionHandler.registerCondition(new UnstartedMissionCondition());
            ConditionHandler.registerCondition(new OngoingMissionCondition());
            ConditionHandler.registerCondition(new CompletedTaskCondition());
            CoreMain.get().logInfo("Registered conditions for DeepQuests5");
            CoreMain.get().logInfo("Registering actions for DeepQuests5");
            ActionHandler.registerAction(new ProgressTaskAction());
            ActionHandler.registerAction(new CompleteMissionAction());
            CoreMain.get().logInfo("Registered actions for DeepQuests5");
        }
    }

    @EventHandler
    private void event(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("DeepQuests5")) {
            CoreMain.get().logInfo("Unregistering conditions for DeepQuests5");
            ConditionHandler.unregisterCondition(CompletedMissionCondition.ID);
            ConditionHandler.unregisterCondition(UnstartedMissionCondition.ID);
            ConditionHandler.unregisterCondition(OngoingMissionCondition.ID);
            ConditionHandler.unregisterCondition(CompletedTaskCondition.ID);
            CoreMain.get().logInfo("Unregistered conditions for DeepQuests5");
            CoreMain.get().logInfo("Unregistering actions for DeepQuests5");
            ActionHandler.unregisterAction(ProgressTaskAction.ID);
            ActionHandler.unregisterAction(CompleteMissionAction.ID);
            CoreMain.get().logInfo("Unregistered actions for DeepQuests5");
        }
    }
}
