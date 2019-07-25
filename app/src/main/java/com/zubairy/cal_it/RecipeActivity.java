package com.zubairy.cal_it;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by Rule on 9/29/2017.
 */

public class RecipeActivity extends AppCompatActivity {

    FragmentManager manager;
    Recipes recipeFrag;
    Recipes2 recipeFrag2;
    Button hideFrag;
    FrameLayout fragmentContainer;
    FrameLayout fragContainer;
    float mLastPosX;
    DisplayMetrics dm;
    Boolean rightDrawer;
    private GestureDetector gestureDetector;
    Resources r;
    float boxpx;
    float px;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        manager = getFragmentManager();
        recipeFrag = (Recipes) manager.findFragmentById(R.id.fragment);

        r = getResources();
        boxpx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 390, r.getDisplayMetrics());
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());

        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        fragContainer = (FrameLayout) fragmentContainer.findViewById(R.id.fragContainer);
        recipeFrag2 = (Recipes2) manager.findFragmentById(R.id.fragment2);
        //hideFrag = (Button) findViewById(R.id.buttonHide);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        rightDrawer = true;

        getSupportActionBar().setHomeButtonEnabled(true);

        ImageButton button = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.add(fragContainer.getId(), new Recipes(), "Recipes3");
        trans.commit();

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //fragmentContainer.setTranslationX((dm.widthPixels/2) - 80);


        //fragmentContainer.setX(dm.widthPixels - 80);
        //OvershootInterpolator interpolator = new OvershootInterpolator(1);
        //fragmentContainer.animate().setInterpolator(interpolator).x(-815).setDuration(500);

        ViewGroup.LayoutParams params = recipeFrag2.getView().getLayoutParams();
        params.width = (int) (dm.widthPixels - boxpx+px);
        recipeFrag2.getView().setLayoutParams(params);

        ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
        btn.setImageResource(R.drawable.rightarrow);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    slideFrag(view);
                    return true;
                } else {
                    switch ( motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mLastPosX = motionEvent.getX();
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            float currentPos = motionEvent.getX();
                            float deltaX = mLastPosX - currentPos;
                            float transX = fragmentContainer.getTranslationX();

                            transX -= deltaX;

                            ViewGroup.LayoutParams params = recipeFrag2.getView().getLayoutParams();
                            params.width = (int) Math.abs((fragmentContainer.getX()));
                            recipeFrag2.getView().setLayoutParams(params);

                            if(transX < 0){
                                transX = 0;

                                //fragmentContainer.setX(dm.widthPixels );

                                params.width = (int)(dm.widthPixels - boxpx + px);
                                recipeFrag2.getView().setLayoutParams(params);

                                ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
                                btn.setImageResource(R.drawable.rightarrow);
                                rightDrawer = true;
                            }

                            if(transX > boxpx -px*2){
                                transX = boxpx -px*2;

                                //fragmentContainer.setX(dm.widthPixels - px);

                                params.width = (int)(dm.widthPixels - px);
                                recipeFrag2.getView().setLayoutParams(params);

                                ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
                                btn.setImageResource(R.drawable.leftarrow);
                                rightDrawer = false;
                            }

                            fragmentContainer.setTranslationX(transX);
                            return true;


                        case MotionEvent.ACTION_UP:
                            ViewGroup.LayoutParams params2 = recipeFrag2.getView().getLayoutParams();
                            if(fragmentContainer.getTranslationX() < boxpx/2){
                                fragmentContainer.setTranslationX(0);
                                params2.width = (int)(dm.widthPixels - boxpx + px);
                                recipeFrag2.getView().setLayoutParams(params2);

                                ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
                                btn.setImageResource(R.drawable.rightarrow);
                                rightDrawer = true;
                            }

                            if(fragmentContainer.getTranslationX() > boxpx/2){
                                fragmentContainer.setTranslationX(boxpx -px*2);
                                params2.width = (int)(dm.widthPixels - px);
                                recipeFrag2.getView().setLayoutParams(params2);

                                ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
                                btn.setImageResource(R.drawable.leftarrow);
                                rightDrawer = false;
                            }
                            return true;


                    }
                }

                return false;
            }
        });


        //f1 = (FragmentRecipe) manager.findFragmentById(R.id.fragment);
        //f1.setCommunicator(this);
    }

    private class SingleTapConfirm extends SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu); //inflate our menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                RecipeActivity.this.finish();
                startActivity(new Intent(RecipeActivity.this, MainActivity.class));
                break;

            case R.id.item_delete:
                if(getRotation(getApplicationContext()).equals("landscape") || getRotation(getApplicationContext()).equals("reverse landscape")){
                    Recipes recipeFrag3 = (Recipes) manager.findFragmentByTag("Recipes3");
                    recipeFrag3.removeAll(recipeFrag3.parentLinearLayout);
                } else if(getRotation(getApplicationContext()).equals("portrait") || getRotation(getApplicationContext()).equals("reverse portrait")) {
                    recipeFrag.removeAll(recipeFrag.parentLinearLayout);
                }
                break;

            case R.id.item_add:
                if(getRotation(getApplicationContext()).equals("landscape") || getRotation(getApplicationContext()).equals("reverse landscape")){
                    Recipes recipeFrag3 = (Recipes) manager.findFragmentByTag("Recipes3");
                    recipeFrag3.startNewDish(recipeFrag3.parentLinearLayout);
                } else if(getRotation(getApplicationContext()).equals("portrait") || getRotation(getApplicationContext()).equals("reverse portrait")) {
                    recipeFrag.startNewDish(recipeFrag.parentLinearLayout);
                }
                break;

            case R.id.item_edit:
                if(getRotation(getApplicationContext()).equals("landscape") || getRotation(getApplicationContext()).equals("reverse landscape")){
                    Recipes recipeFrag3 = (Recipes) manager.findFragmentByTag("Recipes3");
                    recipeFrag3.editTools();
                } else if(getRotation(getApplicationContext()).equals("portrait") || getRotation(getApplicationContext()).equals("reverse portrait")) {
                    recipeFrag.editTools();
                }
                break;
        }
        return true;
    }

    public void slideFrag(View v) {


        if(rightDrawer){

            fragmentContainer.setTranslationX(dm.widthPixels/2-boxpx);

            OvershootInterpolator interpolator = new OvershootInterpolator(1);
            fragmentContainer.animate().setInterpolator(interpolator).x(dm.widthPixels-px).setDuration(500);

            ViewGroup.LayoutParams params = recipeFrag2.getView().getLayoutParams();

            params.width = dm.widthPixels - (int)(px);
            recipeFrag2.getView().setLayoutParams(params);

            ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
            btn.setImageResource(R.drawable.leftarrow);
            rightDrawer = false;
        } else {
            //fragmentContainer.setTranslationX(dm.widthPixels/2 - 80);
            OvershootInterpolator interpolator = new OvershootInterpolator(1);
            fragmentContainer.animate().setInterpolator(interpolator).translationX(0).setDuration(500);

            ViewGroup.LayoutParams params = recipeFrag2.getView().getLayoutParams();
            params.width = (int)(dm.widthPixels - boxpx + px);
            recipeFrag2.getView().setLayoutParams(params);

            ImageButton btn = (ImageButton) fragmentContainer.findViewById(R.id.imageButton);
            btn.setImageResource(R.drawable.rightarrow);

            /*
            Context context = getApplicationContext();
            CharSequence text = Float.toString(fragmentContainer.getX()) + "\n" + params.width + "\n" + dm.widthPixels;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            */
            rightDrawer = true;
        }
    }

    /*
    public void hideFrag(View v){
        showHideFragment(recipeFrag);
    }
    */

    public void showHideFragment(final Recipes fragment){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
         ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        if (fragment.isHidden()) {
            ft.show(fragment);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80,LinearLayout.LayoutParams.MATCH_PARENT);
            hideFrag.setLayoutParams(params);
            hideFrag.setText(">");
            Log.d("hidden","Show");
        } else {
            ft.hide(fragment);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160,LinearLayout.LayoutParams.MATCH_PARENT);
            hideFrag.setLayoutParams(params);
            hideFrag.setText("<");
            Log.d("Shown","Hide");
        }

        ft.commit();
    }

}
