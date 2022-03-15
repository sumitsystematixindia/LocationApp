package com.mlins.view.ctrls;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.mlins.views.d2dTouchImageView;

import java.util.ArrayList;
import java.util.List;

public class FloorList extends BaseCtrl {
    int x = 0;
    int y = 0;
    List<FloorButton> floors = new ArrayList<FloorButton>();


//	public FloorList(int floornum, int width, int height, List<BaseCtrl> ctrls, d2dTouchImageView d2dTouchImageView) {
//		for (int i = 0; i < floornum; i++) {
//			createfloors(i, width, height, ctrls);
//		}
//	}


    public FloorList(int i, d2dTouchImageView d2dTouchImageView) {
        for (int j = 0; j < i; j++) {
            createfloor(j, d2dTouchImageView);
        }
    }

    private void createfloor(int j, d2dTouchImageView d2dTouchImageView) {
        FloorButton floor = new FloorButton(j);
        String imagename = "f" + String.valueOf(j + 1);
        Resources res = d2dTouchImageView.getContext().getResources();
        int resID = res.getIdentifier(imagename, "drawable", d2dTouchImageView.getContext().getPackageName());
        Bitmap bitmap1 = BitmapFactory.decodeResource(d2dTouchImageView.getContext().getResources(), resID);
        floor.setBitmap(bitmap1);
        floor.setX(0);
        floor.setY(j * (bitmap1.getHeight() + 10));
        floor.setParent(d2dTouchImageView);
        floor.setOwner(this);
        floors.add(floor);

    }


    @Override
    public void setScreenSize(int x, int y) {
        // TODO Auto-generated method stub
        super.setScreenSize(x, y);
        for (FloorButton element : floors) {
            element.setX(x - 100);
            element.setY(y - 200);
            element.setScreenSize(x, y);
        }

    }


    @Override
    public void draw(Canvas canvas) {
        if (!isHidden) {
            for (BaseCtrl element : floors) {
                element.draw(canvas);
            }
        }


    }

    @Override
    public void setIsHidden(boolean v) {
        // TODO Auto-generated method stub
        super.setIsHidden(v);
        for (FloorButton element : floors) {
            element.setIsHidden(v);
        }
    }

    @Override
    public boolean clickTest(float f, float g) {
        if (isHidden)
            return false;
        float relx = f - x;
        float rely = g - y;
        for (BaseCtrl element : floors) {
            if (element.clickTest(relx, rely)) {
                return true;
            }
        }
        return false;


    }

    @Override
    public void setParent(View parent) {

        for (FloorButton element : floors) {
            element.setParent(parent);
        }

    }

    public void setX(int x) {
        this.x = x;

    }

    public void setY(int y) {
        this.y = y;

    }


}
