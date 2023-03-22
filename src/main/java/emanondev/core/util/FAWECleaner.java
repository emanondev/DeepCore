package emanondev.core.util;

import com.fastasyncworldedit.core.configuration.Settings;
import com.fastasyncworldedit.core.extent.clipboard.DiskOptimizedClipboard;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class FAWECleaner {
    /*

    public static HashMap<String, List<Path>> files = new HashMap<>();

    public static void track(Clipboard clip, Plugin plugin) {
        if (Settings.settings().CLIPBOARD.USE_DISK && clip instanceof DiskOptimizedClipboard diskClip) {
            files.putIfAbsent(plugin.getName(), new ArrayList<>());
            files.get(plugin.getName()).add(diskClip.getFile().toPath());
        }
    }

    public static void clean(Plugin plugin) {
        for (Path path : files.getOrDefault(plugin.getName(), Collections.emptyList())) {
            try {
                File file = path.toFile();
                if (file.isFile())
                    Files.delete(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        files.remove(plugin.getName());
    }

    public static void cleanAll() {
        for (List<Path> paths : files.values())
            for (Path path : paths) {
                try {
                    File file = path.toFile();
                    if (file.isFile())
                        Files.delete(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        files.clear();
    }

    public static void track2(BlockArrayClipboard clipboard, Plugin plugin) {
        track(clipboard.getParent(), plugin);
    }*/

    public static void clean(Clipboard clip) {
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
}















