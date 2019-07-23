package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ViewRecipe extends AppCompatActivity {

    private ScrollView scrollView;
    private EditText recipeName;
    private EditText recipeDetails;
    private Button deleteButton;
    private Button updateButton;
    private ImageButton shareButton;
    private ImageButton likedButton;
    private ListView tagLists;
    private ListView listViewPhotoR;
    private ListView listViewPhoto2R;
    private ListView listViewPhoto3R;

    private RelativeLayout gridViewR;
    private RelativeLayout gridView2R;
    private RelativeLayout gridView3R;



    private Bundle extra;
    private String id;
    private Toolbar toolbar;

    private ImageBitmapAdapter imageAdapterForIngredients;
    private ImageBitmapAdapter imageAdapterForSteps;
    private ImageBitmapAdapter imageAdapterForFinal;

    //private Intent intent;
    ArrayAdapter adapter;
    //delete calısıyor
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    int position=0;
    String str = "";
    private final String baseUrlForDelete = "https://recipe-management-service.herokuapp.com/deleteRecipe/";
    private final String baseUrlForLiked = "https://recipe-management-service.herokuapp.com/liked/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        toolbar = (Toolbar) findViewById(R.id.CustomT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("    Yemek Tarifleri");
        getSupportActionBar().setIcon(R.drawable.asdf);

        recipeName = (EditText) findViewById(R.id.recipeName);
        recipeDetails = (EditText) findViewById(R.id.recipeDescription);
        disableEditText(recipeDetails);
        disableEditText(recipeName);

        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        tagLists = findViewById(R.id.tagLists);
        shareButton= findViewById(R.id.share_button);
        extra = this.getIntent().getExtras();
        likedButton = findViewById(R.id.liked);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextUrl();
            }
        });
        str = SaveSharedPreference.getToken(ViewRecipe.this);
        System.out.println("Token86378162   "+ str);

        listViewPhotoR = findViewById(R.id.listViewPhotoR);
        listViewPhoto2R = findViewById(R.id.listViewPhoto2R);
        listViewPhoto3R = findViewById(R.id.listViewPhoto3R);

        gridViewR = findViewById(R.id.gridViewR);
        gridView2R = findViewById(R.id.gridView2R);
        gridView3R = findViewById(R.id.gridView3R);


        if(extra == null){
            id = null;
        }else {
            id = (String)extra.get("idofItem");
        }


        adapter = new ArrayAdapter < String >
                (ViewRecipe.this, android.R.layout.simple_list_item_1,tags);
        tagLists.setAdapter(adapter);

        new JsonTask().execute();
        adapter.notifyDataSetChanged();

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
                    httpCon.setRequestProperty("Authorization", SaveSharedPreference.getToken(ViewRecipe.this));
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

        likedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //delete recipe
                System.out.println("ID"+id);
                try{
                    URL url = new URL(baseUrlForLiked+id);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpCon.setRequestProperty("Authorization",str);
                    httpCon.setRequestMethod("GET");
                    int responseCode = httpCon.getResponseCode();
                    System.out.println("Response code:" + responseCode);
                    httpCon.connect();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() { //update ise diğer sayfaya gec
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ViewRecipe.this,RecipeActivity.class);
                intent.putExtra("idofItem", id);

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

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String value =(String) extra.get("idofItem");

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "https://recipe-management-service-web.herokuapp.com/recipe.html?id=" + value);

        startActivity(Intent.createChooser(share, "Tarifi Paylaş!"));
    }

    protected class JsonTask extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params)
        {

            String value =(String) extra.get("idofItem");
            System.out.println(value);


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
        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    Log.e("App", "Success: ");
                } catch (Exception ex) {
                    Log.e("App", "Failure", ex);
                }
            }
            try {
                recipeName.setText(response.getString("name"));
                recipeDetails.setText(response.getString("details"));
                JSONArray myJson = response.getJSONArray("tags");

                JSONArray jsonPhotosIng = response.getJSONArray("ingredients");
                ArrayList<Photo> photosIng = new ArrayList<>();

                for (int k = 0; k < jsonPhotosIng.length(); k++) {
                    Photo p = new Photo();
                    JSONObject jsonPhoto = (JSONObject) jsonPhotosIng.get(k);
                    p.setId(jsonPhoto.getString("id"));
                    p.setPhotoLink(jsonPhoto.getString("photoLink"));
                    p.setCloudinaryId(jsonPhoto.getString("publicCloudinaryId"));
                    photosIng.add(p);
                }
                ArrayList<Bitmap> imageBitmapsForIngredients = new ArrayList<>();

                for (int i = 0; i < photosIng.size(); i++ ){

                    imageBitmapsForIngredients.add(showPhotos(photosIng.get(i)));
                }
                if(photosIng.size() > 0){
                    gridViewR.setVisibility(View.VISIBLE);
                }
                imageAdapterForIngredients = new ImageBitmapAdapter(ViewRecipe.this,R.layout.row_of_image,imageBitmapsForIngredients);
                imageAdapterForIngredients.notifyDataSetChanged();
                listViewPhotoR.setAdapter(imageAdapterForIngredients);


                JSONArray jsonPhotosCook = response.getJSONArray("cookingSteps");
                ArrayList<Photo> photosCook = new ArrayList<>();
                for (int k = 0; k < jsonPhotosCook.length(); k++) {
                    Photo p = new Photo();
                    JSONObject jsonPhoto = (JSONObject) jsonPhotosCook.get(k);
                    p.setId(jsonPhoto.getString("id"));
                    p.setPhotoLink(jsonPhoto.getString("photoLink"));
                    p.setCloudinaryId(jsonPhoto.getString("publicCloudinaryId"));
                    photosCook.add(p);
                }
                ArrayList<Bitmap> imageBitmapsForSteps = new ArrayList<>();
                for (int i = 0; i < photosCook.size(); i++ ){

                    imageBitmapsForSteps.add(showPhotos(photosCook.get(i)));
                }
                if(photosCook.size() > 0){
                    gridView2R.setVisibility(View.VISIBLE);
                }
                imageAdapterForSteps = new ImageBitmapAdapter(ViewRecipe.this,R.layout.row_of_image,imageBitmapsForSteps);
                imageAdapterForSteps.notifyDataSetChanged();
                listViewPhoto2R.setAdapter(imageAdapterForSteps);


                JSONArray jsonPhotos = response.getJSONArray("photos");
                ArrayList<Photo> photos = new ArrayList<>();
                for (int k = 0; k < jsonPhotos.length(); k++) {
                    Photo p = new Photo();
                    JSONObject jsonPhoto = (JSONObject) jsonPhotos.get(k);
                    p.setId(jsonPhoto.getString("id"));
                    p.setPhotoLink(jsonPhoto.getString("photoLink"));
                    p.setCloudinaryId(jsonPhoto.getString("publicCloudinaryId"));
                    photos.add(p);
                }
                ArrayList<Bitmap> imageBitmapForFinal = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++ ){
                    imageBitmapForFinal.add(showPhotos(photos.get(i)));
                }
                if(photos.size() > 0){
                    System.out.println("GİRDİ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    gridView3R.setVisibility(View.VISIBLE);
                }
                imageAdapterForFinal = new ImageBitmapAdapter(ViewRecipe.this,R.layout.row_of_image,imageBitmapForFinal);
                imageAdapterForFinal.notifyDataSetChanged();
                listViewPhoto3R.setAdapter(imageAdapterForFinal);


                System.out.println(imageBitmapForFinal.get(0) + "         DOLU OLMASI LAZIM ARTIK AQ ");



                for (int i = 0; i < myJson.length(); i++) {
                    tags.add(myJson.get(i).toString());
                }
                setTitle(recipeName.getText().toString());
                adapter.notifyDataSetChanged();

                setTitle(recipeName.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap showPhotos(Photo p){
        URL url = null;
        try {
            url = new URL(p.getPhotoLink());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("BITMAP:                 " + bmp);
        return bmp;
    }

}
