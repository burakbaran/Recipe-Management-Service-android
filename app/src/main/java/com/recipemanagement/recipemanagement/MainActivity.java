package com.recipemanagement.recipemanagement;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.recipemanagement.recipemanagement.models.Photo;
import com.recipemanagement.recipemanagement.models.RecipeModel;
import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String baseUrl = "https://recipe-management-service.herokuapp.com/getRecipes";
    private ListView listView;
    private Toolbar toolbar;
    private ImageView imageView;

    FloatingActionButton addButton;

    private RecipeArrayAdapter adapter;
    ArrayList<String> listItems=new ArrayList<String>();

    ArrayList<String> itemIds=new ArrayList<String>();
    ArrayList<String> details =new ArrayList<String>();
    ArrayList<String> taglist = new ArrayList<String>();
    private static SwipeRefreshLayout pullToRefresh;

    private static HashMap<String,Bitmap> cacheForUrls = new HashMap<String,Bitmap>(); //cache for fewer request
   // TODO: silinen resim cacheden silinmeli




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //adding sections in menu bar
        menuInflater.inflate(R.menu.search_recipe,menu);
        menuInflater.inflate(R.menu.auto_delete,menu);
        menuInflater.inflate(R.menu.about_us,menu);
        menuInflater.inflate(R.menu.logout,menu);
        return super.onCreateOptionsMenu(menu);
    }


    //move on the selected activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.search_recipe){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.logout){
            SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
            SaveSharedPreference.setToken(getApplicationContext(),"token");
            Intent activity = new Intent(MainActivity.this, ActivityLogin.class);
            //activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle options = ActivityOptions.makeCustomAnimation(MainActivity.this,android.R.anim.fade_in,android.R.anim.fade_out).toBundle();
            MainActivity.this.startActivity(activity,options);
        }

        if(item.getItemId() == R.id.auto_delete){

            Intent activity = new Intent(MainActivity.this, AutoDelete.class);
            activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            MainActivity.this.startActivity(activity);
        }


        if(item.getItemId() == R.id.about_us ){
            Intent intent = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Yemek Tarifleri");
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.CustomToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("    Yemek Tarifleri");
        getSupportActionBar().setIcon(R.drawable.asdf);

        imageView = findViewById(R.id.imageView);




        listView = (ListView)findViewById(R.id.listView);//eklenen recipeler listelenmesi icin
        if(listView != null){
            listView.setOnItemClickListener(this);

        }

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listItems.clear();
                new JsonTask().execute();
                adapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });

        new JsonTask().execute();


    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, ViewRecipe.class);
        List<RecipeModel> list = adapter.getRecipeModelList();
        String idofItem = list.get(position).getId();
        intent.putExtra("idofItem",idofItem);
        // Or / And
        intent.putExtra("id", id);
        startActivity(intent);
    }

    protected class JsonTask extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
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
                    listItems.add(result.getJSONObject(i).getString("name"));
                    itemIds.add(result.getJSONObject(i).getString("id"));
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


            List<RecipeModel> recipeModelList = new ArrayList<>();
            adapter = new RecipeArrayAdapter(getApplicationContext(),R.layout.row_of_list,recipeModelList);
            listView.setAdapter(adapter);
            String a ="";
            for(int i = 0; i < response.size(); i++) {
                JSONObject myJson = response.get(i);
                try {
                    RecipeModel recipeModel = new RecipeModel();
                    recipeModel.setName(myJson.getString("name"));
                    recipeModel.setId(myJson.getString("id"));
                    recipeModel.setDetails(myJson.getString("details"));

                    JSONArray jsonPhotos = myJson.getJSONArray("photos");
                    ArrayList<Photo> photos = new ArrayList<>();
                    for(int k = 0; k < jsonPhotos.length(); k++){
                        Photo p = new Photo();
                        JSONObject jsonPhoto = (JSONObject) jsonPhotos.get(k);
                        p.setId(jsonPhoto.getString("id"));
                        p.setPhotoLink(jsonPhoto.getString("photoLink"));
                        p.setCloudinaryId(jsonPhoto.getString("publicCloudinaryId"));
                        photos.add(p);
                        if(k == 0 && p.getPhotoLink() != null ){
                            URL url = null;
                            try {
                                url = new URL(p.getPhotoLink());

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            Bitmap bmp = null;
                            try {
                                if(cacheForUrls.get(p.getPhotoLink()) == null){
                                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    System.out.println("girdii");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(cacheForUrls.get(p.getPhotoLink()) == null) {
                                cacheForUrls.put(p.getPhotoLink(), bmp);
                            }
                            recipeModel.setImage(cacheForUrls.get(p.getPhotoLink()));
                        }
                    }

                    recipeModel.setPhotos(photos);
                    recipeModelList.add(recipeModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }
}
