package emanondev.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WorldEditUtility {

	public static Clipboard load(File dest) {
		ClipboardFormat format = ClipboardFormats.findByFile(dest);
		try (ClipboardReader reader = format.getReader(new FileInputStream(dest))) {
			Clipboard clipboard = reader.read();
			return clipboard;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Clipboard copy(Location corner1, Location corner2, boolean copyEntity, boolean copyBiomes) {
		if (!corner1.getWorld().equals(corner2.getWorld()))
			throw new IllegalArgumentException();
		return copy(corner1.getWorld(), BoundingBox.of(corner1, corner2), copyEntity, copyBiomes);
	}

	public static Clipboard copy(World w, BoundingBox area, boolean copyEntity, boolean copyBiomes) {
		BlockVector3 min = BlockVector3.at((int) area.getMinX(), (int) area.getMinY(), (int) area.getMinZ());

		CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(w), min,
				BlockVector3.at((int) area.getMaxX(), (int) area.getMaxY(), (int) area.getMaxZ()));
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
		clipboard.setOrigin(min);
		EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(w))
				.maxBlocks(-1).build();

		ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
				region.getMinimumPoint());
		forwardExtentCopy.setCopyingEntities(copyEntity);
		forwardExtentCopy.setCopyingBiomes(copyBiomes);
		try {
			Operations.complete(forwardExtentCopy);
		} catch (WorldEditException e) {
			e.printStackTrace();
			return null;
		}
		return clipboard;
	}

	public static boolean paste(Location dest, Clipboard clip) {
		try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
				.world(BukkitAdapter.adapt(dest.getWorld())).maxBlocks(-1).build()) {
			Operation operation = new ClipboardHolder(clip).createPaste(editSession)
					.to(BlockVector3.at(dest.getBlockX(), dest.getBlockY(), dest.getBlockZ())).ignoreAirBlocks(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

	public static boolean paste(Location dest, Clipboard clip,double rotationDegree) {
		try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
				.world(BukkitAdapter.adapt(dest.getWorld())).maxBlocks(-1).build()) {
			ClipboardHolder holder = new ClipboardHolder(clip);
			if (rotationDegree!=0) {
				holder.setTransform(new AffineTransform().rotateY(-rotationDegree));
			}
			Operation operation = holder.createPaste(editSession)
					.to(BlockVector3.at(dest.getBlockX(), dest.getBlockY(), dest.getBlockZ())).ignoreAirBlocks(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean save(File dest, Clipboard clip) {
		dest.getParentFile().mkdirs();
		try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(dest))) {
			writer.write(clip);
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
}
