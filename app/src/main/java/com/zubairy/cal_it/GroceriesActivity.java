package com.zubairy.cal_it;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rule on 10/4/2017.
 */

public class GroceriesActivity extends AppCompatActivity {
    MyDBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);

        dbHandler = new MyDBHandler(this, null, null, 1);
        //String[] foods = {"Apple", "Oranges", "Grape"};
        ArrayList<String> foods = dbHandler.getListIngredients();

        ListAdapter groceriesAdapter = new GroceriesAdapter(this, foods);
        ListView groceriesList = (ListView) findViewById(R.id.groceriesList);
        groceriesList.setAdapter(groceriesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_groceries, menu); //inflate our menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                GroceriesActivity.this.finish();
                startActivity(new Intent(GroceriesActivity.this, MainActivity.class));
                break;

            case R.id.item_delete:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dbHandler.removeAllIngredients();
                                GroceriesActivity.this.finish();
                                startActivity(new Intent(GroceriesActivity.this, GroceriesActivity.class));

                                Context context = getApplicationContext();
                                CharSequence text = "Deleted all ingredients.";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(GroceriesActivity.this);
                builder.setTitle("Delete all ingredients");
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                break;
        }
        return true;
    }

}
