package emanondev.core;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class UtilsTowny {

    private UtilsTowny() {
        throw new AssertionError();
    }

    public static @Nullable Resident getResident(@NotNull OfflinePlayer p) {
        return TownyUniverse.getInstance().getResident(p.getUniqueId());
    }

    public static @NotNull Collection<Town> getTowns() {
        return TownyUniverse.getInstance().getTowns();
    }

    public static @NotNull Collection<Nation> getNations() {
        return TownyUniverse.getInstance().getNations();
    }

    public static @Nullable Town getTown(@NotNull OfflinePlayer p) {
        try {
            Resident r = getResident(p);
            return r != null && r.hasTown() ? r.getTown() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static @Nullable Town getTown(@Nullable TownBlock tBlock) {
        if (tBlock == null)
            return null;
        try {
            return tBlock.hasTown() ? tBlock.getTown() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static @Nullable Nation getNation(@Nullable Town t) {
        if (t == null)
            return null;
        try {
            return t.hasNation() ? t.getNation() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static @Nullable Nation getNation(@NotNull OfflinePlayer p) {
        try {
            Resident r = getResident(p);
            return r.hasNation() ? r.getNation() : null;
        } catch (Exception e) {
            return null;
        }
    }


    public static @Nullable Nation getNation(@Nullable TownBlock tBlock) {
        Town t = getTown(tBlock);
        if (t == null)
            return null;
        try {
            return t.hasNation() ? t.getNation() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static @Nullable TownBlock getTownBlock(Location l) {
        try {
            return TownyAPI.getInstance().getTownBlock(l);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see #getTownBlock(Location)
     */
    @Deprecated
    public static @Nullable TownBlock getLocationTownBlock(Location l) {
        return getTownBlock(l);
    }

    public static @Nullable Town getTown(Location l) {
        try {
            return TownyAPI.getInstance().getTown(l);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * @see #getTown(Location)
     */
    @Deprecated
    public static @Nullable Town getLocationTown(Location l) {
        return getTown(l);
    }

    public static @Nullable Nation getNation(Location l) {
        return getNation(getTown(l));
    }

    /**
     * @see #getNation(Location)
     */
    @Deprecated
    public static @Nullable Nation getLocationNation(Location l) {
        return getNation(l);
    }

    public static @Nullable TownBlock getTownBlock(Block b) {
        return getTownBlock(b.getLocation());
    }

    public static @Nullable Town getTown(Block b) {
        return getTown(b.getLocation());
    }

    public static @Nullable Nation getNation(Block b) {
        return getNation(b.getLocation());
    }

    /**
     * @see #getTownBlock(Block)
     */
    @Deprecated
    public static @Nullable TownBlock getLocationTownBlock(Block b) {
        return getLocationTownBlock(b.getLocation());
    }

    /**
     * @see #getTown(Block)
     */
    @Deprecated
    public static @Nullable Town getLocationTown(Block b) {
        return getLocationTown(b.getLocation());
    }

    /**
     * @see #getNation(Block)
     */
    @Deprecated
    public static @Nullable Nation getLocationNation(Block b) {
        return getLocationNation(b.getLocation());
    }

    public static boolean hasTown(@NotNull OfflinePlayer p) {
        return getTown(p) != null;
    }

    public static boolean hasNation(Player p) {
        return getNation(p) != null;
    }

    public static boolean areSameTown(Player p, Player p2) {
        if (p == null || p2 == null)
            return false;
        try {
            Town t = getTown(p);
            if (t == null)
                return false;
            return t.equals(getTown(p2));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean areSameNation(Player p, Player p2) {
        if (p == null || p2 == null)
            return false;
        try {
            Town t = getTown(p);
            if (t == null)
                return false;
            return areSameNation(t, getTown(p2));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean areSameNation(Town t, Town t2) {
        if (t == null || t2 == null)
            return false;
        try {
            Nation n = getNation(t);
            if (n == null)
                return false;
            return n.equals(getNation(t2));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean areAlliedNations(@NotNull OfflinePlayer p, @NotNull OfflinePlayer p2) {
        try {
            return areAlliedNations(getTown(p), getTown(p2));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean areAlliedNations(Town t, Town t2) {
        if (t == null || t2 == null)
            return false;
        try {
            if (t.equals(t2))
                return true;
            return areAlliedNations(getNation(t), getNation(t2));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean areAlliedNations(Nation n, Nation n2) {
        if (n == null || n2 == null)
            return false;
        try {
            if (n.equals(n2))
                return true;
            return n.isAlliedWith(n2) && n2.isAlliedWith(n);
        } catch (Exception e) {
            return false;
        }
    }

}
