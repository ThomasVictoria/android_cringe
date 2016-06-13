package fr.stephenrichard.cringe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.stephenrichard.cringe.CircleTransform;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;
import fr.stephenrichard.cringe.viewholder.CringeViewHolder;

public class ListActivity extends Fragment {

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

    public static final String TAG = "List activity";

    public ListActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_list, container, false);

        mContext = getActivity().getApplicationContext();

        // Change number to show more post by default
        mDatabase = FirebaseDatabase.getInstance().getReference("cringes").limitToLast(10);

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
        Query cringesQuery = mDatabase;
        mAdapter = new FirebaseRecyclerAdapter<Cringe, CringeViewHolder>(
                Cringe.class, R.layout.item_cringe, CringeViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final CringeViewHolder viewHolder, final Cringe cringe, final int position) {
                final DatabaseReference cringeRef = getRef(position);

                viewHolder.setAuthorName(cringe.author);
                viewHolder.setDateCreationView(cringe.created_at);
                viewHolder.setBodyView(cringe.desc);

                Picasso
                        .with(getContext())
                        .load(cringe.author_picture)
                        .transform(new CircleTransform())
                        .into(viewHolder.authorPicture);

                // UNCOMMENT TO ACTIVATE ACCESS TO DETAIL VIEW
                /*
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
                */
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


    public Query getQuery(DatabaseReference databaseReference) {
        return null;
    }
}
