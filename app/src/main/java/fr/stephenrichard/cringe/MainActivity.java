package fr.stephenrichard.cringe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.onCreate();
        Firebase.setAndroidContext(this);

        Firebase myFirebaseRef = new Firebase("https://project-5907554604004073121.firebaseio.com/");

        myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

    }



}