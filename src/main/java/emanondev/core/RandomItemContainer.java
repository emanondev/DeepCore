package emanondev.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomItemContainer<T> {

    private final List<T> items = new ArrayList<>();
    private final List<Integer> weights = new ArrayList<>();
    private long fullWeight = 0;

    /**
     * Adds selected items, weight is 100 by default
     *
     * @param items items to add
     */
    public void addItems(@NotNull Collection<T> items) {
        addItems(items, 100);
    }

    /**
     * @param items  items to add
     * @param weight weight of the items, must be positive
     */
    public void addItems(@NotNull Collection<T> items, int weight) {
        for (T item : items)
            addItem(item, weight);
    }

    /**
     * Adds selected item, weight is 100 by default
     *
     * @param item item to add
     */
    public void addItem(T item) {
        addItem(item, 100);
    }

    public int getWeight(T item){
        int index = items.indexOf(item);
        if (index==-1)
            return 0;
        return weights.get(index);
    }


    public void addItem(T item, int weight) {
        int before = getWeight(item);
        int after = Math.max(before+weight,0);
        int index = items.indexOf(item);
        if (after==0){
            deleteItem(item);
            return;
        }
        if (index==-1) {
            items.add(item);
            weights.add(after);
            fullWeight += after;
        }else{
            weights.set(index,after);
            fullWeight += (after-before);
        }
    }

    public boolean deleteItem(T item) {
        int index = items.indexOf(item);
        if (index < 0)
            return false;
        items.remove(index);
        fullWeight -= weights.remove(index);
        return true;
    }

    public boolean setWeight(T item, int weight) {
        if (!deleteItem(item))
            return false;
        addItem(item, weight);
        return true;
    }

    public T getItem() {
        if (items.isEmpty())
            return null;
        int val = (int) (Math.random() * fullWeight);
        for (int i = 0; i < weights.size(); i++) {
            val -= weights.get(i);
            if (val < 0)
                return items.get(i);
        }
        new IllegalStateException("Invalid state").printStackTrace();
        return items.get(items.size() - 1);
    }

    @NotNull
    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    @NotNull
    public List<Integer> getWeights() {
        return Collections.unmodifiableList(weights);
    }

    public long getFullWeight() {
        return fullWeight;
    }

}
