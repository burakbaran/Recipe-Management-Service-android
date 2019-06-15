package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ViewRecipe extends AppCompatActivity {


    private EditText recipeName;
    private EditText recipeDetails;
    private Button deleteButton;
    private Button updateButton;
    private ListView tagLists;
    private String id;
    //private Intent intent;
    ArrayAdapter adapter;
    //delete calısıyor
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    int position=0;
    private final String baseUrlForDelete = "https://recipe-management-service.herokuapp.com/deleteRecipe/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        recipeName = (EditText) findViewById(R.id.recipeName);
        recipeDetails = (EditText) findViewById(R.id.recipeDescription);
        disableEditText(recipeDetails);
        disableEditText(recipeName);

        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        tagLists = findViewById(R.id.tagLists);
        Bundle extra = this.getIntent().getExtras();

        int index=0;

        if(extra == null){
            index = 0;
            list = null;
            id = null;
        }else {
            index = (int) extra.getLong("id");
            list = extra.getStringArrayList("list");
            id = list.get(index);
            name = extra.getStringArrayList("name");
            details = extra.getStringArrayList("details"); // bunlar settext olmalı
            tags = extra.getStringArrayList("tags"); //tagler yazılması incele
            position = extra.getInt("position");
        }

        recipeName.setText(name.get(index));
        recipeDetails.setText(details.get(index)); //textler güncellendi.

        adapter = new ArrayAdapter < String >
                (ViewRecipe.this, android.R.layout.simple_list_item_1,tags);
        tagLists.setAdapter(adapter);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //delete recipe
                System.out.println("ID"+id);
                try{
                    URL url = new URL(baseUrlForDelete+id);
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
                Intent activity = new Intent(ViewRecipe.this, MainActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() { //update ise diğer sayfaya gec
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ViewRecipe.this,RecipeActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("list",list);

                startActivity(intent);
            }
        });
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

}
