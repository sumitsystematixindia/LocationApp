package com.mlins.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spreo.spreosdk.R;

public class ViewCache {

    private View baseView;
    private TextView textView;
    private ImageView imageView;
    private ImageView infoView;

    public ViewCache(View baseView) {
        this.baseView = baseView;
    }

    public TextView getTextView() {
        if (textView == null) {
            textView = (TextView) baseView.findViewById(R.id.text);
        }
        return textView;
    }

    public ImageView getImageView() {
        if (imageView == null) {
            imageView = (ImageView) baseView.findViewById(R.id.image);
        }
        return imageView;
    }

    public ImageView getInfoView() {
        if (infoView == null) {
            infoView = (ImageView) baseView.findViewById(R.id.info_lists_image);
        }
        return infoView;
    }
}
