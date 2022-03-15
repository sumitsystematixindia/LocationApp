package com.mlins.dualmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;


class IconWithLabelGenerator {

    private static final int MAX_LINES = 3;

    public synchronized static Bitmap generate(Bitmap icon, String text, int widthPX) {
        TextPaint textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(5);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(widthPX*0.15f);

        widthPX = getWidth(textPaint, text, widthPX);

        StaticLayout textLayout =
                new StaticLayout(
                        text,
                        textPaint,
                        widthPX,
                        Layout.Alignment.ALIGN_CENTER,
                        1.0f,
                        0.0f,
                        false);

        if(textLayout.getLineCount() > MAX_LINES) {
            textLayout =
                    new StaticLayout(
                            text,
                            0,
                            textLayout.getLineEnd(MAX_LINES-1),
                            textPaint,
                            widthPX,
                            Layout.Alignment.ALIGN_CENTER,
                            1.0f,
                            0,
                            false,
                            TextUtils.TruncateAt.END,
                            (int) (textLayout.getLineWidth(MAX_LINES-1) * 0.9f)
                    );
        }

        Bitmap result = Bitmap.createBitmap(widthPX, icon.getHeight() + textLayout.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(icon, (widthPX - icon.getWidth())/2,0, null);

        canvas.translate(0, icon.getHeight());

        textLayout.draw(canvas);

        textLayout.getPaint().setColor(Color.BLACK);
        textLayout.getPaint().setStyle(Paint.Style.FILL);
        textLayout.draw(canvas);

        return result;
    }

    private static int getWidth(TextPaint textPaint, String text, int initialwidth) {
        int result = initialwidth;
        try {
            if (textPaint != null && text != null) {
                String[] words = text.split(" ");
                if (words != null) {
                    double maxw = Double.MIN_VALUE;
                    for (String o : words) {
                        float w = textPaint.measureText(o);
                        if (w > maxw) {
                            maxw = w;
                        }
                    }

                    if (maxw > initialwidth) {
                        result = (int)(maxw + 0.5);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

}
