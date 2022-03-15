package com.spreo.interfaces;

import java.util.List;

public interface LocationSharingListener {
    void onLocationsUpdate(List<ILocationSharingUser> users);
}
