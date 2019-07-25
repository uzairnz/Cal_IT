package com.zubairy.cal_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import android.database.MatrixCursor;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "dishesdb";

    public static final String TABLE_DISHES = "dishes";
    public static final String TABLE_GROCERIES = "groceries";
    public static final String TABLE_MEALS = "meals";
    public static final String TABLE_HIDE = "hide";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "dishName";
    public static final String COLUMN_DIRECTIONS = "dishDirections";
    public static final String COLUMN_INGREDIENTS = "dishIngredients";
    public static final String COLUMN_IMAGE = "dishImage";
    public static final String COLUMN_MEALS = "dishMeals";
    public static final String COLUMN_CALORIES = "dishCalories";

    public static final String GRO_NAME = "groName";
    public static final String GRO_AMOUNT = "groAmount";

    public static final String MEALS_DATE = "mealsDate";
    public static final String MEALS_BREAKFAST = "mealsBreakfast";
    public static final String MEALS_LUNCH = "mealsLunch";
    public static final String MEALS_DINNER = "mealsDinner";

    public static final String HIDE_POSITION = "hidePosition";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_DISHES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DIRECTIONS + " TEXT," +
                COLUMN_INGREDIENTS + " TEXT," +
                COLUMN_IMAGE + " BLOB," +
                COLUMN_MEALS + " INTEGER," +
                COLUMN_CALORIES + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_GROCERIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GRO_NAME + " TEXT," +
                GRO_AMOUNT + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(query2);

        String query3 = "CREATE TABLE " + TABLE_MEALS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MEALS_DATE + " TEXT," +
                MEALS_BREAKFAST + " TEXT," +
                MEALS_LUNCH + " TEXT," +
                MEALS_DINNER + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(query3);

        String query4 = "CREATE TABLE " + TABLE_HIDE + "(" +
                HIDE_POSITION + " INTEGER PRIMARY KEY " +
                ");";
        sqLiteDatabase.execSQL(query4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DISHES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERIES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HIDE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(sqLiteDatabase);
    }

    public void addToGroceries(Dish dish) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 0; i < dish.getIngredients().size(); i++) {
            if (!ingredientExists(dish.getIngredients().get(i))) {
                values.put(GRO_NAME, dish.getIngredients().get(i));
                values.put(GRO_AMOUNT, 1);
                db.insert(TABLE_GROCERIES, null, values);
                values.clear();
            } else {
                values.put(GRO_AMOUNT, getGroceriesAmt(dish.getIngredients().get(i)) + 1);
                db.update(TABLE_GROCERIES, values, "_id=" + this.getIdGroceries(dish.getIngredients().get(i)), null);
                values.clear();
            }
        }
        db.close();
    }

    public void addToHide(ArrayList<Integer> items) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 0; i < items.size(); i++) {
            values.put(HIDE_POSITION, items.get(i));
            db.insert(TABLE_HIDE, null, values);
        }
        db.close();
    }

    public void addToHide(int item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(HIDE_POSITION, item);
        if(hideExists(item)) {
            db.insert(TABLE_HIDE, null, values);
        } else {
            //db.update(TABLE_HIDE, values, "hidePosition" + item, null);
        }

        db.close();
    }

    public void addToMealPlan(String dish, String date, String mealTime) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MEALS_DATE, date);
        if (mealTime.equals("breakfast")) {
            values.put(MEALS_BREAKFAST, dish);
        }
        if (mealTime.equals("lunch")) {
            values.put(MEALS_LUNCH, dish);
        }
        if (mealTime.equals("dinner")) {
            values.put(MEALS_DINNER, dish);
        }

        if (dateExists(date)) {
            db.update(TABLE_MEALS, values, "_id=" + this.getMealPlanId(date), null);
        } else {
            db.insert(TABLE_MEALS, null, values);
        }

        db.close();
    }

    public void addToMeal(String dish) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE " + COLUMN_NAME + "=\"" + dish + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return;
        } else {
            int amt = c.getInt(c.getColumnIndex("dishMeals"));
            if (amt == 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MEALS, 1);
                db.update(TABLE_DISHES, values, "_id=" + this.getId(dish), null);
            } else if (amt > 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_MEALS, this.getMealsAmt(dish) + 1);
                db.update(TABLE_DISHES, values, "_id=" + this.getId(dish), null);
            }

            c.close();
        }
    }

    public void subtractIngredient(String ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (getGroceriesAmt(ingredient) == 0) {

        } else {
            values.put(GRO_AMOUNT, getGroceriesAmt(ingredient) - 1);
            db.update(TABLE_GROCERIES, values, "_id=" + this.getIdGroceries(ingredient), null);
        }
        db.close();
    }

    public void addIngredient(String ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GRO_NAME, ingredient);
        values.put(GRO_AMOUNT, getGroceriesAmt(ingredient) + 1);

        if (ingredientExists(ingredient)) {
            db.update(TABLE_GROCERIES, values, "_id=" + this.getIdGroceries(ingredient), null);
        } else {
            db.insert(TABLE_GROCERIES, null, values);
        }

        db.close();
    }

    public void addDish(Dish dish) throws IOException {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, dish.getName());
        values.put(COLUMN_DIRECTIONS, dish.getDirections());


        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < dish.getIngredients().size(); i++) {
            jsonArray.put(dish.getIngredients().get(i));
        }

        values.put(COLUMN_INGREDIENTS, jsonArray.toString());

        Bitmap bmp = dish.getImage();
        DbBitmapUtility bmpUtility = new DbBitmapUtility();

        values.put(COLUMN_IMAGE, bmpUtility.getBytes(bmp));
        values.put(COLUMN_CALORIES, dish.getCalories());

        if (nameExists(dish.getName())) {
            values.put(COLUMN_MEALS, this.getMealsAmt(dish.getName()));
            db.update(TABLE_DISHES, values, "_id=" + this.getId(dish.getName()), null);
        } else {
            values.put(COLUMN_MEALS, 0);
            db.insert(TABLE_DISHES, null, values);
        }

        db.close();
    }

    public void addDish(Dish dish, int id) throws IOException {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, dish.getName());
        values.put(COLUMN_DIRECTIONS, dish.getDirections());


        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < dish.getIngredients().size(); i++) {
            jsonArray.put(dish.getIngredients().get(i));
        }

        values.put(COLUMN_INGREDIENTS, jsonArray.toString());

        Bitmap bmp = dish.getImage();
        DbBitmapUtility bmpUtility = new DbBitmapUtility();

        values.put(COLUMN_IMAGE, bmpUtility.getBytes(bmp));

        db.update(TABLE_DISHES, values, "_id=" + id, null);
        db.close();
    }

    public void deleteDish(String dishName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_DISHES + " WHERE " + COLUMN_NAME + "=\"" + dishName + "\";");
    }

    public void deleteIngredient(String ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GROCERIES + " WHERE " + GRO_NAME + "=\"" + ingredient + "\";");
    }

    public boolean dateExists(String date) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_MEALS + " WHERE " + MEALS_DATE + "=\"" + date + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean hideExists(int hide) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_HIDE + " WHERE " + HIDE_POSITION + "=\"" + hide + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean nameExists(String dishName) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE " + COLUMN_NAME + "=\"" + dishName + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean ingredientExists(String ingredientName) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_GROCERIES + " WHERE " + GRO_NAME + "=\"" + ingredientName + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getId(String dishName) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE " + COLUMN_NAME + "=\"" + dishName + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return -1;
        } else {
            int id = c.getInt(c.getColumnIndex("_id"));
            c.close();
            return id;
        }
    }

    public int getMealPlanId(String date) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_MEALS + " WHERE " + MEALS_DATE + "=\"" + date + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return -1;
        } else {
            int id = c.getInt(c.getColumnIndex("_id"));
            c.close();
            return id;
        }
    }

    public int getGroceriesAmt(String ingredientName) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_GROCERIES + " WHERE " + GRO_NAME + "=\"" + ingredientName + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return -1;
        } else {
            int amt = c.getInt(c.getColumnIndex("groAmount"));
            c.close();
            return amt;
        }
    }

    public int getMealsAmt(String dish) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE " + COLUMN_NAME + "=\"" + dish + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return -1;
        } else {
            int amt = c.getInt(c.getColumnIndex("dishMeals"));
            c.close();
            return amt;
        }
    }

    public String getMeal(String date, String mealTime) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_MEALS + " WHERE " + MEALS_DATE + "=\"" + date + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return "";
        } else {
            String mealT = mealTime.substring(0, 1).toUpperCase() + mealTime.substring(1);
            String meal = c.getString(c.getColumnIndex("meals" + mealT));
            c.close();
            return meal;
        }
    }

    public ArrayList<String> getMeals() {
        ArrayList<String> meals = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE 1";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getInt(c.getColumnIndex("dishMeals")) > 0) {
                for (int i = 0; i < c.getInt(c.getColumnIndex("dishMeals")); i++) {
                    meals.add(c.getString(c.getColumnIndex("dishName")));
                }
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        return meals;
    }

    public ArrayList<String> getAllIngredients() throws JSONException {
        ArrayList<String> ingredients = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE 1";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("dishIngredients")) != null) {
                JSONArray jsonArray = new JSONArray(c.getString(c.getColumnIndex("dishIngredients")));
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (!ingredients.contains(jsonArray.get(i).toString())) {
                        ingredients.add(jsonArray.get(i).toString());
                    }
                }
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        return ingredients;
    }

    public ArrayList<Integer> getHidden() {
        ArrayList<Integer> hidden = new ArrayList<Integer>();
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_HIDE + " WHERE 1";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            hidden.add(c.getInt(0));
            c.moveToNext();
        }
        db.close();
        c.close();
        return hidden;
    }

    public ArrayList<String> getMealPlanMeals() {
        ArrayList<String> meals = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_MEALS + " WHERE 1";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("mealsBreakfast")) != null && !c.getString(c.getColumnIndex("mealsBreakfast")).equals("Eating out")) {
                meals.add(c.getString(c.getColumnIndex("mealsBreakfast")));
            }
            if (c.getString(c.getColumnIndex("mealsLunch")) != null && !c.getString(c.getColumnIndex("mealsLunch")).equals("Eating out")) {
                meals.add(c.getString(c.getColumnIndex("mealsLunch")));
            }
            if (c.getString(c.getColumnIndex("mealsDinner")) != null && !c.getString(c.getColumnIndex("mealsDinner")).equals("Eating out")) {
                meals.add(c.getString(c.getColumnIndex("mealsDinner")));
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        return meals;
    }

    public int getIdGroceries(String ingredientName) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_GROCERIES + " WHERE " + GRO_NAME + "=\"" + ingredientName + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return -1;
        } else {
            int id = c.getInt(c.getColumnIndex("_id"));
            c.close();
            return id;
        }
    }

    public String getName(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE " + COLUMN_ID + "=\"" + id + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("dishName"));
        db.close();
        return name;
    }

    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        String Query = "DELETE FROM sqlite_sequence WHERE NAME='" + TABLE_DISHES + "';";
        db.delete(TABLE_DISHES, null, null);
        db.execSQL(Query);
        db.close();
    }

    public void removeAllIngredients() {
        SQLiteDatabase db = getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        String Query = "DELETE FROM sqlite_sequence WHERE NAME='" + TABLE_GROCERIES + "';";
        db.delete(TABLE_GROCERIES, null, null);
        db.execSQL(Query);
        db.close();
    }

    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DISHES + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("dishName")) != null) {
                dbString += "ID: " + Integer.toString(c.getInt(c.getColumnIndex("_id")));
                dbString += "\n";
                dbString += "Name: " + c.getString(c.getColumnIndex("dishName"));
                dbString += "\n";
                dbString += "Directions: " + c.getString(c.getColumnIndex("dishDirections"));
                dbString += "\n";
                dbString += "Ingredients: " + c.getString(c.getColumnIndex("dishIngredients"));
                dbString += "\n";
                dbString += "Total Recipes: " + Integer.toString(getCountRecipes());
                dbString += "\n";
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        return dbString;
    }

    public ArrayList<String> getListNames() {
        ArrayList<String> names = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DISHES + " WHERE 1;";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        //names.add(c.getString(c.getColumnIndex("dishName")));

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("_id")) != null) {
                names.add(c.getString(c.getColumnIndex("dishName")));
            }
            c.moveToNext();
        }

        db.close();
        c.close();
        return names;
    }

    public ArrayList<String> getListIngredients() {
        ArrayList<String> names = new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GROCERIES + " WHERE 1;";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        //names.add(c.getString(c.getColumnIndex("dishName")));

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("_id")) != null) {
                names.add(c.getString(c.getColumnIndex("groName")));
            }
            c.moveToNext();
        }

        db.close();
        c.close();
        return names;
    }

    public int getCountIngredient(String ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_GROCERIES + " WHERE " + GRO_NAME + "=\"" + ingredient + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        int amt = c.getInt(c.getColumnIndex("groAmount"));
        db.close();
        c.close();
        return amt;
    }

    public int getCalories(String dish) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DISHES + " WHERE " + COLUMN_CALORIES + "=\"" + dish + "\";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        int amt = c.getInt(c.getColumnIndex("dishCalories"));
        db.close();
        c.close();
        return amt;
    }

    public int getCountRecipes() {
        SQLiteDatabase db = getWritableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_DISHES);

        return numRows;
    }

    public Dish dbToObject(String name) throws JSONException {
        DbBitmapUtility bmpUtility = new DbBitmapUtility();
        Dish newDish = new Dish();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DISHES + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("dishName")).equals(name)) {
                newDish.setId(c.getInt(c.getColumnIndex("dishId")));
                newDish.setName(c.getString(c.getColumnIndex("dishName")));
                newDish.setDirections(c.getString(c.getColumnIndex("dishDirections")));
                newDish.setCalories(c.getInt(c.getColumnIndex("dishCalories")));

                JSONArray jsonArray = new JSONArray(c.getString(c.getColumnIndex("dishIngredients")));
                for (int i = 0; i < jsonArray.length(); i++) {
                    newDish.addIngredient(jsonArray.get(i).toString());
                }

                newDish.setImage(bmpUtility.getImage(c.getBlob(c.getColumnIndex("dishImage"))));
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        return newDish;
    }

    public Dish dbToObject(int id) throws JSONException {
        DbBitmapUtility bmpUtility = new DbBitmapUtility();
        Dish newDish = new Dish();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DISHES + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getInt(c.getColumnIndex("_id")) == id) {
                newDish.setId(c.getInt(c.getColumnIndex("_id")));
                newDish.setName(c.getString(c.getColumnIndex("dishName")));
                newDish.setDirections(c.getString(c.getColumnIndex("dishDirections")));
                newDish.setCalories(c.getInt(c.getColumnIndex("dishCalories")));

                JSONArray jsonArray = new JSONArray(c.getString(c.getColumnIndex("dishIngredients")));
                for (int i = 0; i < jsonArray.length(); i++) {
                    newDish.addIngredient(jsonArray.get(i).toString());
                }

                newDish.setImage(bmpUtility.getImage(c.getBlob(c.getColumnIndex("dishImage"))));
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        return newDish;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}
