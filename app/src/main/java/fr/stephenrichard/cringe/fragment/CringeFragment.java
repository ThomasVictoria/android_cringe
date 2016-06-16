package fr.stephenrichard.cringe.fragment;

import android.Manifest;
import android.app.Fragment;
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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import fr.stephenrichard.cringe.MainActivity;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;

public class CringeFragment extends android.support.v4.app.Fragment {

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
    private RadioGroup mRadioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.activity_cringe, container, false);

        // Initialise Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //

        mButton_cringe = (Button) rootView.findViewById(R.id.Button_create_cringe);
        mSwitchPrivate = (Switch) rootView.findViewById(R.id.isPrivate);
        mBodyTextField = (EditText) rootView.findViewById(R.id.cringe_desc);

        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);

        EditText mEditText = (EditText) rootView.findViewById(R.id.cringe_desc);
        mEditText.clearFocus();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MyLocationListener()
            );
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

        if(mRadioGroup != null) {
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // boolean checked = ((RadioButton) view).isChecked();

                    switch (checkedId) {
                        case R.id.choice_1:
                                level = 1;
                            break;
                        case R.id.choice_2:
                                level = 2;
                            break;
                        case R.id.choice_3:
                                level = 3;
                            break;
                        case R.id.choice_4:
                                level = 4;
                            break;
                        case R.id.choice_5:
                                level = 5;
                            break;
                    }
                }
            });
        }

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

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return rootView;
    }

    private Double[] showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            // Toast.makeText(CringeFragment.this, message, Toast.LENGTH_LONG).show();

            return new Double[]{mLocation.getLongitude(), mLocation.getLatitude()};

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
            // Toast.makeText(CringeFragment.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Toast.makeText(this, "Provider status changed",
//                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
//            Toast.makeText(this,
//                    "Provider disabled by the user. GPS turned off",
//                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
//            Toast.makeText(this,
//                    "Provider enabled by the user. GPS turned on",
//                    Toast.LENGTH_LONG).show();
        }
    }

    public void submitCringe() throws IOException {

        Double[] userLocation = showCurrentLocation();

        final String desc = mBodyTextField.getText().toString();
        final Long timestamp = System.currentTimeMillis();
        // final Date mDate = new Date();

        final String userId = user.getUid();
        final String userName = user.getDisplayName();
        final String userPicture = user.getPhotoUrl().toString();

        writeNewCringe(isPrivate, timestamp, userName, userPicture, level, desc, userId, userLocation[0], userLocation[1]);
    }

    private void writeNewCringe(Boolean isPrivate, Long createdAt, String author, String authorPicture, Integer level, String desc, String uid, Double longitude, Double latitude) {

        String key = mDatabase.child("cringes").push().getKey();

        Cringe cringe = new Cringe(isPrivate, createdAt, author, authorPicture, level, desc, uid, longitude, latitude);
        Map<String, Object> cringeValues = cringe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cringes/" + key, cringeValues);

        mDatabase.updateChildren(childUpdates);

        getFragmentManager().popBackStack();

        Toast.makeText(getActivity(),
                "Votre cringe a bien été publié",
                Toast.LENGTH_SHORT).show();

    }

}