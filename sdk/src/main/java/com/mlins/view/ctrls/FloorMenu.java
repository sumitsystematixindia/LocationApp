package com.mlins.view.ctrls;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;

public class FloorMenu extends ButtonObject {
    private FloorList floorList;

    public FloorMenu(FloorList theList) {
        super();
        floorList = theList;
    }

    public void OnClick() {

        floorList.setIsHidden(false);
        isHidden = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isHidden)
            return;
        Paint p = new Paint();

        p.setColor(Color.BLACK);

        canvas.drawRect(rect, p);
        p.setColor(Color.RED);
        p.setTextSize(28);

        FacilityConf facConf = FacilityContainer.getInstance().getSelected();
        String txt = "" + (facConf.getSelectedFloor() + 1);
        canvas.drawText(txt, x + 10, y + 30, p);

    }

}
