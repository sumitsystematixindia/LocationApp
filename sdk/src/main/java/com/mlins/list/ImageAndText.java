package com.mlins.list;

import android.graphics.drawable.Drawable;


public class ImageAndText {
    protected String imageUrl;
    protected String text;
    protected boolean isselected = false;
    protected Drawable drawableMediaIcon = null;
    public ImageAndText(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    public ImageAndText(Drawable mediaicon, String text) {
        this.drawableMediaIcon = mediaicon;
        this.text = text;
    }

    public boolean isselected() {
        return isselected;
    }

    public void setselected(boolean isselected) {
        this.isselected = isselected;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object other) {
        ImageAndText imgtxt = (ImageAndText) other;
        if (imgtxt == null) {
            return false;
        }
        return (imgtxt.imageUrl.equals(imageUrl) && imgtxt.text.equals(text));


    }

    public Drawable getDrawableMediaIcon() {
        return drawableMediaIcon;
    }


}

