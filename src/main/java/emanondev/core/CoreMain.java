package emanondev.core;

import emanondev.core.actions.*;
import emanondev.core.command.CoreCommand;
import emanondev.core.events.CustomEventListener;
import emanondev.core.events.EquipChangeListener;
import emanondev.core.gui.GuiHandler;
import emanondev.core.message.DMessage;
import emanondev.core.message.Translations;
import emanondev.core.util.ItemUtility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CoreMain extends CorePlugin {

    private static CoreMain inst;

    /**
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
        this.logDone("Enabled &aActionAPI");
        ItemUtility.initialize();

        this.registerCommand(new TestCommand());
    }

    class TestCommand extends CoreCommand {//TODO

        public TestCommand() {
            super("test", inst, null);
        }

        @Override
        public void onExecute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
            ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
            BookMeta meta = (BookMeta) item.getItemMeta();
            meta.spigot().addPage(new ComponentBuilder("text").event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/somecommand")).append("testo2").create());
            item.setItemMeta(meta);
            CoreMain.this.getConfig("file.yml").set("test", item);
            new DMessage(getPlugin(), sender).append(Translations.get(Material.ANVIL))
                    .append(DyeColor.BLUE).append(" is damaged ").append(Color.fromRGB(255, 255, 0))
                    .append(Translations.getItemDurability(20, 30)).append(" ciao ").newLine()
                    .append(ChatColor.BLUE).append("aaaaa").send();
        }

        @Override
        public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location loc) {
            return null;
        }
    }

    public void reload() {
    }

    protected boolean registerReloadCommand() {
        return false;
    }

    @Override
    public void disable() {
        ConfigurationSerialization.unregisterClass(SoundInfo.class);
        this.logDone("Unregistered &eSoundInfo&f from ConfigurationSerialization");
    }
}
