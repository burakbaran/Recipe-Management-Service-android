package com.recipemanagement.recipemanagement;

import com.recipemanagement.recipemanagement.R.*;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityLogin extends AppCompatActivity {

    RelativeLayout relay1;
    EditText username, password;
    private String baseUrl = "https://recipe-management-service.herokuapp.com/login";
    Button btn,btn_signUp;
    private String baseUrlDeviceId = "https://recipe-management-service.herokuapp.com/setToken/";
    static  String deviceId;
    String s;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relay1.setVisibility(View.VISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        relay1 = findViewById(R.id.relay_layout1);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn = findViewById(R.id.btn);
        btn_signUp = findViewById(R.id.btn_signup);



        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent activity = new Intent(ActivityLogin.this, MainActivity.class);
            Bundle options = ActivityOptions.makeCustomAnimation(ActivityLogin.this,android.R.anim.fade_in,android.R.anim.fade_out).toBundle();
            ActivityLogin.this.startActivity(activity,options);
        }else{
            handler.postDelayed(runnable,2000);
        }

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(ActivityLogin.this, SignUpActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle options = ActivityOptions.makeCustomAnimation(ActivityLogin.this,android.R.anim.fade_in,android.R.anim.fade_out).toBundle();
                ActivityLogin.this.startActivity(activity,options);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL url = new URL(baseUrl);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                    httpCon.setRequestMethod("POST");
                    httpCon.setRequestProperty("Content-Type","application/json");
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("username",username.getText().toString());
                    eventObject.put("password", password.getText().toString());

                    String json = eventObject.toString();

                    byte[] outputInBytes = json.getBytes("UTF-8");
                    OutputStream os = httpCon.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    System.out.println(os.toString());

                    String s = httpCon.getHeaderField("Authorization");
                    int responseCode = httpCon.getResponseCode();
                    System.out.println("response code: " + responseCode);
                    System.out.println("aut code: " + s);

                    if(responseCode == 200){
                        SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
                        SaveSharedPreference.setToken(getApplicationContext(), s);
                        Intent activity = new Intent(ActivityLogin.this, MainActivity.class);
                        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ActivityLogin.this.startActivity(activity);
                        //activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle options = ActivityOptions.makeCustomAnimation(ActivityLogin.this,android.R.anim.fade_in,android.R.anim.fade_out).toBundle();
                        ActivityLogin.this.startActivity(activity,options);
                    }
                    else{
                        BottomSheetDialog dialog = new BottomSheetDialog(ActivityLogin.this);
                        dialog.setContentView(R.layout.wrong_username_password);
                        dialog.show();


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                FcmTokenRegistrationService fcmTokenRegistrationService = new FcmTokenRegistrationService();
                fcmTokenRegistrationService.onHandleIntent(intent);
                deviceId = fcmTokenRegistrationService.token;
                System.out.println("Bu classda bu değer var: " + deviceId);
                System.out.println("Token header icin: " + s);

                try{
                    URL url = new URL(baseUrlDeviceId+deviceId);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                    httpCon.setRequestMethod("PUT");
                    httpCon.setRequestProperty("Authorization", s);
                    int responseCode = httpCon.getResponseCode();
                    System.out.println("Response code:" + responseCode);
                    httpCon.connect();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });


        sendFcmRegistrationToken();
    }

    private void sendFcmRegistrationToken() {
        Intent intent = new Intent(this, FcmTokenRegistrationService.class);
        startService(intent);
    }
}
