package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ActivityLogin extends AppCompatActivity {

    RelativeLayout relay1;
    EditText username, password;
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
                if(username.getText().toString().equals("burak")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });



    }
}
