package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    private EditText name,details,tags;
    private ListView tagLists;
    private ArrayList<String> listItems=new ArrayList<String>(); // getten gelen tagler string
    private Button updateOrCreateEvent, addTagToRecipe;
    private JSONArray jsonTags;
    private ArrayAdapter adapter;
    private final String baseUrl = "https://recipe-management-service.herokuapp.com/updateRecipe/";

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_recipe);

        name = findViewById(R.id.recipeName);
        details = findViewById(R.id.recipeDescription);
        tags = findViewById(R.id.recipeTags);
        tagLists = findViewById(R.id.tagLists);
        addTagToRecipe = findViewById(R.id.addTag);
        updateOrCreateEvent = findViewById(R.id.addRecipe);

        intent = getIntent();
        new JsonTask().execute();

        addTagToRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonTags.put(tags.getText().toString());
                listItems.add(tags.getText().toString());
                adapter.notifyDataSetChanged();
                tags.getText().clear();
            }
        });

        updateOrCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> value = (ArrayList<String>)intent.getExtras().get("list");
                int pos = intent.getExtras().getInt("position");

                String id = value.get(pos);
                try {
                    URL url = new URL(baseUrl + id);
                    System.out.println(url.toString());
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                    httpCon.setRequestMethod("PUT");
                    httpCon.setRequestProperty("Content-Type","application/json");
                    JSONObject eventObject = new JSONObject();

                    eventObject.put("name",name.getText().toString());
                    eventObject.put("details", details.getText().toString());


                    eventObject.put("tags", jsonTags);
                    eventObject.put("photos",new JSONArray());
                    String json = eventObject.toString();

                    byte[] outputInBytes = json.getBytes("UTF-8");
                    OutputStream os = httpCon.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    int responseCode = httpCon.getResponseCode();
                    System.out.println("response code: " + responseCode);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent activity = new Intent(RecipeActivity.this, MainActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RecipeActivity.this.startActivity(activity);
            }
        });
    }
    protected class JsonTask extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params)
        {

            ArrayList<String> value = (ArrayList<String>)intent.getExtras().get("list");
            int pos = intent.getExtras().getInt("position");
            String id = value.get(pos);
            String str="https://recipe-management-service.herokuapp.com/getRecipe/" + id;
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

                JSONObject result = new JSONObject(builder.toString());

                return result;
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
        protected void onPostExecute(JSONObject response)
        {
            if(response != null)
            {
                try {
                    Log.e("App", "Success: "  );
                } catch (Exception ex) {
                    Log.e("App", "Failure", ex);
                }
            }
            try {

                name.setText(response.getString("name"));
                details.setText(response.getString("details"));
                adapter = new ArrayAdapter < String >
                        (RecipeActivity.this, android.R.layout.simple_list_item_1,listItems);
                tagLists.setAdapter(adapter);

                JSONArray myJson = response.getJSONArray("tags");
                jsonTags = myJson;
                for(int i = 0; i < myJson.length(); i++) {
                    listItems.add(myJson.get(i).toString());
                }
                setTitle(name.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
