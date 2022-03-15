package com.mlins.maping;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.mlins.aStar.GisSegment;
import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.utils.PropertyHolder;

public class InstructionSprite extends Drawable {

    GisSegment line;
    GisSegment nextline;
    private Paint mPaint;
    private Bitmap instructionBmp;
    private Instruction instruction;

    public InstructionSprite(GisSegment l, GisSegment nextl) {
        line = l;
        nextline = nextl;
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        Context context = PropertyHolder.getInstance().getMlinsContext();

        instruction = findTheInstruction(l);
        if (instruction != null) {
            instructionBmp = BitmapFactory.decodeResource(context.getResources(), instruction.getImage().get(0));
//			instructionBmp = RotateBitmap(instructionBmp, aStarMath.getSegmentAngle(l));

//			if (nextline != null && instruction.getImage().get(0) == (R.drawable.arrow)) {
//				instructionBmp = RotateBitmap(instructionBmp,
//						aStarMath.getSegmentAngle(nextline));
//			} else {
//				instructionBmp = RotateBitmap(instructionBmp,
//						aStarMath.getSegmentAngle(line));
//			}
        }

    }

    public static Bitmap RotateBitmap(Bitmap source, double angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public void scaleBitmap(double scaleFactor) {

        if (instructionBmp == null)
            return;

        Bitmap outBitmap;

        int width = (int) (instructionBmp.getWidth() * scaleFactor);
        int height = (int) (instructionBmp.getHeight() * scaleFactor);
        outBitmap = (Bitmap.createScaledBitmap(instructionBmp, width, height, true));
        instructionBmp = outBitmap;

    }

    public IconSprite convertToIconSprite() {
        return new IconSprite(instructionBmp);
    }

    public Instruction findTheInstruction(GisSegment s) {
        Instruction result = null;
        GisSegment segment = s;
        if (segment != null) {
            for (Instruction i : InstructionBuilder.getInstance()
                    .getCurrentInstructions()) {
                int id1 = segment.getId();
                int id2 = i.getSegment().getId();
                if (id1 == id2) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void draw(Canvas canvas) {
        if (instruction != null && instructionBmp != null) {

            float left = (float) (instruction.getLocation().getX() - instructionBmp.getWidth() / 2.0f);
            float top = (float) (instruction.getLocation().getY() - instructionBmp.getHeight() / 2.0f);
//			double d = 5.0;
//			if (nextline != null) {
//				double iangle = Math.toRadians(360.0f-aStarMath
//						.getSegmentAngle(nextline));
//				left = (float) (left + d * Math.cos(iangle));
//				top = (float) (top + d * Math.sin(iangle));
//			}
            canvas.drawBitmap(instructionBmp, left, top, mPaint);
        }

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
