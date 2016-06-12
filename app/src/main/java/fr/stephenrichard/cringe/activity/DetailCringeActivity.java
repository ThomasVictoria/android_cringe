package fr.stephenrichard.cringe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.stephenrichard.cringe.R;

public class DetailCringeActivity extends AppCompatActivity {

    public static final String EXTRA_POST_KEY = "post_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cringe);
    }
}
