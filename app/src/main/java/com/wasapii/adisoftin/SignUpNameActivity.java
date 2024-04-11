package com.wasapii.adisoftin;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wasapii.adisoftin.Constants.Constants;
import com.wasapii.adisoftin.util.AppSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpNameActivity extends AppCompatActivity {


    private Button btnNext;
    private Context ctx;
    private EditText et_dob,et_fullname;

    private RadioGroup gender_rg;
    Calendar myCalendar = Calendar.getInstance();
    String fullname="",gender="",dob="";
    private TextInputLayout input_layout_fullname,input_layout_dob;
    private TextView tv_redirectToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up_name);

        tv_redirectToLogin = (TextView) findViewById(R.id.tv_redirectToLogin);
        btnNext = (Button) findViewById(R.id.btn_next);
        et_dob = (EditText) findViewById(R.id.et_dob);
        gender_rg = (RadioGroup) findViewById(R.id.gender_rg);
        et_fullname = (EditText) findViewById(R.id.et_fullname);
        input_layout_dob = (TextInputLayout) findViewById(R.id.input_layout_dob);
        input_layout_fullname = (TextInputLayout) findViewById(R.id.input_layout_fullname);

        ctx = SignUpNameActivity.this;

        if(!AppSharedPreferences.getFullName(ctx).equalsIgnoreCase("") && !AppSharedPreferences.getDOB(ctx).equalsIgnoreCase("") && !AppSharedPreferences.getGender(ctx).equalsIgnoreCase("")){

            Intent intent = new Intent(ctx,SignUpPasswordActivity.class);
            startActivity(intent);
            finish();

        }


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                fullname = et_fullname.getText().toString().trim();
                dob = et_dob.getText().toString().trim();
                int selectedId = gender_rg.getCheckedRadioButtonId();

                // find the radiobutton by returned id
               RadioButton  radioButton = (RadioButton) findViewById(selectedId);

                gender = radioButton!= null ? radioButton.getText().toString() : "";

                if(fullname.equalsIgnoreCase("")){
                    Toast.makeText(ctx,"Please enter full name ",Toast.LENGTH_SHORT).show();
                }else if(gender.equalsIgnoreCase("")){
                    Toast.makeText(ctx,"Please select gender",Toast.LENGTH_SHORT).show();
                }else if(dob.equalsIgnoreCase("")){
                    Toast.makeText(ctx,"Please set date of birth ",Toast.LENGTH_SHORT).show();
                }else{

                    AppSharedPreferences.putFullName(ctx,fullname);
                    AppSharedPreferences.putDOB(ctx,dob);
                    AppSharedPreferences.putGender(ctx,gender);

                    Intent intent = new Intent(ctx,SignUpPasswordActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });


        showDatePicker();

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

    private void showDatePicker(){


        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat(Constants.MDY);

                et_dob.setText(sdf.format(myCalendar.getTime()));

//                if(myCalendar.getTime().before(new Date())){
//                    et_dob.setText(sdf.format(new Date()));
//                }else{
//                    et_dob.setText(sdf.format(myCalendar.getTime()));
//                }

            }

        };

//        et_dob.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                DatePickerDialog datePickerDialog =  new DatePickerDialog(ctx, startDate, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH));//.show();
//
//
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//
//                datePickerDialog.show();
//            }
//        });

        et_dob.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_UP){

                    DatePickerDialog datePickerDialog =  new DatePickerDialog(ctx, startDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));//.show();


                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

                    datePickerDialog.show();
                }

                return false;
            }
        });

    }


}
