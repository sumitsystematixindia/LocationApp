package com.mlins.view.ctrls;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.mlins.views.IFloorSetter;

public class FloorButton extends ButtonObject {

    private FloorList owner;
    private int floorNumber;
    private IFloorSetter pView;

    public FloorButton(int j) {
        floorNumber = j;
    }

    public void setOwner(FloorList owner) {
        this.owner = owner;
    }

    public void OnClick() {


        pView.setFloorNumber(floorNumber);
        pView.setNavigationPath();

//		PoiDataHelper.getInstance().loadPois();


    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x + owner.x, y + owner.y, drawlble.getPaint());
    }


    @Override
    public void setX(int x) {
        // TODO Auto-generated method stub
        super.setX(x);

    }

    @Override
    public void setY(int y) {

        super.setY(y);
    }

    @Override
    public void setParent(View parent) {
        if (parent instanceof IFloorSetter) {
            this.pView = (IFloorSetter) parent;
        }
        context = parent.getContext();
        this.parent = parent;
        drawlble = new BitmapDrawable(context.getResources());

    }

}
