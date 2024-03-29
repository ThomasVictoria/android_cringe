package fr.stephenrichard.cringe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import fr.stephenrichard.cringe.activity.ProfileActivity;
import fr.stephenrichard.cringe.adapter.ActivityAdapter;
import fr.stephenrichard.cringe.fragment.CringeFragment;
import fr.stephenrichard.cringe.fragment.ListFragment;
import fr.stephenrichard.cringe.fragment.MapFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FloatingActionButton fabButton;
    private ImageView profileIcon;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }

        });

        profileIcon = (ImageView) findViewById(R.id.acces_profile);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfile();
            }
        });
        
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null)
        tabLayout.setupWithViewPager(viewPager);
    }

    protected void launchProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void setupViewPager(ViewPager viewPager) {
        ActivityAdapter adapter = new ActivityAdapter(getSupportFragmentManager());
        adapter.addFragment(new CringeFragment(), "Cringe");
        adapter.addFragment(new ListFragment(), "List");
        adapter.addFragment(new MapFragment(), "Map");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0) {
                    fabButton.setVisibility(View.GONE);
                } else {
                    fabButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
