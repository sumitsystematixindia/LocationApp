package com.mlins.maping;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.mlins.aStar.GisSegment;
import com.mlins.aStar.aStarMath;
import com.mlins.utils.PropertyHolder;
import com.spreo.spreosdk.R;

import java.util.List;

public class LineSprite extends Drawable {
    GisSegment line;
    GisSegment nextline;
    List<PointF> imagePoints;
    boolean isFirst = false;
    private Paint mPaint;
    private Bitmap fillBMP;

    // AddTry
    public LineSprite(GisSegment l, GisSegment nextl, boolean isfirst) {
        line = l;
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        nextline = nextl;
        isFirst = isfirst;
        Context context = PropertyHolder.getInstance().getMlinsContext();
        fillBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.patharrow);
        scaleBitmap(0.5);
        fillBMP = RotateBitmap(fillBMP, aStarMath.getSegmentAngle(l));


        imagePoints = aStarMath.divideLine(line, 12);


    }


    public static Bitmap RotateBitmap(Bitmap source, double angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    @Override
    public void draw(Canvas canvas) {

        float x1 = (float) line.getLine().getPoint1().getX();
        float y1 = (float) line.getLine().getPoint1().getY();
        float x2 = (float) line.getLine().getPoint2().getX();
        float y2 = (float) line.getLine().getPoint2().getY();
        mPaint.setColor(Color.parseColor(PropertyHolder.getInstance().getNavRouteColor()));
        mPaint.setStrokeWidth(14);
        canvas.drawLine(x1, y1, x2, y2, mPaint);

        if (isFirst) {
            canvas.drawCircle(x1, y1, 7, mPaint);
        }

        canvas.drawCircle(x2, y2, 7, mPaint);


        if (imagePoints.size() < 3) {
            if (line.getWeight() > 20) {
                imagePoints.clear();
                float centerx = (x1 + x2) / 2;
                float centery = (y1 + y2) / 2;
                PointF centerp = new PointF(centerx, centery);
                imagePoints.add(centerp);
            }
        }


        for (PointF o : imagePoints) {
            if (imagePoints.indexOf(o) != imagePoints.size() - 1 || imagePoints.size() == 1) {
                canvas.drawBitmap(fillBMP, o.x - fillBMP.getWidth() / 2, o.y
                        - fillBMP.getHeight() / 2, mPaint);
            }
        }


    }

    public void scaleBitmap(double scaleFactor) {

        if (fillBMP == null)
            return;

        Bitmap outBitmap;

        int width = (int) (fillBMP.getWidth() * scaleFactor * 0.75);
        int height = (int) (fillBMP.getHeight() * scaleFactor * 0.75);
        outBitmap = (Bitmap.createScaledBitmap(fillBMP, width, height, true));
        fillBMP.recycle();
        fillBMP = outBitmap;
    }


    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub

    }
}
