package emanondev.core.util;

import com.fastasyncworldedit.core.extent.clipboard.DiskOptimizedClipboard;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import emanondev.core.CoreMain;
import emanondev.core.YMLConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FAWECleaner {

    //private static final HashSet<Path> paths = new HashSet<>();

    private static final YMLConfig config = new YMLConfig(CoreMain.get(), "FAWE.yml");

    static void clean(Clipboard clip) {
        if (clip instanceof BlockArrayClipboard bClip)
            clip = bClip.getParent();
        if (clip instanceof DiskOptimizedClipboard dClip) {
            try {
                File file = dClip.getFile();
                if (file.isFile())
                    Files.delete(file.toPath());
            } catch (Exception e) {
                track(clip);
                //e.printStackTrace();
            }
        }
    }

    public static void cleanAll() {
        List<String> newList = new ArrayList<>();
        for (String rawPath : config.loadStringList("to_remove", Collections.emptyList())) {
            try {
                File file = new File(rawPath);
                if (file.isFile())
                    Files.delete(file.toPath());
            } catch (Throwable e) {
                e.printStackTrace();
                newList.add(rawPath);
            }
        }
        config.set("to_remove", newList);
    }

    static void track(Clipboard clip) {
        if (clip instanceof BlockArrayClipboard bClip)
            clip = bClip.getParent();
        if (clip instanceof DiskOptimizedClipboard dClip) {
            File file = dClip.getFile();
            try {
                if (file != null) {
                    ArrayList<String> list = new ArrayList<>(config.loadStringList("to_remove", Collections.emptyList()));
                    list.add(file.getAbsolutePath());
                    config.set("to_remove", list);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}















