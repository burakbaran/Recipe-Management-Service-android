package com.recipemanagement.recipemanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.listView); //eklenen recipeler listelenmesi icin
    }
}
