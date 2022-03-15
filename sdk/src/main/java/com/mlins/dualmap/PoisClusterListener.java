package com.mlins.dualmap;

import com.spreo.nav.interfaces.IPoi;

import java.util.List;

public interface PoisClusterListener {
    void poiListDelivered(List<IPoi> withLabels, List<IPoi> withoutLabels);
}
