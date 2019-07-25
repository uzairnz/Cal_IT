package com.zubairy.cal_it;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rule on 10/4/2017.
 */

public class GroceriesAdapter extends ArrayAdapter<String> {

    private MyDBHandler dbHandler;
    private float x1, x2;
    static final int MIN_DISTANCE = 75;

    protected GroceriesAdapter(Context context, ArrayList<String> foods) {
        super(context, R.layout.grocerieslist, foods);
        dbHandler = new MyDBHandler(context, null, null, 1);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View customView = inflater.inflate(R.layout.grocerieslist, parent, false);

        final String singleFoodItem = getItem(position);
        int amt = dbHandler.getCountIngredient(singleFoodItem);

        final TextView name = (TextView) customView.findViewById(R.id.ingredientName);
        name.setText(singleFoodItem + " (" + amt + ")");
        name.setTag(singleFoodItem);
        final ImageView arrow = (ImageView) customView.findViewById(R.id.arrow);

        final RelativeLayout ctrlAmtBtn = (RelativeLayout) customView.findViewById(R.id.ctrlAmtBtn);
        final Button add = (Button) ctrlAmtBtn.findViewById(R.id.add);
        final Button subtract = (Button) ctrlAmtBtn.findViewById(R.id.subtract);
        final Button delete = (Button) ctrlAmtBtn.findViewById(R.id.delete);

        if(dbHandler.getCountIngredient(singleFoodItem) == 0){
            subtract.setEnabled(false);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        subtract.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(dbHandler.getCountIngredient(singleFoodItem) > 0) {
                    dbHandler.subtractIngredient(singleFoodItem);
                    int amt = dbHandler.getCountIngredient(singleFoodItem);
                    name.setText(singleFoodItem + " (" + amt + ")");

                    if(dbHandler.getCountIngredient(singleFoodItem) == 0){
                    subtract.setEnabled(false);
                        name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }

                    Context context = getContext();
                    CharSequence text = Integer.toString(dbHandler.getCountIngredient(singleFoodItem));
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();


                }
            }
        });

        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dbHandler.addIngredient(singleFoodItem);
                int amt = dbHandler.getCountIngredient(singleFoodItem);
                name.setText(singleFoodItem + " (" + amt + ")");

                if(dbHandler.getCountIngredient(singleFoodItem) > 0){
                    subtract.setEnabled(true);
                    name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                Context context = getContext();
                CharSequence text = Integer.toString(dbHandler.getCountIngredient(singleFoodItem));
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                RelativeLayout parent = (RelativeLayout) view.getParent().getParent();
                final TextView recipeName = (TextView) parent.findViewById(R.id.ingredientName);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dbHandler.deleteIngredient(recipeName.getTag().toString());
                                ((Activity) getContext()).finish();
                                ((Activity) getContext()).startActivity(new Intent(getContext(), GroceriesActivity.class));

                                Context context = getContext().getApplicationContext();
                                CharSequence text = "Deleted " + recipeName.getTag().toString();
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete " + recipeName.getText().toString());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    name.setBackgroundColor(Color.parseColor("#ededed"));
                    x1 = motionEvent.getX();
                    return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    float currentPos = motionEvent.getX();
                    float deltaX = x1 - currentPos;
                    float transX = arrow.getTranslationX();

                    transX -= deltaX;

                    if (ctrlAmtBtn.getVisibility() == View.GONE) {
                        if (transX < -150) {
                            transX = -150;
                        }
                        if (transX > 0) {
                            transX = 0;
                        }

                        if(transX < -75){
                            RelativeLayout relLayout = (RelativeLayout) v.getRootView().findViewById(R.id.relLayout);
                            relLayout.requestDisallowInterceptTouchEvent(true);
                        } else {
                            RelativeLayout relLayout = (RelativeLayout) v.getRootView().findViewById(R.id.relLayout);
                            relLayout.requestDisallowInterceptTouchEvent(false);
                        }
                    }

                    if (ctrlAmtBtn.getVisibility() == View.VISIBLE) {
                        if (transX > 150) {
                            transX = 150;
                        }
                        if (transX < 0) {
                            transX = 0;
                        }

                        if(transX > 75){
                            RelativeLayout relLayout = (RelativeLayout) v.getRootView().findViewById(R.id.relLayout);
                            relLayout.requestDisallowInterceptTouchEvent(true);
                        }else {
                            RelativeLayout relLayout = (RelativeLayout) v.getRootView().findViewById(R.id.relLayout);
                            relLayout.requestDisallowInterceptTouchEvent(false);
                        }
                    }

                    arrow.setTranslationX(transX);
                    name.setTranslationX(transX);
                    return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    name.setBackgroundColor(Color.parseColor("#fffafafa"));
                    x2 = motionEvent.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (deltaX > 0) {
                            ctrlAmtBtn.setVisibility(View.GONE);
                            arrow.setImageResource(R.drawable.leftarrow);
                            arrow.setTranslationX(0);
                            name.setTranslationX(0);
                        } else {
                            ctrlAmtBtn.setVisibility(View.VISIBLE);
                            arrow.setImageResource(R.drawable.rightarrow);
                            //noinspection ResourceType
                            arrow.setTranslationX(0);
                            //noinspection ResourceType
                            name.setTranslationX(0);
                        }
                    }
                    name.setBackgroundColor(Color.parseColor("#fffafafa"));

                    if (ctrlAmtBtn.getVisibility() == View.GONE) {
                        if (arrow.getTranslationX() < 0) {
                            arrow.setTranslationX(0);
                            name.setTranslationX(0);
                        }
                    }

                    if (ctrlAmtBtn.getVisibility() == View.VISIBLE) {
                        if (arrow.getTranslationX() > -150) {
                            //noinspection ResourceType
                            arrow.setTranslationX(0);
                            //noinspection ResourceType
                            name.setTranslationX(0);
                        }
                    }
                    return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    x2 = motionEvent.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (deltaX > 0) {
                            ctrlAmtBtn.setVisibility(View.GONE);
                            arrow.setImageResource(R.drawable.leftarrow);
                            arrow.setTranslationX(0);
                            name.setTranslationX(0);
                        } else {
                            ctrlAmtBtn.setVisibility(View.VISIBLE);
                            arrow.setImageResource(R.drawable.rightarrow);
                            //noinspection ResourceType
                            arrow.setTranslationX(0);
                            //noinspection ResourceType
                            name.setTranslationX(0);
                        }
                    }
                    name.setBackgroundColor(Color.parseColor("#fffafafa"));

                    if (ctrlAmtBtn.getVisibility() == View.GONE) {
                        if (arrow.getTranslationX() < 0) {
                            arrow.setTranslationX(0);
                            name.setTranslationX(0);
                        }
                    }

                    if (ctrlAmtBtn.getVisibility() == View.VISIBLE) {
                        if (arrow.getTranslationX() > -150) {
                            //noinspection ResourceType
                            arrow.setTranslationX(0);
                            //noinspection ResourceType
                            name.setTranslationX(0);
                        }
                    }
                    return true;
                }

                return false;
            }
        });

        return customView;
    }
}
