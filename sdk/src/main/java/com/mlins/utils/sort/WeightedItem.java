package com.mlins.utils.sort;


import androidx.annotation.NonNull;

class WeightedItem implements Comparable<WeightedItem> {

    private final double weight;

    WeightedItem(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(@NonNull WeightedItem o) {
        return weight - o.weight > 0 ? 1 : -1;
    }
}
