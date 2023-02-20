package emanondev.core.util;

import emanondev.core.Hooks;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.core.util.items.DrcGuiItemConverter;
import emanondev.core.util.items.ItemConverter;
import emanondev.core.util.items.ItemEditItemConverter;
import emanondev.core.util.items.MythicMobsItemConverter;

import java.util.HashSet;

public class ItemUtility {

    private ItemUtility() {

    }

    private static final HashSet<ItemConverter> converters = new HashSet<>();

    public static ItemBuilder convertItem(YMLSection section) {
        if (section == null)
            return null;
        for (ItemConverter conv : converters) {
            try {
                ItemBuilder b = conv.convert(section);
                if (b != null)
                    return b;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void addConverter(ItemConverter converter) {
        if (converter == null)
            return;
        converters.add(converter);
    }

    public static void initialize() {
        if (Hooks.isEnabled("ItemEdit"))
            converters.add(new ItemEditItemConverter());
        if (Hooks.isEnabled("DracarysGUI"))
            converters.add(new DrcGuiItemConverter());
        if (Hooks.isEnabled("MythicMobs"))
            converters.add(new MythicMobsItemConverter());
    }
}

