package fr.stephenrichard.cringe;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import fr.stephenrichard.cringe.model.Cringe;

public class MainActivity extends AppCompatActivity {

    //FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    Firebase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            System.out.println("NOM : "+user.getDisplayName());

            createCringe(true, "Jean michel", 5, "Ouais la description", "0976875");

        } else {
            System.out.println("non");
        }
    }

    public void createCringe(Boolean isPrivate, String author, Integer level, String desc, String uid){

        /*
        String key = mDatabase.child("cringes").push().getKey();
        Cringe cringe = new Cringe(isPrivate, author, level, desc, uid);
        Map<String, Object> cringeValues = cringe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cringes/" + key, cringeValues);

        mDatabase.updateChildren(childUpdates);
        */

        Cringe cringe = new Cringe(isPrivate, author, level, desc, uid);

        mDatabase.child("cringes").child(uid).setValue(cringe);

    }

}