package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoDelete extends AppCompatActivity {
    private EditText ndays;
    private Button submit;
    int value =0;
    String str= "";

    private final String baseUrl = "https://recipe-management-service.herokuapp.com/updateUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_delete);

        submit = findViewById(R.id.submit);
        System.out.println("Keyword" + value + "bitti");
        Intent intent = getIntent();
        str = SaveSharedPreference.getToken(AutoDelete.this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ndays = (EditText)findViewById(R.id.nDays);
                System.out.println("Obje çıktı"+ndays);
                String s = (String) ndays.getText().toString();
                System.out.println("Deneme" + s);
                value = Integer.parseInt(s);
                try {
                    URL url = new URL(baseUrl);
                    System.out.println(url.toString());
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                    httpCon.setRequestMethod("PUT");
                    httpCon.setRequestProperty("Content-Type","application/json");
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("nDays",value);
                    httpCon.setRequestProperty("Authorization", str);
                    String json = eventObject.toString();
                    byte[] outputInBytes = json.getBytes("UTF-8");
                    OutputStream os = httpCon.getOutputStream();
                    os.write(outputInBytes);
                    os.close();
                    int responseCode = httpCon.getResponseCode();
                    System.out.println("response code: " + responseCode);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setClass(AutoDelete.this,MainActivity.class);
                intent.putExtra("token",str);
                startActivity(intent);
            }
        });
    }




}
