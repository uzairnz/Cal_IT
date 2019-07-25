package com.zubairy.cal_it;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Rule on 9/29/2017.
 */

public class Meal {
    private HashMap<String, Integer> dishes = new HashMap<String, Integer>();
    private Context context;
    MyDBHandler dbHandler;

    public Meal(Context context){
        this.context = context;
        dbHandler = new MyDBHandler(context, null, null, 1);
    }

    public void addDishToMeal(String dish){
        if(dishes.containsKey(dish)){
            this.dishes.put(dish, this.dishes.get(dish) + 1);
        } else {
            this.dishes.put(dish, 1);
        }
        dbHandler.addToMeal(dish);
    }

    public void removeDish(String dish){
        if(dishes.containsKey(dish)) {
            if(dishes.get(dish) == 1){
                dishes.remove(dish);
            } else {
                this.dishes.put(dish, this.dishes.get(dish) - 1);
            }
        }
    }

    public HashMap<String, Integer> getDishes(){
        return this.dishes;
    }

    public int getDishAmt(String name){

        return this.dbHandler.getMealsAmt(name);
    }
}
