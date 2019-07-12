package com.recipemanagement.recipemanagement;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
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

        try {
            if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        final String imageFilePath2 = "storage/emulated/0/DCIM/Camera/IMG_20190712_121659.jpg";
        final String imageFilePath = "storage/emulated/0/DCIM/Camera/IMG_20190712_131441.jpg";

        final String[] imageFilePaths = new String[2];

        imageFilePaths[0] = imageFilePath;
        imageFilePaths[1] = imageFilePath2;

        updateOrCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<String, String>(2);
                params.put("name", name.getText().toString());
                params.put("details", description.getText().toString());
                params.put("tags", "1,2,3");
                String result = "";
                try {
                    result = multipartRequest("https://recipe-management-service.herokuapp.com/addRecipe", params, imageFilePaths, "file", "multipart/form-data");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("*+*+++**: " + result);

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

    public String multipartRequest(String urlTo, Map<String, String> parmas, String[] filepaths, String filefield, String fileMimeType) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "**" + Long.toString(System.currentTimeMillis()) + "**";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 4 * 2048 * 2048;

        try {
            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Authorization", SaveSharedPreference.getToken(AddActivity.this));

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            for(int i = 0; i < filepaths.length; i++){
                String[] q = filepaths[i].split("/");
                int idx = q.length - 1;

                File file = new File(filepaths[i]);
                FileInputStream fileInputStream = new FileInputStream(file);

                if(i == 1){
                    System.out.println("Resim 2: " + "Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                }
                else{
                    System.out.println("Resim 1: " + "Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                }

                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                fileInputStream.close();
            }

            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);
                System.out.println(key);
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (200 != connection.getResponseCode()) {
                throw new Exception("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
                System.out.println(line);
            }

            result = this.convertStreamToString(inputStream);


            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}