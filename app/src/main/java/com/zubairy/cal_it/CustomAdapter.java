package com.zubairy.cal_it;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private ArrayList<Integer> hidingItemIndex;

    public CustomAdapter(Context context, int textViewResourceId, String[] objects, ArrayList<Integer> hidingItemIndex) {
        super(context, textViewResourceId, objects);
        this.hidingItemIndex = hidingItemIndex;
    }

    public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects, ArrayList<Integer> hidingItemIndex) {
        super(context, textViewResourceId, objects);
        this.hidingItemIndex = hidingItemIndex;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        boolean contains = false;

        if (!hidingItemIndex.isEmpty()) {
            for (int i = 0; i < hidingItemIndex.size(); i++) {
                if (position == hidingItemIndex.get(i)) {
                    contains = true;
                }
            }

            if (contains) {
                TextView tv = new TextView(getContext());
                tv.setVisibility(View.GONE);
                tv.setHeight(0);
                v = tv;
            } else {
                v = super.getDropDownView(position, null, parent);
            }
        } else {
            v = super.getDropDownView(position, null, parent);
        }

        return v;
    }
}