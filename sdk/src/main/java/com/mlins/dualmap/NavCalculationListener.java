package com.mlins.dualmap;

import com.spreo.enums.NavigationResultStatus;

public interface NavCalculationListener {
    void OnNavigationCalculationFinished(boolean isSimulation, NavigationResultStatus code, boolean isReroute);
}
