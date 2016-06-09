package fr.stephenrichard.cringe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.helper.LocationHelper;
import fr.stephenrichard.cringe.model.Cringe;

public class CringeActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 0;
    private DatabaseReference mDatabase;

    private Integer level;

    private String desc;

    private Boolean isPrivate;

    private double userLat;
    private double userLng;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    LocationResult locationResult;
    LocationHelper locationHelper;
    LocationManager locationManager;

    CringeActivity cringeActivity;

    final String TAG = "MainActivity.java";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cringe);

        Button button_cringe = (Button) findViewById(R.id.Button_create_cringe);
        Switch switchPrivate = (Switch) findViewById(R.id.isPrivate);

        if (switchPrivate != null)
            switchPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isPrivate = true;
                    } else {
                        isPrivate = false;
                    }
                }
            });

        if (button_cringe != null)
            button_cringe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cringeActivity.locationHelper.getLocation(cringeActivity, locationHelper.locationResult);

                    EditText editDesc = (EditText) findViewById(R.id.cringe_desc);

                    if (editDesc != null)
                        desc = editDesc.getText().toString();
                    else
                        desc = "";

                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());

                    createCringe(isPrivate, cal.getTime().toString(), user.getDisplayName(), level, desc, user.getUid(), userLat, userLng);
                }
            });

        // initialize our useful class,
        this.locationHelper = new LocationHelper();
    }

    // prevent exiting the app using back pressed
    // so getting user location can run in the background
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(CringeActivity.this)
                .setTitle("User Location App")
                .setMessage("This will end the app. Use the home button instead.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();

    }

    public void createCringe(Boolean isPrivate, String created_at, String author, Integer level, String desc, String uid, Double longitude, Double latitude) {

        Firebase.setAndroidContext(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String key = mDatabase.child("cringes").push().getKey();

        Toast.makeText(this, "Here is lat"+userLat, Toast.LENGTH_SHORT).show();

        Cringe cringe = new Cringe(isPrivate, created_at, author, level, desc, uid, longitude, latitude);
        Map<String, Object> cringeValues = cringe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cringes/" + key, cringeValues);

        mDatabase.updateChildren(childUpdates);
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.choice_1:
                if (checked)
                    level = 1;
                break;
            case R.id.choice_2:
                if (checked)
                    level = 2;
                break;
            case R.id.choice_3:
                if (checked)
                    level = 3;
                break;
            case R.id.choice_4:
                if (checked)
                    level = 4;
                break;
            case R.id.choice_5:
                if (checked)
                    level = 5;
                break;
        }
    }


}