package com.mlins.utils.sort;

import com.spreo.nav.interfaces.IPoi;

import java.util.List;

public abstract class SortResultItem extends WeightedItem {

    SortResultItem(double weight) {
        super(weight);
    }

    public abstract void addToResultsList(List<IPoi> list);

}
