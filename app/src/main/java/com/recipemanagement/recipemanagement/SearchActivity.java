package com.recipemanagement.recipemanagement;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

public class SearchActivity extends AppCompatActivity {



    //search items
    private Button searchButton;
    private ListView searchList;
    private EditText searchKey;
    private Context context;
    //private EditText details;


    public static ArrayList<JSONObject> json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        context = this;
        searchKey = findViewById(R.id.searchKey);
        searchButton = findViewById(R.id.search_event_button);

        searchList = findViewById(R.id.search_view);
        //  details = findViewById(R.id.details);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AuxClass().execute();
            }
        });
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
            String a ="";
            for(int i = 0; i < response.size(); i++) {
                JSONObject myJson = response.get(i);
                // use myJson as needed, for example
                try {
                    a += " " + myJson.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            System.out.println(a);

        }
    }
}




