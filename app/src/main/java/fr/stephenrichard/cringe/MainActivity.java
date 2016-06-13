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

import fr.stephenrichard.cringe.activity.CringeActivity;

import fr.stephenrichard.cringe.activity.ListActivity;
import fr.stephenrichard.cringe.activity.MapActivity;
import fr.stephenrichard.cringe.activity.ProfileActivity;
import fr.stephenrichard.cringe.adapter.ActivityAdapter;

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
                launchCringeCreate();
            }

        });

        profileIcon = (ImageView) findViewById(R.id.acces_profile);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfile();
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null)
        tabLayout.setupWithViewPager(viewPager);
    }

    protected void launchCringeCreate() {
        startActivity(new Intent(this, CringeActivity.class));
    }

    protected void launchProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void setupViewPager(ViewPager viewPager) {
        ActivityAdapter adapter = new ActivityAdapter(getSupportFragmentManager());
        adapter.addFragment(new CringeActivity(), "Cringe");
        adapter.addFragment(new ListActivity(), "List");
        adapter.addFragment(new MapActivity(), "Map");
        viewPager.setAdapter(adapter);
    }
}
