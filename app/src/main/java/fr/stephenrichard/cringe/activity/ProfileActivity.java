package fr.stephenrichard.cringe.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.stephenrichard.cringe.CircleTransform;
import fr.stephenrichard.cringe.MainActivity;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;
import fr.stephenrichard.cringe.viewholder.CringeViewHolder;

public class ProfileActivity extends AppCompatActivity {

    private Context mContext;

    private FirebaseDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Query queryCringes;

    private TextView profileName;
    private ImageView profilePicture;
    private ImageView arrowBack;
    private TextView cringesCount;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView mRecycler;

    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mRecyclerView = (RecyclerView) findViewById(R.id.profile_cringes_list);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mContext = ProfileActivity.this;

        mView = findViewById(R.id.item_cringe);

        profileName = (TextView) findViewById(R.id.profile_name);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        cringesCount = (TextView) findViewById(R.id.profile_cringe_count);
        profileName.setText(user.getDisplayName());
        Picasso
                .with(mContext)
                .load(user.getPhotoUrl())
                .transform(new CircleTransform())
                .into(profilePicture);

        queryCringes = FirebaseDatabase.getInstance().getReference("cringes").orderByChild("uid").equalTo(user.getUid());

        // specify an adapter
        mAdapter = new FirebaseRecyclerAdapter<Cringe, CringeViewHolder>(
                Cringe.class, R.layout.item_cringe, CringeViewHolder.class, queryCringes) {
            
            @Override
            protected void populateViewHolder(final CringeViewHolder viewHolder, final Cringe cringe, final int position) {
                final DatabaseReference cringeRef = getRef(position);

                viewHolder.setAuthorName(cringe.author);
                viewHolder.setDateCreationView(getTimeAgo(cringe.createdAt));
                viewHolder.setBodyView(cringe.desc);
                Picasso
                        .with(mContext)
                        .load(cringe.author_picture)
                        .transform(new CircleTransform())
                        .into(viewHolder.authorPicture);

                final String cringeKey = cringeRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(mContext, DetailCringeActivity.class);
                        intent.putExtra(DetailCringeActivity.EXTRA_POST_KEY, cringeKey);
                        startActivity(intent);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(mAdapter);

        FirebaseDatabase.getInstance().getReference("cringes").orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        cringesCount.setText(dataSnapshot.getChildrenCount()+" Cringes");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        arrowBack = (ImageView) findViewById(R.id.arrow_back);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "à l'instant";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "il y a une minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Il y a " + diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "il y a une heure";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Il y a " + diff / HOUR_MILLIS + " heures";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "hier";
        } else {
            return "Il y a " + diff / DAY_MILLIS + " jours";
        }
    }
}