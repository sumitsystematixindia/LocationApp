package com.mlins.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mlins.utils.logging.Log;
import com.spreo.spreosdk.R;

public class UiUtils {

    public static int OK_CANCEL_FLAG = 1;
    public static int YES_NO_FLAG = 2;
    public static int RESULT_OK = 0;
    public static int RESULT_CANCEL = -1;
    public static int result = 1;
    public static String usertextinput = "";
    public static Typeface tf;

    public static Dialog doMessageBox(String title, String messge, int flag,
                                      Context ctx, OnClickListener posListener, OnClickListener negListener) {

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_uiutils);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tf = PropertyHolder.getInstance().getClalitFont();
        TextView headertext = (TextView) dialog.findViewById(R.id.headertext);
        headertext.setTypeface(tf);
        headertext.setTextSize(24);
        headertext.setGravity(Gravity.CENTER_HORIZONTAL);
        headertext.setText(title);
        TextView informationtext = (TextView) dialog.findViewById(R.id.informationtext);
        informationtext.setTypeface(tf);
        informationtext.setTextSize(20);
        informationtext.setGravity(Gravity.CENTER_HORIZONTAL);
        informationtext.setText(messge);
        Button posetivebutton = (Button) dialog.findViewById(R.id.PosetiveButton);
        Button negativebutton = (Button) dialog.findViewById(R.id.NegativeButton);
        posetivebutton.setOnClickListener((OnClickListener) posListener);
        negativebutton.setOnClickListener((OnClickListener) negListener);
        Resources res = ctx.getResources();
        String posetive = res.getString(R.string.ok);
        String negative = res.getString(R.string.cancel);
        if (flag == YES_NO_FLAG) {
            posetive = res.getString(R.string.yes);
            negative = res.getString(R.string.no);
        } else if (flag == OK_CANCEL_FLAG) {
            posetive = res.getString(R.string.ok);
            negative = res.getString(R.string.cancel);
        }
        posetivebutton.setTypeface(tf);
        posetivebutton.setText(posetive);
        negativebutton.setTypeface(tf);
        negativebutton.setText(negative);
        dialog.show();
        return dialog;
    }

    public static MlinsDialog doMessageBoxWithInput(String title, String messge,
                                                    String txtinput, int flag, Context ctx, OnClickListener posListener, OnClickListener negListener) {

        final MlinsDialog dialog = new MlinsDialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_uiutils_textinput);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView headertext = (TextView) dialog.findViewById(R.id.headertext);
        headertext.setTypeface(tf);
        headertext.setTextSize(24);
        headertext.setGravity(Gravity.CENTER_HORIZONTAL);
        headertext.setText(title);
        TextView informationtext = (TextView) dialog.findViewById(R.id.informationtext);
        informationtext.setTypeface(tf);
        informationtext.setTextSize(20);
        informationtext.setGravity(Gravity.CENTER_HORIZONTAL);
        informationtext.setText(messge);
        Button posetivebutton = (Button) dialog.findViewById(R.id.PosetiveButton);
        Button negativebutton = (Button) dialog.findViewById(R.id.NegativeButton);
        posetivebutton.setOnClickListener((OnClickListener) posListener);
        negativebutton.setOnClickListener((OnClickListener) negListener);
        Resources res = ctx.getResources();
        String posetive = res.getString(R.string.ok);
        String negative = res.getString(R.string.cancel);
        TextView userinput = (TextView) dialog.findViewById(R.id.userinput);
        userinput.setText(txtinput);
        dialog.setTextView(userinput);
        usertextinput = userinput.getText().toString();

        if (flag == YES_NO_FLAG) {
            posetive = res.getString(R.string.yes);
            negative = res.getString(R.string.no);
        } else if (flag == OK_CANCEL_FLAG) {
            posetive = res.getString(R.string.ok);
            negative = res.getString(R.string.cancel);
        }
        posetivebutton.setText(posetive);
        posetivebutton.setTypeface(tf);
        negativebutton.setText(negative);
        negativebutton.setTypeface(tf);
        dialog.show();

        return dialog;
    }

    public static String getUserinput() {
        return usertextinput;
    }

//	public static Dialog doMessageBox(String title, String messge, int flag,
//			Context ctx,DialogInterface.OnClickListener posListener,DialogInterface.OnClickListener negListener ) {
//
//		final Dialog dialog = new Dialog(ctx);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(R.layout.popup_uiutils);
//		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//		tf = PropertyHolder.getInstance().getClalitFont();
//		TextView headertext = (TextView) dialog.findViewById(R.id.headertext);
//		headertext.setTypeface(tf);
//		headertext.setTextSize(24);
//		headertext.setGravity(Gravity.CENTER_HORIZONTAL);
//		headertext.setText(title);
//		TextView informationtext = (TextView) dialog.findViewById(R.id.informationtext);
//		informationtext.setTypeface(tf);
//		informationtext.setTextSize(20);
//		informationtext.setGravity(Gravity.CENTER_HORIZONTAL);
//		informationtext.setText(messge);
//		Button posetivebutton = (Button) dialog.findViewById(R.id.PosetiveButton);
//		Button negativebutton = (Button) dialog.findViewById(R.id.NegativeButton);
//		posetivebutton.setOnClickListener((OnClickListener) posListener);
//		negativebutton.setOnClickListener((OnClickListener) negListener);
//		Resources res = ctx.getResources();
//		String posetive = res.getString(R.string.ok);
//		String negative = res.getString(R.string.cancel);
//		if (flag == YES_NO_FLAG) {
//			posetive = res.getString(R.string.yes);
//			negative = res.getString(R.string.no);
//		} else if (flag == OK_CANCEL_FLAG) {
//			posetive = res.getString(R.string.ok);
//			negative = res.getString(R.string.cancel);
//		}
//		posetivebutton.setTypeface(tf);
//		posetivebutton.setText(posetive);
//		negativebutton.setTypeface(tf);
//		negativebutton.setText(negative);
//		dialog.show();
//		return dialog;
//	}


    public static Dialog installQRapp(Context context, String title, String messge, int flag) {


        final Context ctx = context;


        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_uiutils);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tf = PropertyHolder.getInstance().getClalitFont();
        TextView headertext = (TextView) dialog.findViewById(R.id.headertext);
        headertext.setTypeface(tf);
        headertext.setTextSize(24);
        headertext.setGravity(Gravity.CENTER_HORIZONTAL);
        headertext.setText(title);
        TextView informationtext = (TextView) dialog.findViewById(R.id.informationtext);
        informationtext.setTypeface(tf);
        informationtext.setTextSize(20);
        informationtext.setGravity(Gravity.CENTER_HORIZONTAL);
        informationtext.setText(messge);
        Button posetivebutton = (Button) dialog.findViewById(R.id.PosetiveButton);
        Button negativebutton = (Button) dialog.findViewById(R.id.NegativeButton);

        OnClickListener okListener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                String PACKAGE = "com.google.zxing.client.android";
                Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    ctx.startActivity(intent);
                } catch (Throwable t) {
                    // Hmm, market is not installed
                    Log.getInstance()
                            .error("UiUtils",
                                    "Android Market is not installed; cannot install Barcode Scanner",
                                    t);
                }
                dialog.dismiss();

            }
        };

        OnClickListener cancelListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        };

        posetivebutton.setOnClickListener(okListener);
        negativebutton.setOnClickListener(cancelListener);
        Resources res = ctx.getResources();
        String posetive = res.getString(R.string.ok);
        String negative = res.getString(R.string.cancel);
        if (flag == YES_NO_FLAG) {
            posetive = res.getString(R.string.yes);
            negative = res.getString(R.string.no);
        } else if (flag == OK_CANCEL_FLAG) {
            posetive = res.getString(R.string.ok);
            negative = res.getString(R.string.cancel);
        }
        posetivebutton.setTypeface(tf);
        posetivebutton.setText(posetive);
        negativebutton.setTypeface(tf);
        negativebutton.setText(negative);
        dialog.show();
        return dialog;
    }

}
