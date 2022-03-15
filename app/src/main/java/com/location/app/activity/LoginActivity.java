package com.location.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.location.app.R;

public class LoginActivity extends AppCompatActivity {
EditText et_username,et_password;
ImageView img_show_password;
boolean show_password=false;
Button btn_next;
TextView tv_forgot;
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=LoginActivity.this;
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        img_show_password=findViewById(R.id.img_show_password);
        tv_forgot=findViewById(R.id.tv_forgot);
        btn_next=findViewById(R.id.btn_next);
        img_show_password.setImageDrawable(getResources().getDrawable(R.drawable.icon_visibility));
        img_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_password.getText()!=null)
                {
                    if(show_password==false)
                    {

                        img_show_password.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
                        et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        show_password=true;
                    }else
                    {
                        img_show_password.setImageDrawable(getResources().getDrawable(R.drawable.icon_visibility));
                        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        show_password=false;
                    }
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context,MainActivity.class));
            }
        });
        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context,ForgotActivity.class));
            }
        });
    }
}