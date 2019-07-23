package com.recipemanagement.recipemanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.recipemanagement.recipemanagement.models.RecipeModel;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RecipeArrayAdapter extends ArrayAdapter {


    private List<RecipeModel> recipeModelList;
    private int resource;
    private LayoutInflater layoutInflater;

    public List<RecipeModel> getRecipeModelList() {
        return recipeModelList;
    }


    public RecipeArrayAdapter(Context context, int resource, List<RecipeModel> objects) {
        super(context, resource, objects);
        recipeModelList = objects;
        this.resource = resource;
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = layoutInflater.inflate(resource,null);
        }
        ImageView im;
        TextView name;
        TextView description;
        System.out.println("SOUT ATILABİLİYORMUŞ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        im = (ImageView)convertView.findViewById(R.id.imageView);
        name =(TextView)convertView.findViewById(R.id.nameOfRecipe);
        description =(TextView)convertView.findViewById(R.id.detailsOfRecipe);

        im.setImageBitmap(recipeModelList.get(position).getImage());
        im.setScaleType(ImageView.ScaleType.FIT_XY);
        name.setText(recipeModelList.get(position).getName());
        description.setText(recipeModelList.get(position).getDetails());

       return convertView;
    }
}
