package com.zubairy.cal_it;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Rule on 9/27/2017.
 */

public class Dish {
    private int id;
    private String name = "";
    private String directions = "";
    private ArrayList<String> ingredients = new ArrayList<String>();
    private Bitmap image = null;
    private int calories = 0;

    public void setId(int num) { this.id = num; }

    public void setName(String str){
        this.name = str;
    }

    public void setDirections(String dir){
        this.directions = dir;
    }

    public void addIngredient(String ing){
        this.ingredients.add(ing);
    }

    public void setImage(Bitmap bit){
        this.image = bit;
    }

    public void setCalories(int num){
        this.calories = num;
    }

    public int getId() { return this.id; }

    public String getName(){
        return this.name;
    }

    public String getDirections(){
        return this.directions;
    }

    public ArrayList<String> getIngredients(){
        return this.ingredients;
    }

    public Bitmap getImage(){
        return this.image;
    }

    public int getCalories() {
        return this.calories;
    }
}
