package emanondev.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public abstract class DRegistryElement {

    private final String id;

    /**
     * @throws IllegalArgumentException if name doesn't match <code>[a-zA-Z][_a-zA-Z0-9]*</code>
     */
    public DRegistryElement(@NotNull String id) {
        if (!Pattern.compile("[a-z][_a-z0-9]+").matcher(id).matches())
            throw new IllegalArgumentException();
        this.id = id;
    }

    public final @NotNull String getId() {
        return this.id;
    }

    public void reload() {
    }

    void onRegistered(DRegistry<?> registry) {
    }

    void onUnregistered(DRegistry<?> registry) {
    }
}
