package fr.stephenrichard.cringe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.activity.CringeActivity;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCringeCreate();
            }
        });
    }

    protected void launchCringeCreate() {
        startActivity(new Intent(this, CringeActivity.class));
    }


}
