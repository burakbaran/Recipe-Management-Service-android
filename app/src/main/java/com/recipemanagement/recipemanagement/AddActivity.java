package com.recipemanagement.recipemanagement;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int PICK_IMAGE = 10;
    private JSONArray tag = new JSONArray();
    private Button updateOrCreateEvent, addTagToRecipe, photoAdd;
    private ListView tagLists;
    private ArrayAdapter adapter;

    private LinearLayout grid;

    String imageEncoded;
    List<String> imagesEncodedList;

    private ArrayList<String> listItems=new ArrayList<String>(); // getten gelen tagler string



    private EditText name, description, tags;
    private final String baseUrl = "https://recipe-management-service.herokuapp.com/addRecipe";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        System.out.println("TOKEN:  "  + SaveSharedPreference.getToken(AddActivity.this));
        toolbar = (Toolbar) findViewById(R.id.CustomToolBa);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("    Yemek Tarifleri");
        getSupportActionBar().setIcon(R.drawable.asdf);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        updateOrCreateEvent = findViewById(R.id.addRecipe);
        name = findViewById(R.id.recipeDetails);
        description = findViewById(R.id.recipeDescription);
        tags = findViewById(R.id.recipeTags);
        tagLists = findViewById(R.id.tagLists);

        addTagToRecipe = findViewById(R.id.addTag);
        photoAdd= findViewById(R.id.photoAdd);
        grid = findViewById(R.id.gridView);

        photoAdd.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        }));




        adapter = new ArrayAdapter< String >
                (AddActivity.this, android.R.layout.simple_list_item_1,listItems);
        tagLists.setAdapter(adapter);

        tagLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.remove(listItems.get(position));
                tag.remove(position);
                adapter.notifyDataSetChanged();

            }
        });

        addTagToRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag.put(tags.getText().toString());
                listItems.add(tags.getText().toString());
                adapter.notifyDataSetChanged();
                tags.getText().clear();
            }
        });

        updateOrCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL url = new URL(baseUrl);
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                    httpCon.setRequestMethod("POST");
                    httpCon.setRequestProperty("Content-Type","application/json");
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("name",name.getText().toString());
                    eventObject.put("details", description.getText().toString());


                    eventObject.put("tags", tag);
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
                Intent activity = new Intent(AddActivity.this, MainActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                AddActivity.this.startActivity(activity);
            }
        });
    }
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ){
          //  imageUri = data.getData();
           // image.setImageURI(imageUri);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            imagesEncodedList = new ArrayList<String>();

            if (data.getData() != null) {         //on Single image selected

                Uri mImageUri = data.getData();

                // Get the cursor
                Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                ImageView im = new ImageView(AddActivity.this);
                im.setBackgroundResource(R.drawable.btn_bg);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
                // im.setScaleType(ImageView.ScaleType.FIT_XY);
                im.setPadding(10, 10, 10, 10);
                im.setImageURI(mImageUri);
                im.setLayoutParams(grid.getLayoutParams());
                grid.addView(im, layoutParams);

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded = cursor.getString(columnIndex);
                cursor.close();

            }else {

                if(data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        System.out.println(item.getUri());
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();
                        ImageView im = new ImageView(AddActivity.this);
                       // im.setBackgroundResource(R.drawable.btn_bg);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
                         im.setScaleType(ImageView.ScaleType.FIT_XY);
                        im.setPadding(10, 10, 10, 10);
                        im.setImageURI(uri);
                        im.setLayoutParams(grid.getLayoutParams());
                        grid.addView(im, layoutParams);


                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        imagesEncodedList.add(imageEncoded);
                        cursor.close();
                    }
                }
            }

        }
    }

}
