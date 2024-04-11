package com.wasapii.adisoftin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wasapii.adisoftin.util.AppSharedPreferences;
import com.wasapii.adisoftin.util.CommonUtilities;

public class SignUpPasswordActivity extends AppCompatActivity {


    private Button btnNext;
    private Context ctx;
    private String password;
    private EditText et_password;
    private TextView tv_redirectToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up_password);

        et_password = (EditText) findViewById(R.id.et_password);
        tv_redirectToLogin = (TextView) findViewById(R.id.tv_redirectToLogin);
        btnNext = (Button) findViewById(R.id.btn_next);

        ctx = SignUpPasswordActivity.this;

        if(!AppSharedPreferences.getPassword(ctx).equalsIgnoreCase("")){

            Intent intent = new Intent(ctx,SignUpMobileActivity.class);
            startActivity(intent);
            finish();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String password = et_password.getText().toString().trim();

                if(password.equalsIgnoreCase("")){
                    Toast.makeText(ctx,"Please enter password",Toast.LENGTH_SHORT).show();
                }else if(!CommonUtilities.checkPasswordLength(password)){
                    Toast.makeText(ctx,"Password length must be at least 6 characters long.",Toast.LENGTH_SHORT).show();
                }else {
                    AppSharedPreferences.putPassword(ctx,password);
                    Intent intent = new Intent(ctx, SignUpMobileActivity.class);
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
}
