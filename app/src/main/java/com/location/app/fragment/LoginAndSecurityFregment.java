package com.location.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.location.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginAndSecurityFregment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginAndSecurityFregment extends Fragment implements View.OnClickListener {
    Context context;
    LinearLayout ll_change_password;
    Fragment fragment = null;
    String tag = "";

    public LoginAndSecurityFregment() {
        // Required empty public constructor
    }

    public static final String ARG_SECTION_NUMBER = "section_number";

    public static LoginAndSecurityFregment newInstance(int number) {
        LoginAndSecurityFregment fragment = new LoginAndSecurityFregment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, number);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            int number = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_and_security_fregment, container, false);
        callView(view);
        return view;

    }

    public void callView(View view) {
        ll_change_password = view.findViewById(R.id.ll_change_password);
        ll_change_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_change_password:
                callChangeFregment();
                break;
        }
    }

    public void callChangeFregment() {
        fragment = new ChangePasswordFregment();
        tag = LoginAndSecurityFregment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.frame, fragment, tag);
        fragmentTransaction.commit();

    }
}