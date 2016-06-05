package fr.stephenrichard.cringe.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.stephenrichard.cringe.MainActivity;
import fr.stephenrichard.cringe.adapter.ViewPagerAdapter;
import fr.stephenrichard.cringe.manager.PrefManager;
import fr.stephenrichard.cringe.R;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private LinearLayout dotsLayout;
    private Button btnNext, btnSkip;
    private int[] layouts;
    private TextView[] dots;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking first time launch
        // If it's not the first time we launch, we don't display the intro slider
        prefManager = new PrefManager(this);
        if(!prefManager.isFirstTimeLaunch()) {
            launchLoginScreen();
            finish();
        }

        // Make notification bar transparent
        /*
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        */

        setContentView(R.layout.activity_intro);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layout_dots);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSkip = (Button) findViewById(R.id.btn_skip);

        layouts = new int[] {
                R.layout.welcome_activity_1,
                R.layout.welcome_activity_2,
                R.layout.welcome_activity_3
        };

        addBottomDots(0);

        changeStatusBarColor();

        viewPagerAdapter = new ViewPagerAdapter(this, layouts);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if(current < layouts.length) {

                    viewPager.setCurrentItem(current);
                } else {
                    launchLoginScreen();
                }
            }
        });

    }

    // Add small dots (depends on intro steps)
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for(int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.light_grey));
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0) {
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    // Call login screen
    private void launchLoginScreen() {
        prefManager.setIsFirstTimeLaunch(false);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if(position == layouts.length - 1) {
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    // Set notification bar transparent
    private void changeStatusBarColor() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
