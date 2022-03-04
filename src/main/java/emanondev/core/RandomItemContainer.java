package emanondev.core;

import java.util.*;

public class RandomItemContainer<T> {

    private final List<T> items = new ArrayList<>();
    private final List<Integer> weights = new ArrayList<>();
    private int fullWeight = 0;

    public void addItems(Collection<T> items) {
        addItems(items, 100);
    }

    public void addItems(Collection<T> items, int weight) {
        for (T item : items)
            addItem(item, weight);
    }

    public void addItem(T item) {
        addItem(item, 100);
    }

    public void addItem(T item, int weight) {
        if (weight <= 0)
            throw new IllegalArgumentException("Invalid weight");
        items.add(item);
        weights.add(weight);
        fullWeight += weight;
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
        if (weight <= 0)
            throw new IllegalArgumentException("Invalid weight");
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

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<Integer> getWeights() {
        return Collections.unmodifiableList(weights);
    }

}
