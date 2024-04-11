package com.wasapii.adisoftin;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wasapii.adisoftin.model.Test;
import com.wasapii.adisoftin.util.AppSharedPreferences;

public class SignUpMobileActivity extends AppCompatActivity {

    private Button btnNext;
    private Context ctx;
    private EditText et_phone;
    private TextInputLayout input_layout_phone;
    private TextView btn_not_now;
    private TextView tv_redirectToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up_mobile);
        tv_redirectToLogin = (TextView) findViewById(R.id.tv_redirectToLogin);
        btnNext = (Button) findViewById(R.id.btn_next);
        et_phone = (EditText) findViewById(R.id.et_phone);
        input_layout_phone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        btn_not_now = (TextView) findViewById(R.id.btn_not_now);

        ctx = SignUpMobileActivity.this;

        if(!AppSharedPreferences.getMobile(ctx).equalsIgnoreCase("")){

            Intent intent = new Intent(ctx,SignUpPhotoActivity.class);
            startActivity(intent);
            finish();

        }

        btn_not_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,SignUpPhotoActivity.class);
                startActivity(intent);
                finish();
            }
        });


        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePhone();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String mobile = et_phone.getText().toString();

                if(validatePhone()) {
                    AppSharedPreferences.putMobile(ctx,mobile);
                    Intent intent = new Intent(ctx, SignUpPhotoActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tv_redirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAlreadyAccount();
            }
        });

    }


    private void actionAlreadyAccount(){

        Intent i = new Intent(ctx, LoginActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    private boolean validatePhone() {

        String email = et_phone.getText().toString().trim();

        if (email.isEmpty() || !AppSharedPreferences.isValidPhone(email)) {
            input_layout_phone.setError("Invalid phone number");
            requestFocus(et_phone);
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
