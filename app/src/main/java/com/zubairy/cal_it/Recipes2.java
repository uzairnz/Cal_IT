package com.zubairy.cal_it;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

/**
 * Created by Rule on 9/29/2017.
 */

public class Recipes2 extends Fragment {
    TextView text;
    private int recipeID;
    MyDBHandler dbHandler;
    Dish viewDish;
    FragmentManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes2, container, false);
        manager = getFragmentManager();
        dbHandler = new MyDBHandler(getActivity(), null, null, 1);

        recipeID = getActivity().getIntent().getIntExtra("EDIT_RECIPE_ID", -1);

        if (recipeID != -1) {
            try {
                viewDish = dbHandler.dbToObject(recipeID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView ing = (TextView) view.findViewById(R.id.textView6);
            ing.setVisibility(View.VISIBLE);
            TextView dir = (TextView) view.findViewById(R.id.textView8);
            dir.setVisibility(View.VISIBLE);

            ImageView recipeImg = (ImageView) view.findViewById(R.id.recipeImg);
            recipeImg.setImageBitmap(viewDish.getImage());

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(viewDish.getName());

            TextView calories = (TextView) view.findViewById(R.id.calories);
            if(viewDish.getCalories() == 0){
                calories.setVisibility(View.GONE);
            } else {
                calories.setText(viewDish.getCalories() + " calories");
            }

            TextView directions = (TextView) view.findViewById(R.id.directions);
            directions.setText(viewDish.getDirections());

            TextView ingredients = (TextView) view.findViewById(R.id.ingredients);
            String strIng = "";
            for (int i = 0; i < viewDish.getIngredients().size(); i++) {
                strIng += "- " + viewDish.getIngredients().get(i);
                strIng += "\n";
            }
            ingredients.setText(strIng);
        }

        return view;
    }

    public void updateData(Dish dish) {
        Recipes recipesFrag = (Recipes) manager.findFragmentByTag("Recipes3");
        LinearLayout recipeLinearLayout = (LinearLayout) recipesFrag.getView().findViewById(R.id.recipeLinearLayout);
        for(int i = 0; i<recipeLinearLayout.getChildCount(); i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // If we're running on Honeycomb or newer, then we can use the Theme's
                // selectableItemBackground to ensure that the View has a pressed state
                TypedValue outValue = new TypedValue();
                getActivity().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                //recipeLinearLayout.getChildAt(i).setBackgroundResource(outValue.resourceId);
                Button button = (Button) recipeLinearLayout.getChildAt(i).findViewById(R.id.btnRecipeName);
                button.setBackgroundResource(outValue.resourceId);
            }
        }

        Button btn = (Button) recipesFrag.getView().findViewWithTag(dish.getName());
        btn.setBackgroundColor(0xFFDDDDDD);

        TextView ing = (TextView) getView().findViewById(R.id.textView6);
        ing.setVisibility(View.VISIBLE);
        TextView dir = (TextView) getView().findViewById(R.id.textView8);
        dir.setVisibility(View.VISIBLE);


        ImageView recipeImg = (ImageView) getView().findViewById(R.id.recipeImg);
        recipeImg.setImageBitmap(dish.getImage());

        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(dish.getName());

        TextView calories = (TextView) getView().findViewById(R.id.calories);
        if(dish.getCalories() == 0){
            calories.setVisibility(View.GONE);
        } else {
            calories.setText(dish.getCalories() + " calories");
        }

        TextView directions = (TextView) getView().findViewById(R.id.directions);
        directions.setText(dish.getDirections());

        TextView ingredients = (TextView) getView().findViewById(R.id.ingredients);
        String strIng = "";
        for (int i = 0; i < dish.getIngredients().size(); i++) {
            strIng += "- " + dish.getIngredients().get(i);
            strIng += "\n";
        }
        ingredients.setText(strIng);

    }
}
