package com.spreo.interfaces;

import com.spreo.enums.NavigationResultStatus;
import com.spreo.nav.enums.NavigationState;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;

import java.util.List;

public interface SpreoNavigationListener {

    /**
     * An event is triggered to listener that the navigation is changed his state
     *
     * @param navigationState
     */
    void onNavigationStateChanged(NavigationState navigationState);

    /**
     * An event is triggered to listener that the navigation is arrived to a specific IPoi
     *
     * @param arrivedToPoi
     */
    void onNavigationArriveToPoi(IPoi arrivedToPoi, List<IPoi> nextPois);

    /**
     * This method will be called when the map detects change in the navigation instructions.
     *
     * @param instruction
     */
    void OnNavigationInstructionChanged(INavInstruction instruction);

    /**
     * This method will be called when the map detects entrance to the range the navigation instructions.
     *
     * @param instruction
     */
    void onInstructionRangeEntered(INavInstruction instruction);

    /**
     * This method will be called when the navigation process failed.
     */
    void OnNavigationFailed(NavigationResultStatus status);

}
