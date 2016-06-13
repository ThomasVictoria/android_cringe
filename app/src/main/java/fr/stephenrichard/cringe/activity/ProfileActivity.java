package fr.stephenrichard.cringe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.stephenrichard.cringe.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        profileName = (TextView) findViewById(R.id.profile_name);
        profileName.setText(user.getDisplayName());
    }
}