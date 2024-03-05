package emanondev.core.util;

import com.fastasyncworldedit.core.extent.clipboard.DiskOptimizedClipboard;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class FAWECleaner {

    private static final HashSet<Path> paths = new HashSet<>();

    static void clean(Clipboard clip) {
        if (clip instanceof BlockArrayClipboard bClip)
            clip = bClip.getParent();
        if (clip instanceof DiskOptimizedClipboard dClip) {
            try {
                File file = dClip.getFile();
                if (file.isFile())
                    Files.delete(file.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void cleanAll() {
        for (Path path : paths) {
            try {
                File file = path.toFile();
                if (file.isFile())
                    Files.delete(path);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static void track(Clipboard clip) {
        if (clip instanceof BlockArrayClipboard bClip)
            clip = bClip.getParent();
        if (clip instanceof DiskOptimizedClipboard dClip) {
            try {
                File file = dClip.getFile();
                if (file != null)
                    paths.add(file.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}















