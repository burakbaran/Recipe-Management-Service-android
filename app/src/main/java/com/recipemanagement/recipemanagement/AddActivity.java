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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.recipemanagement.recipemanagement.utils.MultipartUtility;
import com.recipemanagement.recipemanagement.utils.SaveSharedPreference;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final int PICK_IMAGE = 10;
    private JSONArray tag = new JSONArray();
    private Button updateOrCreateEvent, addTagToRecipe, photoAdd, photoAdd2, photoAdd3;
    private ListView tagLists;
    private ArrayAdapter adapter;
    private ImageAdapter imageAdapterForIngredients;
    private ImageAdapter imageAdapterForSteps;
    private ImageAdapter imageAdapterForFinal;
    private ArrayList<Uri> imageUrisForIngredients = new ArrayList<Uri>();
    private ArrayList<Uri> imageUrisForSteps= new ArrayList<Uri>();
    private ArrayList<Uri> imageUrisForFinals = new ArrayList<Uri>();
    private RelativeLayout gridView;
    private RelativeLayout gridView2;
    private RelativeLayout gridView3;

    private ListView listView;
    private ListView listView2;
    private ListView listView3;

    private TextView desc1;
    private TextView desc2;
    private TextView desc3;
    private static int where;

    String imageEncoded;
    List<String> imagesEncodedListForIngredients = new ArrayList<>();
    List<String> imagesEncodedListForCookingSteps= new ArrayList<>();
    List<String> imagesEncodedListForFinal= new ArrayList<>();


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
        photoAdd2= findViewById(R.id.photoAdd2);
        photoAdd3= findViewById(R.id.photoAdd3);
        listView = findViewById(R.id.listViewPhoto);
        listView2 = findViewById(R.id.listViewPhoto2);
        listView3 = findViewById(R.id.listViewPhoto3);
        gridView = findViewById(R.id.gridView);
        gridView2 = findViewById(R.id.gridView2);
        gridView3 = findViewById(R.id.gridView3);

        desc1 = findViewById(R.id.textView_);
        desc2 = findViewById(R.id.textView_2);
        desc3 = findViewById(R.id.textView_3);

        photoAdd.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openGalleryForIngredients();
                        if(imageUrisForIngredients != null){
                            gridView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }));
        photoAdd2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openGalleryForCookingSteps();
                        if(imageUrisForSteps != null){
                            gridView2.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }));
        photoAdd3.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openGalleryForFinal();
                        if(imageUrisForFinals != null){
                            gridView3.setVisibility(View.VISIBLE);
                        }


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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                imageAdapterForIngredients.remove(imagesEncodedListForIngredients.get(position));
                imagesEncodedListForIngredients.remove(position);
                imageUrisForIngredients.remove(position);
                imageAdapterForIngredients.notifyDataSetChanged();
                if(imagesEncodedListForIngredients.size() == 0){
                    desc1.setVisibility(View.INVISIBLE);
                }

            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                imageAdapterForSteps.remove(imagesEncodedListForCookingSteps.get(position));
                imagesEncodedListForCookingSteps.remove(position);
                imageUrisForSteps.remove(position);
                imageAdapterForSteps.notifyDataSetChanged();
                if(imagesEncodedListForCookingSteps.size() == 0){
                    desc2.setVisibility(View.INVISIBLE);
                }


            }
        });
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                imageAdapterForFinal.remove(imagesEncodedListForFinal.get(position));
                imagesEncodedListForFinal.remove(position);
                imageUrisForFinals.remove(position);
                imageAdapterForFinal.notifyDataSetChanged();
                if(imagesEncodedListForFinal.size() == 0){
                    desc3.setVisibility(View.INVISIBLE);
                }

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
                    MultipartUtility multipart = new MultipartUtility("https://recipe-management-service.herokuapp.com/addRecipe","UTF-8",SaveSharedPreference.getToken(AddActivity.this),"POST");
                    System.out.println("TOKE   " + SaveSharedPreference.getToken(AddActivity.this));
                    //multipart.addHeaderField("Authorization",SaveSharedPreference.getToken(AddActivity.this) );

                    multipart.addFormField("name",name.getText().toString());
                    multipart.addFormField("details",description.getText().toString());
                    multipart.addFormField("tags",tags);

                    for(int i = 0; i< imagesEncodedListForIngredients.size(); i++) {
                        multipart.addFilePart("fileIngredients", new File(imagesEncodedListForIngredients.get(i)));
                    }
                    for(int i = 0; i< imagesEncodedListForCookingSteps.size(); i++) {
                        multipart.addFilePart("fileCookingSteps", new File(imagesEncodedListForCookingSteps.get(i)));
                    }
                    for(int i = 0; i< imagesEncodedListForFinal.size(); i++) {
                        multipart.addFilePart("file", new File(imagesEncodedListForFinal.get(i)));
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

    private void openGalleryForIngredients(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        where = 0;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
    }
    private void openGalleryForCookingSteps(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        where = 1;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
    }
    private void openGalleryForFinal(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        where = 2;
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

            ArrayList<String> imageList = new ArrayList<String>();
            ArrayList<Uri> imUris = new ArrayList<>();

            if (data.getData() != null ) {         //on Single image selected

                Uri mImageUri = data.getData();
                String fileId = DocumentsContract.getDocumentId(mImageUri);
                String id = fileId.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};

                String selector = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, selector, new String[]{id}, null);
                // Move to first row
                cursor.moveToFirst();

                imUris.add(mImageUri);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded = cursor.getString(columnIndex);

                imageList.add(imageEncoded);
                cursor.close();

            }else {

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
                            imageList.add(imageEncoded);
                            imUris.add(uri);
                            System.out.println("IMAGE:       " +  imageEncoded);

                        }

                       /* imageUris.add(uri);
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        System.out.println("IMAGE:       " +  imageEncoded);
                        imagesEncodedListForIngredients.add(imageEncoded);*/
                        cursor.close();
                    }
                }
            }
            if(where == 0){
                imagesEncodedListForIngredients.addAll(imageList);
                imageUrisForIngredients.addAll(imUris);

                imageAdapterForIngredients = new ImageAdapter(AddActivity.this,R.layout.row_of_image,imageUrisForIngredients);
                imageAdapterForIngredients.notifyDataSetChanged();
                listView.setAdapter(imageAdapterForIngredients);
            }
            if(where == 1){
                imagesEncodedListForCookingSteps.addAll(imageList);
                imageUrisForSteps.addAll(imUris);
                imageAdapterForSteps = new ImageAdapter(AddActivity.this,R.layout.row_of_image,imageUrisForSteps);
                imageAdapterForSteps.notifyDataSetChanged();
                listView2.setAdapter(imageAdapterForSteps);

            }
            if(where == 2){
                imagesEncodedListForFinal.addAll(imageList);
                imageUrisForFinals.addAll(imUris);
                System.out.println(imageUrisForFinals);
                imageAdapterForFinal = new ImageAdapter(AddActivity.this,R.layout.row_of_image,imageUrisForFinals);
                imageAdapterForFinal.notifyDataSetChanged();
                listView3.setAdapter(imageAdapterForFinal);
            }

        }

        super.onActivityResult(requestCode,resultCode,data);

    }


}