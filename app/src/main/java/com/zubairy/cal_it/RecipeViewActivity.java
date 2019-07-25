package com.zubairy.cal_it;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;

/**
 * Created by Rule on 10/3/2017.
 */

public class RecipeViewActivity extends AppCompatActivity{
    private int recipeID;
    MyDBHandler dbHandler;
    Dish viewDish;
    FragmentManager manager;
    Recipes2 recipeFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        dbHandler = new MyDBHandler(this, null, null, 1);
        manager = getFragmentManager();
        recipeFrag = (Recipes2) manager.findFragmentById(R.id.fragment3);
        recipeID = getIntent().getIntExtra("EDIT_RECIPE_ID", -1);

        try {
            getSupportActionBar().setTitle(dbHandler.dbToObject(recipeID).getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
