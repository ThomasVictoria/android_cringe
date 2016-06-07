package fr.stephenrichard.cringe;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import fr.stephenrichard.cringe.model.Cringe;

public class MainActivity extends AppCompatActivity{

    private DatabaseReference mDatabase;

    private Integer level;

    private String desc;

    private Boolean isPrivate;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_cringe = (Button) findViewById(R.id.Button_create_cringe);
        Switch switchPrivate = (Switch) findViewById(R.id.isPrivate);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        System.out.println(locationManager);

        if(switchPrivate != null)
            switchPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        isPrivate = true;
                    } else {
                        isPrivate = false;
                    }
                }
            });

        if(button_cringe != null)
            button_cringe.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    EditText editDesc = (EditText) findViewById(R.id.cringe_desc);

                    if (editDesc != null)
                        desc = editDesc.getText().toString();
                    else
                        desc = "";

                    createCringe(isPrivate, user.getDisplayName(), level, desc, user.getUid(), 0.22222222233222, 2.2222222556);
                }
            });
    }

    public void createCringe(Boolean isPrivate, String author, Integer level, String desc, String uid, Double longitude, Double latitude){

        Firebase.setAndroidContext(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String key = mDatabase.child("cringes").push().getKey();
        Cringe cringe = new Cringe(isPrivate, author, level, desc, uid, longitude, latitude);
        Map<String, Object> cringeValues = cringe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cringes/" + key, cringeValues);

        mDatabase.updateChildren(childUpdates);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
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