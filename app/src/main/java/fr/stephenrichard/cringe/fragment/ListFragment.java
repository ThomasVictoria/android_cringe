package fr.stephenrichard.cringe.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.stephenrichard.cringe.CircleTransform;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.activity.DetailCringeActivity;
import fr.stephenrichard.cringe.model.Cringe;
import fr.stephenrichard.cringe.viewholder.CringeViewHolder;

public class ListFragment extends Fragment {

    private Context mContext;

    private Query mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Cringe, CringeViewHolder> mAdapter;
    private ListView mCringeListeView;
    private LinearLayoutManager mManager;
    private RecyclerView mRecycler;

    private ImageView authorPicture;
    private Query mQuery;
    private FirebaseUser mUser;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_list, container, false);

        mContext = getActivity().getApplicationContext();

        // Change number to show more post by default
        mDatabase = FirebaseDatabase.getInstance().getReference("cringes").limitToLast(10).orderByChild("isPrivate").equalTo(false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.cringes_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        authorPicture = (ImageView) getActivity().findViewById(R.id.cringe_author_photo);

        // Set up FirebaseRecyclerAdapter with the Query
        mAdapter = new FirebaseRecyclerAdapter<Cringe, CringeViewHolder>(
                Cringe.class, R.layout.item_cringe, CringeViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final CringeViewHolder viewHolder, final Cringe cringe, final int position) {
                final DatabaseReference cringeRef = getRef(position);

                viewHolder.setAuthorName(cringe.author);
                viewHolder.setDateCreationView(getTimeAgo(cringe.createdAt));
                viewHolder.setBodyView(cringe.desc);
                Picasso
                        .with(getContext())
                        .load(cringe.author_picture)
                        .transform(new CircleTransform())
                        .into(viewHolder.authorPicture);

                // UNCOMMENT TO ACTIVATE ACCESS TO DETAIL VIEW
                final String cringeKey = cringeRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), DetailCringeActivity.class);
                        intent.putExtra(DetailCringeActivity.EXTRA_POST_KEY, cringeKey);
                        startActivity(intent);
                    }
                });

            }
        };
        mRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String getTimeAgo(Long timeInMilliseconds) {

        if (timeInMilliseconds < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            timeInMilliseconds *= 1000;
        }

        long now = System.currentTimeMillis();
        if (timeInMilliseconds > now || timeInMilliseconds <= 0) {
            return null;
        }

        final long diff = now - timeInMilliseconds;
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
