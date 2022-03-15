package com.mlins.view.ctrls;

import com.mlins.aStar.aStarData;
import com.mlins.locator.LocationLocator;

public class NavButton extends ButtonObject {

    public void OnClick() {
        aStarData.getInstance().cleanAStar();
        LocationLocator.getInstance().resetNavState();

//		Intent intent = new Intent (context, NavigationSetting.class); 
//        context.startActivity(intent);
    }
}
