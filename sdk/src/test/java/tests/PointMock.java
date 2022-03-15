package tests;

import android.graphics.PointF;

public class PointMock extends PointF {

    public PointMock(float x, float y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }

    public PointMock(double x, double y) {
        super((float) x, (float) y);
        this.x = (float) x;
        this.y = (float) y;
    }
}
