package emanondev.core;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
            //this.map.put(key, result); //TODO i wanna die
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
        if (path == null || path.isEmpty())
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

    /*
     * private final YMLConfig parent; private final String subPath;
     *
     *
     * YMLSubSection(ConfigurationSection section) { if (section==null) throw new
     * NullPointerException(); this.section = section; }
     *
     * public YMLSubSection(YMLConfig parent,String path) { if (path==null ||
     * parent==null) throw new NullPointerException(); if (path.isEmpty()) throw new
     * IllegalArgumentException(); this.parent = parent; this.subPath = path; }
     *
     * @Override public String getFileName() { return parent.getFileName(); }
     *
     * @Override public JavaPlugin getPlugin() { return parent.getPlugin(); }
     *
     * @Override public boolean reload() { return parent.reload(); }
     *
     * @Override public void save() { parent.save(); }
     *
     * @Override public void saveAsync() { parent.saveAsync(); }
     *
     * @Override public File getFile() { return parent.getFile(); }
     *
     * @Override public Set<String> getKeys(String path) { if (path==null ||
     * path.isEmpty()) return parent.getKeys(subPath); return
     * parent.getKeys(subPath+"."+path); }
     *
     * @Override public boolean isDirty() { return parent.isDirty(); }
     *
     * @Override public void set(String path, Object value) { if (path==null ||
     * path.isEmpty()) throw new IllegalArgumentException();
     * parent.set(subPath+"."+path,value); }
     *
     * @Override public Object get(String path) { if (path==null || path.isEmpty())
     * throw new IllegalArgumentException(); return parent.get(subPath+"."+path); }
     *
     * @Override public boolean contains(String path) { if (path==null ||
     * path.isEmpty()) throw new IllegalArgumentException(); return
     * parent.contains(subPath+"."+path); }
     *
     * @Override public YMLSection getSubSection(String path) { if (path==null ||
     * path.isEmpty()) return this; return parent.getSubSection(subPath+"."+path); }
     */
}
