package com.recipemanagement.recipemanagement;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    RelativeLayout relay1;
    EditText username, password, fullname;
    private String baseUrl = "https://recipe-management-service.herokuapp.com/signup";
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        relay1 = findViewById(R.id.relay_layout1);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        fullname = findViewById(R.id.fullname);
        btn = findViewById(R.id.btn);




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
                    eventObject.put("fullname", fullname.getText().toString());

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
                        Intent activity = new Intent(SignUpActivity.this, ActivityLogin.class);
                        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SignUpActivity.this.startActivity(activity);
                    }
                    else{
                        BottomSheetDialog dialog = new BottomSheetDialog(SignUpActivity.this);
                        dialog.setContentView(R.layout.same_user_name);
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
