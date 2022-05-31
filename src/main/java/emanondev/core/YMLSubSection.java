package emanondev.core;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class YMLSubSection extends MemorySection implements YMLSection {

    protected YMLSubSection() {
        super();
    }

    protected YMLSubSection(@NotNull YMLSection parent, @NotNull String path) {
        super(parent, path);
    }

    public YMLConfig getRoot() {
        return (YMLConfig) super.getRoot();
    }

    @Override
    public void set(@NotNull String path, Object value) {
        super.set(path, value);
        getRoot().setDirty();
        if (getRoot().getAutosaveOnSet())
            if (getRoot().getPlugin().isEnabled())
                saveAsync();
            else
                save();
    }

    @Override
    public void setNoDirty(@NotNull String path, Object value) {
        super.set(path, value);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    public YMLSection createSection(@NotNull String path) {
        Validate.notEmpty(path, "Cannot create section at empty path");
        Configuration root = getRoot();
        if (root == null) {
            throw new IllegalStateException("Cannot create section without a root");
        }

        char separator = root.options().pathSeparator();

        int i1 = -1;
        YMLSection section = this;
        int i2;
        while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            YMLSection subSection = section.getConfigurationSection(node);
            if (subSection == null) {
                section = section.createSection(node);
                continue;
            }
            section = subSection;
        }

        String key = path.substring(i2);
        if (section == this) {
            YMLSection result = new YMLSubSection(this, key);
            this.set(key, result);
            return result;
        }
        return section.createSection(key);
    }

    @NotNull
    public YMLSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return (YMLSection) super.createSection(path, map);
    }

    @Override
    public String getFileName() {
        return getRoot().getFileName();
    }

    @Override
    public JavaPlugin getPlugin() {
        return getRoot().getPlugin();
    }

    @Override
    public boolean reload() {
        return getRoot().reload();
    }

    @Override
    public void save() {
        getRoot().save();
    }

    @Override
    public void saveAsync() {
        getRoot().saveAsync();
    }

    @Override
    public @NotNull File getFile() {
        return getRoot().getFile();
    }

    @Override
    public @NotNull Set<String> getKeys(@NotNull String path) {
        if (path.isEmpty())
            return getKeys(false);
        ConfigurationSection section = this.getConfigurationSection(path);
        if (section == null)
            return new LinkedHashSet<>();
        else
            return section.getKeys(false);
    }

    @Override
    public boolean isDirty() {
        return getRoot().isDirty();
    }

    @Override
    public YMLSection getConfigurationSection(@NotNull String path) {
        return (YMLSection) super.getConfigurationSection(path);
    }

    @Override
    public YMLSection getParent() {
        return (YMLSection) super.getParent();
    }

    @Override
    public YMLSection getDefaultSection() {
        return (YMLSection) super.getDefaultSection();
    }

}
