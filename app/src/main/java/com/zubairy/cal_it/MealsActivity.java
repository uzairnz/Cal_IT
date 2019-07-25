package com.zubairy.cal_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class MealsActivity extends AppCompatActivity {
    CalendarView calendarView;
    MyDBHandler dbHandler;
    String day;
    String dayLongName;
    Calendar calendar;
    ImageView arrow;
    Spinner breakfastSpinner;
    Spinner lunchSpinner;
    Spinner dinnerSpinner;
    private boolean editMode;
    private TextView b;
    private TextView l;
    private TextView d;
    private ArrayList<String> list;
    private ArrayList<String> selectedMeals;
    private ArrayList<Integer> hidingItemIndex;
    int previous, current;
    int total;
    private TextView totalCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        editMode = false;

        dbHandler = new MyDBHandler(this, null, null, 1);
        final Button date = (Button) findViewById(R.id.date);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        arrow = (ImageView) findViewById(R.id.arrow);
        breakfastSpinner = (Spinner) findViewById(R.id.breakfastSpinner);
        lunchSpinner = (Spinner) findViewById(R.id.lunchSpinner);
        dinnerSpinner = (Spinner) findViewById(R.id.dinnerSpinner);
        //totalCalories = (TextView) findViewById(R.id.totalCalories);
        b = (TextView) findViewById(R.id.breakfastText);
        l = (TextView) findViewById(R.id.lunchText);
        d = (TextView) findViewById(R.id.dinnerText);

        calendar = Calendar.getInstance();

        dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cYear = calendar.get(Calendar.YEAR);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                day = (i1 + 1) + "/" + i2 + "/" + i;
                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                switch (dayOfWeek) {
                    case Calendar.SUNDAY:
                        dayLongName = "Sunday";
                        break;

                    case Calendar.MONDAY:
                        dayLongName = "Monday";
                        break;

                    case Calendar.TUESDAY:
                        dayLongName = "Tuesday";
                        break;

                    case Calendar.WEDNESDAY:
                        dayLongName = "Wednesday";
                        break;

                    case Calendar.THURSDAY:
                        dayLongName = "Thursday";
                        break;

                    case Calendar.FRIDAY:
                        dayLongName = "Friday";
                        break;

                    case Calendar.SATURDAY:
                        dayLongName = "Saturday";
                        break;

                }

                date.setText(dayLongName + "" + " (" + day + ")");

                if (dbHandler.dateExists(date.getText().toString())) {
                    b.setText(dbHandler.getMeal(date.getText().toString(), "breakfast"));
                    l.setText(dbHandler.getMeal(date.getText().toString(), "lunch"));
                    d.setText(dbHandler.getMeal(date.getText().toString(), "dinner"));


                    breakfastSpinner.setSelection(list.indexOf(dbHandler.getMeal(date.getText().toString(), "breakfast")));
                    lunchSpinner.setSelection(list.indexOf(dbHandler.getMeal(date.getText().toString(), "lunch")));
                    dinnerSpinner.setSelection(list.indexOf(dbHandler.getMeal(date.getText().toString(), "dinner")));

                } else {
                    b.setText("Eating out");
                    l.setText("Eating out");
                    d.setText("Eating out");

                    breakfastSpinner.setSelection(0);
                    lunchSpinner.setSelection(0);
                    dinnerSpinner.setSelection(0);

                }

            }

        });

        date.setText(dayLongName + "" + " (" + cMonth + "/" + cDay + "/" + cYear + ")");
        calendarView.setVisibility(View.GONE);


        list = dbHandler.getMeals();
        list.add(0, "Eating out");

        //for(int i = 0; i < list.size(); i++){
        //    list2.add(list.get(i));
        //}
/*
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
*/

        hidingItemIndex = dbHandler.getHidden();
        //hidingItemIndex.add(0);
        selectedMeals = dbHandler.getMealPlanMeals();

        final CustomAdapter dataAdapter = new CustomAdapter(this,
                android.R.layout.simple_spinner_item, list, hidingItemIndex);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        breakfastSpinner.setAdapter(dataAdapter);
        lunchSpinner.setAdapter(dataAdapter);
        dinnerSpinner.setAdapter(dataAdapter);


        breakfastSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isLoaded = false;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long lon) {
                dbHandler.addToMealPlan(breakfastSpinner.getSelectedItem().toString(), date.getText().toString(), "breakfast");
                selectedMeals = dbHandler.getMealPlanMeals();
                b.setText(dbHandler.getMeal(date.getText().toString(), "breakfast"));

                previous = current;
                if (isLoaded) {
                    //hidingItemIndex.remove(previous);
                }
                current = i;

                if (!breakfastSpinner.getSelectedItem().toString().equals("Eating out")) {
                    dbHandler.addToHide(i);
                    hidingItemIndex.add(i);

                    CustomAdapter dataAdapter = new CustomAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, list, hidingItemIndex);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    lunchSpinner.setAdapter(dataAdapter);
                    dinnerSpinner.setAdapter(dataAdapter);
                    lunchSpinner.setSelection(list.indexOf(l.getText().toString()));
                    dinnerSpinner.setSelection(list.indexOf(d.getText().toString()));

                }
                isLoaded = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lunchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long lo) {
                dbHandler.addToMealPlan(lunchSpinner.getSelectedItem().toString(), date.getText().toString(), "lunch");
                l.setText(dbHandler.getMeal(date.getText().toString(), "lunch"));

                if (!lunchSpinner.getSelectedItem().toString().equals("Eating out")) {
                    dbHandler.addToHide(i);
                    hidingItemIndex.add(i);

                    CustomAdapter dataAdapter = new CustomAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, list, hidingItemIndex);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    breakfastSpinner.setAdapter(dataAdapter);
                    dinnerSpinner.setAdapter(dataAdapter);

                    breakfastSpinner.setSelection(list.indexOf(b.getText().toString()));
                    dinnerSpinner.setSelection(list.indexOf(d.getText().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dinnerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long lon) {
                dbHandler.addToMealPlan(dinnerSpinner.getSelectedItem().toString(), date.getText().toString(), "dinner");
                d.setText(dbHandler.getMeal(date.getText().toString(), "dinner"));

                if (!dinnerSpinner.getSelectedItem().toString().equals("Eating out")) {
                    dbHandler.addToHide(i);
                    hidingItemIndex.add(i);

                    CustomAdapter dataAdapter = new CustomAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, list, hidingItemIndex);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    breakfastSpinner.setAdapter(dataAdapter);
                    lunchSpinner.setAdapter(dataAdapter);

                    breakfastSpinner.setSelection(list.indexOf(b.getText().toString()));
                    lunchSpinner.setSelection(list.indexOf(l.getText().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (dbHandler.dateExists(date.getText().toString())) {
            b.setText(dbHandler.getMeal(date.getText().toString(), "breakfast"));
            l.setText(dbHandler.getMeal(date.getText().toString(), "lunch"));
            d.setText(dbHandler.getMeal(date.getText().toString(), "dinner"));

            breakfastSpinner.setSelection(list.indexOf(dbHandler.getMeal(date.getText().toString(), "breakfast")));
            lunchSpinner.setSelection(list.indexOf(dbHandler.getMeal(date.getText().toString(), "lunch")));
            dinnerSpinner.setSelection(list.indexOf(dbHandler.getMeal(date.getText().toString(), "dinner")));
        }
    }

    static ArrayList<Integer> indexOfAll(Object obj, ArrayList list) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++)
            if (obj.equals(list.get(i)))
                indexList.add(i);
        return indexList;
    }

    public void showCalendar(View view) {
        if (!calendarView.isShown()) {
            calendarView.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.uparrow);
        } else {
            calendarView.setVisibility(View.GONE);
            arrow.setImageResource(R.drawable.downarrow);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_meals, menu); //inflate our menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MealsActivity.this.finish();
                startActivity(new Intent(MealsActivity.this, MainActivity.class));
                break;

            case R.id.item_edit:
                if (editMode) {
                    editMode = false;
                    breakfastSpinner.setVisibility(View.INVISIBLE);
                    lunchSpinner.setVisibility(View.INVISIBLE);
                    dinnerSpinner.setVisibility(View.INVISIBLE);

                    b.setVisibility(View.VISIBLE);
                    l.setVisibility(View.VISIBLE);
                    d.setVisibility(View.VISIBLE);
                } else {
                    editMode = true;
                    breakfastSpinner.setVisibility(View.VISIBLE);
                    lunchSpinner.setVisibility(View.VISIBLE);
                    dinnerSpinner.setVisibility(View.VISIBLE);

                    b.setVisibility(View.INVISIBLE);
                    l.setVisibility(View.INVISIBLE);
                    d.setVisibility(View.INVISIBLE);
                }
                break;
        }
        return true;
    }
}
