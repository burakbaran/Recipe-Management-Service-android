package com.recipemanagement.recipemanagement;

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
    Button btn;
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

        handler.postDelayed(runnable,2000);
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
                        Intent activity = new Intent(ActivityLogin.this, MainActivity.class);
                        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ActivityLogin.this.startActivity(activity);
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
            }
        });
    }
}