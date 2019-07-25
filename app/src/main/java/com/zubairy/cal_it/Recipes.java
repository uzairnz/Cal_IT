package com.zubairy.cal_it;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;


/**
 * Created by Rule on 9/28/2017.
 */

public class Recipes extends Fragment {
    private FragmentManager manager;
    public LinearLayout parentLinearLayout;
    private LinearLayout parent;
    private ScrollView scrollv;
    private MyDBHandler dbHandler;
    private TextView test;
    private EditText recipeName;
    private Button getRecipe;
    private ImageView getImg;
    private Button recipeBtn;
    private Button goToNewDish;
    private int editId;
    private boolean edit;
    private LinearLayout editTools;
    private LinearLayout editTools2;
    private Meal mealPlanOptions;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recipes, container, false);

        test = (TextView) view.findViewById(R.id.test);
        recipeName = (EditText) view.findViewById(R.id.recipeInput);
        getRecipe = (Button) view.findViewById(R.id.getRecipe);
        getImg = (ImageView) view.findViewById(R.id.getImg);
        dbHandler = new MyDBHandler(getActivity(), null, null, 1);
        parentLinearLayout = (LinearLayout) view.findViewById(R.id.recipeLinearLayout);
        mealPlanOptions = new Meal(getActivity().getApplicationContext());
        editTools = (LinearLayout) view.findViewById(R.id.editTools);
        editTools2 = (LinearLayout) view.findViewById(R.id.recipeLinearLayout);
        manager = getFragmentManager();

        edit = false;

        populateList();
        printDatabase();

        if(getActivity().getIntent().getBooleanExtra("EDITING", false)){
            editTools();
        }


        goToNewDish = (Button) view.findViewById(R.id.goToNewDish);
        goToNewDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewDish(view);
            }
        });

        if(dbHandler.getCountRecipes() == 0){
            goToNewDish.setVisibility(View.VISIBLE);
        } else {
            goToNewDish.setVisibility(View.GONE);
        }


        return view;
    }


    public void onBackPressed() {
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
    }


    public void editTools() {
        if(!edit) {

            for (int i = 0; i < editTools2.getChildCount(); i++) {
                editTools2.findViewWithTag(i).setVisibility(View.VISIBLE);
            }

            //editTools.setVisibility(View.VISIBLE);

            Context context = getActivity().getApplicationContext();
            CharSequence text = Integer.toString(editTools2.getChildCount());
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            //toast.show();
            edit = true;
        } else {
            edit = false;
            LinearLayout editTools2 = (LinearLayout) getView().findViewById(R.id.recipeLinearLayout);

            for (int i = 0; i < editTools2.getChildCount(); i++) {
                editTools2.findViewWithTag(i).setVisibility(View.GONE);
            }
        }
    }

    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        test.setText(dbString);
    }

    public void removeAll(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dbHandler.removeAll();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), RecipeActivity.class));

                        Context context = getActivity().getApplicationContext();
                        CharSequence text = "Deleted all recipes.";
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete all recipes");
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();



    }

    public void startNewDish(View v){
        getActivity().finish();
        startActivity(new Intent(getActivity(), NewDish.class));
    }

    public void getRecipe(View v) throws JSONException {
        if(!recipeName.getText().toString().equals("")) {
            if(dbHandler.nameExists(recipeName.getText().toString())){
                Dish getDish = dbHandler.dbToObject(recipeName.getText().toString());
                getImg.setImageBitmap(getDish.getImage());

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Dish retrieved");
                alertDialog.setMessage(getDish.getName() + "\n" + getDish.getDirections() + "\n" + getDish.getIngredients() + "\n" + getDish.getImage() + "\n");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                Context context = getActivity().getApplicationContext();
                CharSequence text = "Recipe does not exist.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please input recipe name!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    public void populateList(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;

        // Add the new row before the add field button.
        for(int i = 0; i< dbHandler.getCountRecipes(); i++) {
            rowView = inflater.inflate(R.layout.recipelist, null);
            Button btn = (Button) rowView.findViewById(R.id.btnRecipeName);
            Button btn2 = (Button) rowView.findViewById(R.id.addToMealPlan);
            ImageButton btn3 = (ImageButton) rowView.findViewById(R.id.btnEditRecipe);
            Button btn4 = (Button) rowView.findViewById(R.id.deleteRecipeDB);

            LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.editTools);
            layout.setTag(i);
            btn.setText(dbHandler.getListNames().get(i));
            btn.setTag(dbHandler.getListNames().get(i));

            btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    onViewRecipe(view);
                }
            });

            btn.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    onEditRecipeLongClick(view);
                    return true;
                }
            });

            btn2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    try {
                        onAddToMealPlan(view);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            btn3.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    onEditRecipe(view);
                }
            });

            btn4.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    onDeleteRecipe(view);
                }
            });

            parentLinearLayout.addView(rowView);
        }
    }

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.

        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount());

    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onAddToMealPlan(View v) throws JSONException {
        LinearLayout parent = (LinearLayout) v.getParent();
        Button recipeBtn = (Button) parent.findViewById(R.id.btnRecipeName);
        String recName = recipeBtn.getText().toString();
        Dish dish = dbHandler.dbToObject(dbHandler.getId(recName));

        mealPlanOptions.addDishToMeal(recName);
        dbHandler.addToGroceries(dish);

        Context context = getActivity().getApplicationContext();
        CharSequence text = "Added " + recipeBtn.getText().toString() + " to meal plan options. " + "(" + mealPlanOptions.getDishAmt(recName) + ")";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onDeleteRecipe(View v){
        LinearLayout parent = (LinearLayout) v.getParent().getParent();
        recipeBtn = (Button) parent.findViewById(R.id.btnRecipeName);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dbHandler.deleteDish(recipeBtn.getText().toString());
                        Intent intent = new Intent(getActivity(), RecipeActivity.class);
                        intent.putExtra("EDITING", true);

                        getActivity().finish();
                        //startActivity(new Intent(Recipes.this, Recipes.class));

                        startActivityForResult(intent, 0);

                        Context context = getActivity().getApplicationContext();
                        CharSequence text = "Deleted " + recipeBtn.getText().toString();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete " + recipeBtn.getText().toString());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    public void onEditRecipe(View v){
        //View parent = (View) v.getParent();
        LinearLayout parent = (LinearLayout) v.getParent().getParent();
        Button recipeBtn = (Button) parent.findViewById(R.id.btnRecipeName);

        editId = dbHandler.getId(recipeBtn.getText().toString());
        Intent intent = new Intent(getActivity().getBaseContext(), NewDish.class);
        intent.putExtra("EDIT_RECIPE_ID", editId);
        intent.putExtra("EDITING_MODE", true);
        startActivityForResult(intent, 0);
    }

    public void onViewRecipe(View v) {
        //View parent = (View) v.getParent();
        LinearLayout parent = (LinearLayout) v.getParent();
        Button recipeBtn = (Button) parent.findViewById(R.id.btnRecipeName);

        editId = dbHandler.getId(recipeBtn.getText().toString());

        if(getRotation(getActivity().getApplicationContext()).equals("landscape") || getRotation(getActivity().getApplicationContext()).equals("reverse landscape")){
            Recipes2 recipeFrag = (Recipes2) manager.findFragmentById(R.id.fragment2);
            try {
                recipeFrag.updateData(dbHandler.dbToObject(editId));
            }  catch (JSONException e) {
                e.printStackTrace();
            }

        } else if(getRotation(getActivity().getApplicationContext()).equals("portrait") || getRotation(getActivity().getApplicationContext()).equals("reverse portrait")) {
            Intent intent = new Intent(getActivity().getBaseContext(), RecipeViewActivity.class);
            intent.putExtra("EDIT_RECIPE_ID", editId);
            startActivityForResult(intent, 0);
        }

    }

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse portrait";
            default:
                return "reverse landscape";
        }
    }

    public void onEditRecipeLongClick(View v){
        //View parent = (View) v.getParent();
        LinearLayout parent = (LinearLayout) v.getParent();
        Button recipeBtn = (Button) parent.findViewById(R.id.btnRecipeName);

        editId = dbHandler.getId(recipeBtn.getText().toString());
        Intent intent = new Intent(getActivity().getBaseContext(), NewDish.class);
        intent.putExtra("EDIT_RECIPE_ID", editId);
        intent.putExtra("EDITING_MODE", true);
        startActivityForResult(intent, 0);
    }



}
