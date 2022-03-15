package com.mlins.view.ctrls;

import android.graphics.PointF;

import com.mlins.locator.LocationLocator;
import com.mlins.utils.AnimationsHolder;
import com.mlins.utils.PropertyHolder;
import com.mlins.views.d2dTouchImageView;
import com.spreo.nav.enums.MapRotationType;

public class TurnMap extends ButtonObject {
    public void OnClick() {
        d2dTouchImageView map = (d2dTouchImageView) parent;
        // if (PropertyHolder.getInstance().isRotatingMap()) {
        if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
            map.setCompassMode(true, true);
        }

        AnimationsHolder.getInstance().reset();
        AnimationsHolder.releaseInstance();

        map.setFollowMeMode(true);
        PointF location = LocationLocator.getInstance().getCurrentLock();
        map.SetCenter(location);
//		 map.setZoom(3);
        if (isHidden == false) {
            setIsHidden(true);

        }
    }

}
