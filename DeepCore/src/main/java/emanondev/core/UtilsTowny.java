package emanondev.core;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;

public class UtilsTowny {
	
	@SuppressWarnings("deprecation")
	public static @Nullable Resident getResident(Player p) {
		try {
			return TownyAPI.getInstance().getDataSource().getResident(p.getName());
		} catch (NotRegisteredException e) {
			return null;
		}
	}
	
	public static @Nullable Town getTown(Player p) {
		try {
			Resident r = getResident(p);
			return r.hasTown()?r.getTown():null;
		}catch (Exception e) {
			return null;
		}
	}

	public static @Nullable Town getTown(TownBlock tBlock) {
		if (tBlock==null)
			return null;
		try {
			return tBlock.hasTown()?tBlock.getTown():null;
		}catch (Exception e) {
			return null;
		}
	}
	public static @Nullable Nation getNation(Town t) {
		if (t==null)
			return null;
		try {
			return t.hasNation()?t.getNation():null;
		}catch (Exception e) {
			return null;
		}
	}
	
	public static @Nullable Nation getNation(Player p) {
		try {
			return getNation(getTown(p));
		}catch (Exception e) {
			return null;
		}
	}
	
	public static @Nullable TownBlock getLocationTownBlock(Location l) {
		try {
			return TownyAPI.getInstance().getTownBlock(l);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static @Nullable Town getLocationTown(Location l) {
		try {
			return TownyAPI.getInstance().getTown(l);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static @Nullable Nation getLocationNation(Location l) {
		try {
			return getNation(getLocationTown(l));
		}catch (Exception e) {
			return null;
		}
	}

	public static @Nullable TownBlock getLocationTownBlock(Block b) {
		return getLocationTownBlock(b.getLocation());
	}
	
	public static @Nullable Town getLocationTown(Block b) {
		return getLocationTown(b.getLocation());
	}
	
	public static @Nullable Nation getLocationNation(Block b) {
		return getLocationNation(b.getLocation());
	}
	
	public static boolean hasTown(Player p) {
		try{
			return getTown(p)!=null;
		}catch (Exception e) {
			return false;
		}
	}
	public static boolean hasNation(Player p) {
		try{
			return getNation(p)!=null;
		}catch (Exception e) {
			return false;
		}
	}
	public static boolean areSameTown(Player p,Player p2) {
		if (p==null || p2==null)
			return false;
		try {
			Town t = getTown(p);
			if (t==null)
				return false;
			return t.equals(getTown(p2));
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean areSameNation(Player p,Player p2) {
		if (p==null || p2==null)
			return false;
		try {
			Town t = getTown(p);
			if (t==null)
				return false;
			return areSameNation(t,getTown(p2));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean areSameNation(Town t,Town t2) {
		if (t==null || t2==null)
			return false;
		try {
			Nation n = getNation(t);
			if (n==null)
				return false;
			return n.equals(getNation(t2));
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean areAlliedNations(Player p,Player p2) {
		if (p==null || p2==null)
			return false;
		try {
			return areAlliedNations(getTown(p),getTown(p2));
		} catch (Exception e) {
			return false;
		}
	}
	public static boolean areAlliedNations(Town t,Town t2) {
		if (t==null || t2==null)
			return false;
		try {
			if (t.equals(t2))
				return true;
			return areAlliedNations(getNation(t),getNation(t2));
		} catch (Exception e) {
			return false;
		}
	}
	public static boolean areAlliedNations(Nation n,Nation n2) {
		if (n==null || n2==null)
			return false;
		try {
			if (n.equals(n2))
				return true;
			if (n.isAlliedWith(n2) && n2.isAlliedWith(n))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
