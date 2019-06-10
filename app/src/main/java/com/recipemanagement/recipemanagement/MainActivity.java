package com.recipemanagement.recipemanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String baseUrl = "https://recipe-management-service.herokuapp.com/getRecipes";
    private ListView listView;


    private ArrayAdapter adapter;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> itemIds=new ArrayList<String>();




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //adding sections in menu bar
        menuInflater.inflate(R.menu.add_recipe,menu);
        menuInflater.inflate(R.menu.search_recipe,menu);
        menuInflater.inflate(R.menu.delete_recipe,menu);
        return super.onCreateOptionsMenu(menu);
    }



    //move on the selected activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.add_recipe){
            Intent intent = new Intent(getApplicationContext(), AddActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.search_recipe){
            Intent intent = new Intent(getApplicationContext(), searchActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.delete_recipe){
            Intent intent = new Intent(getApplicationContext(), deleteActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Yemek Tarifleri");
        setContentView(R.layout.activity_main);
        new JsonTask().execute();

        listView = (ListView)findViewById(R.id.listView);//eklenen recipeler listelenmesi icin
        listView.setOnItemClickListener(this);




    }
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, RecipeActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("list",itemIds);
        // Or / And
        intent.putExtra("id", id);
        startActivity(intent);
    }
    protected class JsonTask extends AsyncTask<Void, Void, ArrayList<JSONObject>>
    {
        @Override
        protected ArrayList<JSONObject> doInBackground(Void... params)
        {

            String str="https://recipe-management-service.herokuapp.com/getRecipes";
            BufferedReader bufferedReader = null;
            try {
                System.out.println("objects fecthing");
                URL getEventsUrl = new URL(str);

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
                return jsons;
            }
            catch(Exception ex)
            {
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
                    (MainActivity.this, android.R.layout.simple_list_item_1,listItems);
            listView.setAdapter(adapter);
            String a ="";
            for(int i = 0; i < response.size(); i++) {
                JSONObject myJson = response.get(i);
                try {
                   listItems.add(myJson.getString("name"));
                   itemIds.add(myJson.getString("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
