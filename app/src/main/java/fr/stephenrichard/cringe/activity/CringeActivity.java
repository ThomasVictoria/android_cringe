package fr.stephenrichard.cringe.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import fr.stephenrichard.cringe.MainActivity;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;

public class CringeActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 0;

    private DatabaseReference mDatabase;

    private Integer level;

    private Boolean isPrivate = false;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    protected LocationManager locationManager;
    protected Location mLocation;

    private static final String TAG = "NewPostActivity";

    private Switch mSwitchPrivate;
    private Button mButton_cringe;
    private EditText mBodyTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cringe);

        // Initialise Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //

        mButton_cringe = (Button) findViewById(R.id.Button_create_cringe);
        mSwitchPrivate = (Switch) findViewById(R.id.isPrivate);
        mBodyTextField = (EditText) findViewById(R.id.cringe_desc);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MyLocationListener()
            );
            return;
        }

        if (mSwitchPrivate != null)
            mSwitchPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isPrivate = true;
                    } else {
                        isPrivate = false;
                    }
                }
            });

        if (mButton_cringe != null)
            mButton_cringe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        submitCringe();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private Double[] showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MyLocationListener()
            );
        }
        mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(mLocation != null) {

            String message = String.format(
                    "Current location \n Longitude: %1$s \n Latitude: %2$s",
                    mLocation.getLongitude(), mLocation.getLatitude()
            );
            // Toast.makeText(CringeActivity.this, message, Toast.LENGTH_LONG).show();

            return new Double[]{mLocation.getLatitude(), mLocation.getLongitude()};

        }
        return new Double[0];
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            // Toast.makeText(CringeActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(CringeActivity.this, "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(CringeActivity.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(CringeActivity.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void submitCringe() throws IOException {

        Double[] userLocation = showCurrentLocation();

        final String desc = mBodyTextField.getText().toString();
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final String userId = user.getUid();
        final String userName = user.getDisplayName();
        final String userPicture = user.getPhotoUrl().toString();

        writeNewCringe(isPrivate, cal.getTime().toString(), userName, userPicture, level, desc, userId, userLocation[0], userLocation[1]);
    }

    private void writeNewCringe(Boolean isPrivate, String created_at, String author, String authorPicture, Integer level, String desc, String uid, Double longitude, Double latitude) {

        String key = mDatabase.child("cringes").push().getKey();

        Cringe cringe = new Cringe(isPrivate, created_at, author, authorPicture, level, desc, uid, longitude, latitude);
        Map<String, Object> cringeValues = cringe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cringes/" + key, cringeValues);

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(CringeActivity.this,
                "Votre cringe a bien été publié",
                Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

}