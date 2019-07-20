package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
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

import com.recipemanagement.recipemanagement.utils.MultipartUtility;
import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {
    private EditText name,details,tags;
    private ListView tagLists;
    private ArrayList<String> listItems=new ArrayList<String>(); // getten gelen tagler string
    private Button updateOrCreateEvent, addTagToRecipe , likeButton;
    private JSONArray jsonTags;
    private ArrayAdapter adapter;
    private final String baseUrl = "https://recipe-management-service.herokuapp.com/updateRecipe/";
    private final String baseUrlLiked = "https://recipe-management-service.herokuapp.com/liked/";
    private final String baseUrlForDelete = "https://recipe-management-service.herokuapp.com/deleteRecipe/";
    private String id;
    private Intent intent;
    private Toolbar toolbar;
    static String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_recipe);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.CustomTool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("    Yemek Tarifleri");
        getSupportActionBar().setIcon(R.drawable.asdf);

        name = findViewById(R.id.recipeDetails);
        details = findViewById(R.id.recipeDescription);
        tags = findViewById(R.id.recipeTags);
        tagLists = findViewById(R.id.tagLists);
        addTagToRecipe = findViewById(R.id.addTag);
        updateOrCreateEvent = findViewById(R.id.addRecipe);

        //tagLists.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        intent = getIntent();
        new JsonTask().execute();

        tagLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.remove(listItems.get(position));
                jsonTags.remove(position);
                adapter.notifyDataSetChanged();

            }
        });


        addTagToRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonTags.put(tags.getText().toString());
                listItems.add(tags.getText().toString());
                adapter.notifyDataSetChanged();
                tags.getText().clear();
            }
        });




        //delete activity
        Bundle lol = this.getIntent().getExtras();


        if(lol == null){
            id = null;
        }else {
            id = (String) intent.getExtras().get("idofItem");
        }

        updateOrCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value =(String) intent.getExtras().get("idofItem");
                try {
                    String tags = "";
                    for (int i = 0; i < listItems.size(); i++){
                        tags += listItems.get(i) + ",";
                    }
                    System.out.println("MULTİ 1");
                    MultipartUtility multipart = new MultipartUtility("https://recipe-management-service.herokuapp.com/updateRecipe/" + value,"UTF-8",SaveSharedPreference.getToken(RecipeActivity.this));
                    System.out.println("TOKE   " + SaveSharedPreference.getToken(RecipeActivity.this));
                    //multipart.addHeaderField("Authorization",SaveSharedPreference.getToken(AddActivity.this) );

                    multipart.addFormField("name",name.getText().toString());
                    multipart.addFormField("details",details.getText().toString());
                    multipart.addFormField("tags",tags);

                    /*for(int i = 0; i < imagesEncodedList.size(); i++) {
                        multipart.addFilePart("file", new File(imagesEncodedList.get(i)));
                    }*/

                    System.out.println("MULTİ 2");

                    List<String> response = multipart.finish();

                    System.out.println("SERVER REPLIED:");

                    for (String line : response) {
                        System.out.println(line);
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
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

            String value =(String) intent.getExtras().get("idofItem");
            System.out.println(value);

            int pos = intent.getExtras().getInt("position");

            String str="https://recipe-management-service.herokuapp.com/getRecipe/" + value;
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