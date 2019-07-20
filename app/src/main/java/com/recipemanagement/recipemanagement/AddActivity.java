package com.recipemanagement.recipemanagement;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.recipemanagement.recipemanagement.utils.MultipartUtility;
import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int PICK_IMAGE = 10;
    private JSONArray tag = new JSONArray();
    private Button updateOrCreateEvent, addTagToRecipe, photoAdd;
    private ListView tagLists;
    private ArrayAdapter adapter;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris = new ArrayList<Uri>();

    private ListView listView;

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
        listView = findViewById(R.id.listViewPhoto);
        //grid = findViewById(R.id.gridView);



        photoAdd.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openGallery();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        final String imageFilePath2 = "storage/emulated/0/DCIM/Camera/IMG_20190709_142108.jpg";
        //final String imageFilePath = "storage/emulated/0/DCIM/Camera/IMG_20190712_131441.jpg";

        final String[] imageFilePaths = new String[2];

        //imageFilePaths[0] = imageFilePath;
        imageFilePaths[1] = imageFilePath2;

        updateOrCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tags = "";
                    for (int i = 0; i < listItems.size(); i++){
                        tags += listItems.get(i) + ",";
                    }
                    System.out.println("MULTİ 1");
                    MultipartUtility multipart = new MultipartUtility("https://recipe-management-service.herokuapp.com/addRecipe","UTF-8",SaveSharedPreference.getToken(AddActivity.this));
                    System.out.println("TOKE   " + SaveSharedPreference.getToken(AddActivity.this));
                    //multipart.addHeaderField("Authorization",SaveSharedPreference.getToken(AddActivity.this) );

                    multipart.addFormField("name",name.getText().toString());
                    multipart.addFormField("details",description.getText().toString());
                    multipart.addFormField("tags",tags);

                    for(int i = 0; i<imagesEncodedList.size(); i++) {
                        multipart.addFilePart("file", new File(imagesEncodedList.get(i)));
                    }

                    System.out.println("MULTİ 2");

                    List<String> response = multipart.finish();

                    System.out.println("SERVER REPLIED:");

                    for (String line : response) {
                        System.out.println(line);
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                }


                Intent activity = new Intent(AddActivity.this, MainActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                AddActivity.this.startActivity(activity);
            }
        });
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data ){
            //  imageUri = data.getData();
            // image.setImageURI(imageUri);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            imagesEncodedList = new ArrayList<String>();

            if (data.getData() != null ) {         //on Single image selected

                Uri mImageUri = data.getData();

                // Get the cursor
                Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                imageUris.add(mImageUri);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded = cursor.getString(columnIndex);
                imagesEncodedList.add(imageEncoded);
                cursor.close();

            }else {
            imageAdapter = new ImageAdapter(AddActivity.this,R.layout.row_of_image,imageUris);
            listView.setAdapter(imageAdapter);
                if(data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        System.out.println(item.getUri());
                        String fileId = DocumentsContract.getDocumentId(uri);
                        String id = fileId.split(":")[1];
                        String[] column = {MediaStore.Images.Media.DATA};

                        String selector = MediaStore.Images.Media._ID + "=?";
                        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, selector, new String[]{id}, null);
                        // Move to first row
                        int columnIndex = cursor.getColumnIndex(column[0]);
                        if (cursor.moveToFirst()) {
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            imageUris.add(uri);
                            System.out.println("IMAGE:       " +  imageEncoded);

                        }

                       /* imageUris.add(uri);
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        System.out.println("IMAGE:       " +  imageEncoded);
                        imagesEncodedList.add(imageEncoded);*/
                        cursor.close();
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

        }
        super.onActivityResult(requestCode,resultCode,data);

    }


}