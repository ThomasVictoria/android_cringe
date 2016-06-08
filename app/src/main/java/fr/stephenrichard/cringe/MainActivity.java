package fr.stephenrichard.cringe;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import fr.stephenrichard.cringe.model.Cringe;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CHECK_SETTINGS = 0;
    private DatabaseReference mDatabase;

    private Integer level;

    private String desc;

    private Boolean isPrivate;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    public static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    // Calendar c = Calendar.getInstance().getTime();

                    createCringe(isPrivate, cal.getTime().toLocaleString(), user.getDisplayName(), level, desc, user.getUid(), 0.22222222233222, 2.2222222556);
                }
            });

        mLatitudeTextView = (TextView) findViewById((R.id.user_lat));
        mLongitudeTextView = (TextView) findViewById((R.id.user_lng));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void createCringe(Boolean isPrivate, String created_at, String author, Integer level, String desc, String uid, Double longitude, Double latitude) {

        Firebase.setAndroidContext(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String key = mDatabase.child("cringes").push().getKey();
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        /*
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        */

        if (mLocation != null) {
            Toast.makeText(this, String.valueOf(mLocation.getLatitude()), Toast.LENGTH_SHORT).show();

            mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {



            return;
        } else {
            Toast.makeText(this, "Can not find permissions", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}