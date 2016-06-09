package fr.stephenrichard.cringe.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;

public class CringeActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_CHECK_SETTINGS = 0;
    private DatabaseReference mDatabase;

    private Integer level;

    private String desc;

    private Boolean isPrivate;

    private double userLat;
    private double userLng;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private LocationManager mLocationManager;
    private String provider;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


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

                    EditText editDesc = (EditText) findViewById(R.id.cringe_desc);

                    if (editDesc != null)
                        desc = editDesc.getText().toString();
                    else
                        desc = "";

                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());

                    createCringe(isPrivate, cal.getTime().toString(), user.getDisplayName(), level, desc, user.getUid(), userLat, userLng);
                }
            });

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Get the location manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Location location = mLocationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {

            }
            return;
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        mLocationManager.requestLocationUpdates(provider, 400, 1, this);

    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLat = (double) (location.getLatitude());
        userLng = (double) (location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://fr.stephenrichard.cringe/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://fr.stephenrichard.cringe/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}