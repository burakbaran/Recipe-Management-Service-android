package com.recipemanagement.recipemanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{



    //search items
    private Button searchButton,details;
    private ListView searchList; //add the items on the list
    private EditText searchKey;
    private Toolbar toolbar;
    private ArrayAdapter adapter;
    private ArrayAdapter adapter2;
    private ArrayAdapter adapter3;
    private ArrayAdapter adapter4;
    ArrayList<String> listForDetails=new ArrayList<String>();
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> itemIds=new ArrayList<String>();
    ArrayList<String> listForTag=new ArrayList<String>();
    JSONObject myJson;



    public static ArrayList<JSONObject> json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        //CustomBar
        toolbar = findViewById(R.id.CustomBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("    Yemek Tarifleri");
        getSupportActionBar().setIcon(R.drawable.asdf);


        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        searchKey = findViewById(R.id.searchKey);
        searchButton = findViewById(R.id.search_event_button);
        details = (Button) findViewById(R.id.details);
        searchList = findViewById(R.id.search_view);
        searchList.setOnItemClickListener(this);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AuxClass().execute();

            }
        });


    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Details");

        // Setting Dialog Message
        try {
            alertDialog.setMessage(myJson.getString("details"));
        }catch(Exception e){
            e.printStackTrace();
        }

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //nothing
        Intent intent = new Intent();
        intent.setClass(this, ViewRecipe.class);
        String idofItem = itemIds.get(position);
        intent.putExtra("idofItem",idofItem);
        // Or / And
        intent.putExtra("id", id);
        startActivity(intent);

    }

    protected class AuxClass extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
        private final String baseUrl = "https://recipe-management-service.herokuapp.com/searchRecipes/";
        protected ArrayList<JSONObject> doInBackground(Void... voids) {
            BufferedReader bufferedReader = null;
            String search = searchKey.getText().toString();
            try {
                System.out.println("objects fecthing");
                System.out.println(searchKey);
                URL getEventsUrl = new URL(baseUrl +search);

                HttpURLConnection httpURLConnection = (HttpURLConnection) getEventsUrl.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    builder.append(line);
                }

                ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();

                JSONArray result = new JSONArray(builder.toString());
                for(int i = 0; i < result.length(); i++) {
                    jsons.add(result.getJSONObject(i));
                }
                System.out.println(jsons.size());
                return jsons;
            }catch(Exception ex){
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(ArrayList<JSONObject> response)
        {
            if(response != null)
            {
                try {
                    Log.e("App", "Success: "  );
                } catch (Exception ex) {
                    Log.e("App", "Failure", ex);
                }
            }
            adapter = new ArrayAdapter < String >
                    (SearchActivity.this, android.R.layout.simple_list_item_1,listItems);
            searchList.setAdapter(adapter);

            String a ="";
            for(int i = 0; i < response.size(); i++) {
                myJson = response.get(i);
                try {
                    listItems.add(myJson.getString("name"));
                    itemIds.add(myJson.getString("id"));
                    listForDetails.add(myJson.getString("details"));
                    listForTag.add(myJson.getString("tags"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




