package com.mlins.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class MlinsDialog extends Dialog {
    TextView tv = null;

    public MlinsDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setTextView(TextView tv) {
        this.tv = tv;
    }

    public String getText() {
        String result = "";
        if (tv != null)
            result = (String) tv.getText().toString();


        return result;

    }

}
