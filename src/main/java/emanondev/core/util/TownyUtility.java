package emanondev.core.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class TownyUtility {

    public static @NotNull TownyRelation getRelation(@NotNull OfflinePlayer p1,@NotNull OfflinePlayer p2){
        return getRelation(TownyAPI.getInstance().getResident(p1.getUniqueId()),
                TownyAPI.getInstance().getResident(p2.getUniqueId()));
    }

    public static @NotNull TownyRelation getRelation(@Nullable Resident r1, @Nullable Resident r2){
        if (r1==null||r2==null)
            return TownyRelation.NONE;
        return TownyRelation.from(r1.getTownOrNull(),r2.getTownOrNull());
    }
    public static @NotNull TownyRelation getRelation(@NotNull OfflinePlayer p1,@Nullable  Town t2){
        return getRelation(TownyAPI.getInstance().getResident(p1.getUniqueId()),t2);
    }
    public static @NotNull TownyRelation getRelation(@Nullable Town t1,@Nullable Town t2){
        return TownyRelation.from(t1,t2);
    }
    public static @NotNull TownyRelation getRelation(@NotNull Player p1){
        return getRelation(p1,getTownBlock(p1));
    }
    public static @NotNull TownyRelation getRelation(@NotNull Player p1, @Nullable TownBlock t2){
        return getRelation(getResident(p1),t2);
    }
    public static @NotNull TownyRelation getRelation(@Nullable Resident p1, @Nullable TownBlock t2){
        if (p1 == null || t2==null)
            return TownyRelation.NONE;
        return TownyRelation.from(p1.getTownOrNull(),t2.getTownOrNull());
    }
    public static @NotNull TownyRelation getRelation(@Nullable TownBlock t1,@Nullable TownBlock t2){
        if (t1==null || t2==null)
            return TownyRelation.NONE;
        return TownyRelation.from(t1.getTownOrNull(),t2.getTownOrNull());
    }
    public static @NotNull TownyRelation getRelation(@Nullable Resident r1,@Nullable Town t2){
        if (r1==null)
            return TownyRelation.NONE;
        return TownyRelation.from(r1.getTownOrNull(),t2);
    }
    public static @Nullable Town getTown(@NotNull OfflinePlayer p){
        Resident r = getResident(p);
        return r==null?null:r.getTownOrNull();
    }
    public static @Nullable Nation getNation(@NotNull OfflinePlayer p){
        Resident r = getResident(p);
        return r==null?null:r.getNationOrNull();
    }
    public static @Nullable Resident getResident(@NotNull OfflinePlayer p){
        return TownyAPI.getInstance().getResident(p.getUniqueId());
    }
    public static @Nullable TownBlock getTownBlock(@NotNull Entity e){
        return getTownBlock(e.getLocation());
    }
    public static @Nullable TownBlock getTownBlock(@NotNull Location loc){
        return TownyAPI.getInstance().getTownBlock(loc);
    }
    public static @Nullable TownBlock getTownBlock(@NotNull Block block){
        return TownyAPI.getInstance().getTownBlock(block.getLocation());
    }
    public static @NotNull Collection<Player> getOnlinePlayers(@NotNull ResidentList town){
        return TownyAPI.getInstance().getOnlinePlayers(town);
    }
    @Contract("!null -> !null; null -> null")
    public static @Nullable Government getGovernament(@Nullable Town t){
        return t==null?null:(t.hasNation()?t.getNationOrNull():t);
    }
    public static @Nullable Government getGovernament(@NotNull Player p){
        return getGovernament(getTown(p));
    }
    public static @NotNull Collection<Town> getTowns(){
        return TownyAPI.getInstance().getTowns();
    }

    public static @NotNull Collection<Nation> getNations(){
        return TownyAPI.getInstance().getNations();
    }
    public static boolean isInsideClaims(@NotNull Entity entity,@NotNull Town town){
        return isInsideClaims(entity.getLocation(),town);
    }
    public static boolean isInsideClaims(@NotNull Location location,@NotNull Town town){
        @Nullable TownBlock tb = getTownBlock(location);
        return tb!=null && town.equals(tb.getTownOrNull());
    }
    public static boolean isHomeBlock(@NotNull Entity entity, @NotNull Town town) {
        return isHomeBlock(entity.getLocation(), town);
    }

    public static boolean isHomeBlock(@NotNull Location location, @NotNull Town town) {
        TownBlock tb = getTownBlock(location);
        return tb != null && town.equals(tb.getTownOrNull()) && tb.isHomeBlock();
    }

    public enum TownyRelation {
        NONE,
        ENEMY_NATION,
        SAME_TOWN,
        SAME_NATION,
        ALLIED_NATION;

        public static TownyRelation from(Town t1,Town t2){
            if (t1==null||t2==null)
                return NONE;
            if (t1.equals(t2))
                return SAME_TOWN;
            Nation n1 = t1.getNationOrNull();
            Nation n2 = t2.getNationOrNull();
            if (n1==null || n2==null)
                return NONE;
            if (n1.equals(n2))
                return SAME_NATION;
            if (n1.isAlliedWith(n2) && n2.isAlliedWith(n1))
                return ALLIED_NATION;
            if (n1.hasEnemy(n2) || n2.hasEnemy(n1))
                return ENEMY_NATION;
            return NONE;
        }

        public boolean isSameTown(){
            return this==SAME_TOWN;
        }
        public boolean isSameTownOrNation(){
            return this==SAME_TOWN || this==SAME_NATION;
        }
        public boolean isAllied(){
            return switch (this){
                case SAME_TOWN, SAME_NATION,ALLIED_NATION -> true;
                default -> false;
            };
        }
        public boolean isEnemyNation(){
            return this==ENEMY_NATION;
        }
    }


}
