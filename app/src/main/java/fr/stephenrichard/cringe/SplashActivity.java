package fr.stephenrichard.cringe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fr.stephenrichard.cringe.activity.IntroActivity;

/**
 * Created by stephenrichard on 04/06/16.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        
        finish();
    }
}
