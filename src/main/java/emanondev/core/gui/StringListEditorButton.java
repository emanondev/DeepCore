package emanondev.core.gui;

import emanondev.core.CoreMain;
import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class StringListEditorButton extends AGuiButton {

    private Action action = Action.ADD_LINE;
    private int line;
    private String subTitle = "&9Line editor";
    private ItemStack baseItem = new ItemBuilder(Material.PAPER).setGuiProperty().build();

    public StringListEditorButton(Gui parent) {
        super(parent);
    }

    public void setLineEditorTitle(String value) {
        subTitle = value;
    }

    public void setBaseItem(ItemStack item) {
        baseItem = item;
    }

    public abstract List<String> getValue();

    public abstract void onChange(List<String> list);

    @Override
    public boolean onClick(@NotNull InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT:
                setLine(getLine() - 1);
                return true;
            case RIGHT:
                setLine(getLine() + 1);
                return true;
            case MIDDLE:
                applyAction();
                return true;
            case SHIFT_LEFT:
                action = Action.values()[(action.ordinal() - 1 + Action.values().length) % Action.values().length];
                return true;
            case SHIFT_RIGHT:
                action = Action.values()[(action.ordinal() + 1 + Action.values().length) % Action.values().length];
                return true;
            default:
                break;
        }
        return false;
    }

    private void setLine(int value) {
        line = value;
    }

    private int getLine() {
        List<String> value = getValue();
        if (value == null) {
            action = Action.ADD_LINE;
            return 0;
        }
        int r = Math.min(value.size(), Math.max(0, line));
        if (r == value.size())
            action = Action.ADD_LINE;
        if (r == 0 && action == Action.MOVE_UP)
            action = Action.MOVE_DOWN;
        return r;
    }

    void applyAction() {
        List<String> list = getValue();
        if (list == null)
            list = new ArrayList<>();
        else
            list = new ArrayList<>(list);
        int line = getLine();
        switch (action) {
            case ADD_LINE:
                try {
                    if (line >= list.size())
                        list.add("&f");
                    else
                        list.add(line, "&f");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.onChange(list);
                break;
            case DELETE_LINE:
                try {
                    list.remove(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.onChange(list);
                break;
            case EDIT_LINE:
                final List<String> temp = list;
                new TextEditorGui(subTitle, list.get(line), getTargetPlayer(), this.getGui(), getPlugin()) {

                    @Override
                    public void onTextConfirmed(String lineText) {
                        temp.set(line, lineText);
                        StringListEditorButton.this.onChange(temp);
                        StringListEditorButton.this.getGui().open(getTargetPlayer());
                    }

                }.open(getTargetPlayer());
                break;
            case MOVE_DOWN:
                try {
                    if (line + 1 >= list.size())
                        list.add(line, "");
                    list.add(line, list.remove(line));
                    setLine(line + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.onChange(list);
                break;
            case MOVE_UP:
                try {
                    if (line - 1 < 0) {
                        action = Action.MOVE_DOWN;
                        getGui().updateInventory();
                        return;
                    }
                    list.add(line - 1, list.remove(line));
                    setLine(line - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.onChange(list);
                break;
            default:
                throw new IllegalStateException();
        }

    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = getBaseItem();
        if (item == null)
            return null;
        return new ItemBuilder(item).setDescription(getDescription()).build();
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        List<String> tmp = getBaseDescription();
        if (tmp != null)
            desc.addAll(tmp);
        tmp = getValueDescription();
        if (tmp != null)
            desc.addAll(tmp);
        tmp = getInstructionsDescription();
        if (tmp != null)
            desc.addAll(tmp);
        return UtilsString.fix(desc, null, true, "%action_symbol%", action.getSymbol(getTargetPlayer()), "%action_description%",
                action.getDescription(getTargetPlayer()), "%action_line%", actionLine());
    }

    public List<String> getBaseDescription() {
        return List.of("&6&lText:");
    }

    public List<String> getValueDescription() {
        List<String> val = getValue();
        if (val == null)
            val = new ArrayList<>();
        else
            val = new ArrayList<>(val);
        val.add("");
        int line = getLine();
        for (int i = 0; i < val.size(); i++) {
            if (i != line)
                val.set(i, "   &f" + val.get(i));
            else
                val.set(i, "%action_symbol% &f" + val.get(i));
        }
        return val;
    }

    public List<String> getInstructionsDescription() {
        return Arrays.asList("&7[&fClick&7] &9Left&b/&9Right &7> &9Change line",
                "&7[&fClick&7] &9Shift Left&b/&9Right &7> &9Change action",
                "&7[&fClick&7] &9Middle &7> %action_symbol% &9%action_description%", "%action_line%");
    }

    private String actionLine() {
        return "" +
                (action == Action.ADD_LINE ? "&b[" + action.getSymbol(getTargetPlayer()) + "&b] "
                        : "&8[" + Action.ADD_LINE.getSymbol(getTargetPlayer()) + "&8] ") +
                (action == Action.EDIT_LINE ? "&b[" + action.getSymbol(getTargetPlayer()) + "&b] "
                        : "&8[" + Action.EDIT_LINE.getSymbol(getTargetPlayer()) + "&8] ") +
                (action == Action.DELETE_LINE ? "&b[" + action.getSymbol(getTargetPlayer()) + "&b] "
                        : "&8[" + Action.DELETE_LINE.getSymbol(getTargetPlayer()) + "&8] ") +
                (action == Action.MOVE_DOWN ? "&b[" + action.getSymbol(getTargetPlayer()) + "&b] "
                        : "&8[" + Action.MOVE_DOWN.getSymbol(getTargetPlayer()) + "&8] ") +
                (action == Action.MOVE_UP ? "&b[" + action.getSymbol(getTargetPlayer()) + "&b] "
                        : "&8[" + Action.MOVE_UP.getSymbol(getTargetPlayer()) + "&8]");
    }

    private enum Action {
        ADD_LINE, EDIT_LINE, DELETE_LINE,
        MOVE_DOWN, MOVE_UP;

        private String getSymbol(CommandSender sender) {
            return CoreMain.get().getLanguageConfig(sender)
                    .loadString("gui_button.string_list_editor." + this.name().toLowerCase() + ".symbol", "@");
        }

        private String getDescription(CommandSender sender) {
            return CoreMain.get().getLanguageConfig(sender)
                    .loadString("gui_button.string_list_editor." + this.name().toLowerCase() + ".desc", "description");
        }

    }

}
