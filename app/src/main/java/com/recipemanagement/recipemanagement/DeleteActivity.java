package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteActivity extends AppCompatActivity {



    private Button deleteEvent;
    private final String baseUrl = "https://recipe-management-service.herokuapp.com/deleteRecipe/";
    String id = "5cfe5b7049bb550004f951b2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        deleteEvent = findViewById(R.id.deleteButton);
        //deleteEvent.setVisibility(View.INVISIBLE);
        deleteEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("ID"+id);
                try{
                    URL url = new URL(baseUrl+id);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                    httpCon.setRequestMethod("DELETE");
                    int responseCode = httpCon.getResponseCode();
                    System.out.println("Response code:" + responseCode);
                    httpCon.connect();
                }catch(Exception e){
                    e.printStackTrace();
                }
                Intent activity = new Intent(DeleteActivity.this, MainActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
            }
        });
    }
}
