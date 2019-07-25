package com.zubairy.cal_it;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = (ImageView) findViewById(R.id.imageView);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        //Intent dbmanager = new Intent(MainActivity.this,AndroidDatabaseManager.class);
                        //startActivity(dbmanager);


                startActivity(new Intent(MainActivity.this, Popup.class));
            }
        });


        ImageButton newDish = (ImageButton) findViewById(R.id.newdish);
        newDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewDish.class));
            }
        });

        ImageButton meals = (ImageButton) findViewById(R.id.meals);
        meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MealsActivity.class));
            }
        });

        ImageButton recipes = (ImageButton) findViewById(R.id.recipes);
        recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecipeActivity.class));
            }
        });

        ImageButton groceries = (ImageButton) findViewById(R.id.groceries);
        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GroceriesActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
