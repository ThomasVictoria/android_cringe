package fr.stephenrichard.cringe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fr.stephenrichard.cringe.CircleTransform;
import fr.stephenrichard.cringe.MainActivity;
import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;

public class DetailCringeActivity extends AppCompatActivity {

    public static final String EXTRA_POST_KEY = "post_key";

    public DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private ImageView authorPicture;
    private ImageView mapPicture;
    private TextView authorName;
    private TextView cringeDate;
    private TextView cringeDesc;
    private TextView cringeLevelText;
    private ImageView arrowBack;
    private ImageView cringeIcon;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cringe);

        Bundle extras = getIntent().getExtras();
        final String cringeKey = (String) extras.get(EXTRA_POST_KEY);

        mContext = this.getApplication();

        authorName = (TextView) findViewById(R.id.cringe_author_name);
        authorPicture = (ImageView) findViewById(R.id.cringe_author_photo);
        mapPicture = (ImageView) findViewById(R.id.map);
        cringeDate = (TextView) findViewById(R.id.cringe_creation_date);
        cringeDesc = (TextView) findViewById(R.id.cringe_desc);
        cringeIcon = (ImageView) findViewById(R.id.cringe_level_icon);
        cringeLevelText = (TextView) findViewById(R.id.cringe_level_text);

        mDataBase.child("cringes").child(cringeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cringe cringe = dataSnapshot.getValue(Cringe.class);
                authorName.setText(cringe.author);
                cringeDesc.setText(cringe.desc);
                cringeDate.setText(getTimeAgo(cringe.createdAt));
                Picasso
                        .with(mContext)
                        .load(cringe.author_picture)
                        .transform(new CircleTransform())
                        .into(authorPicture);

                Picasso
                        .with(mContext)
                        .load("https://maps.googleapis.com/maps/api/staticmap?center="+cringe.lat+","+cringe.lng+"&zoom=16&size=640x640&maptype=roadmap&markers=color:red%7C"+cringe.lat+","+cringe.lng+"&key=AIzaSyCD81N4t9R-BppOD4jsiNoSyJk1YBxlfBM")
                        .into(mapPicture);

                int id = mContext.getResources().getIdentifier(imageLevel(cringe.level), "drawable", mContext.getPackageName());
                cringeIcon.setImageResource(id);
                cringeLevelText.setText(textLevel(cringe.level));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        arrowBack = (ImageView) findViewById(R.id.arrow_back);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                startActivity(new Intent(DetailCringeActivity.this, MainActivity.class));
            }
        });
    }

    public String imageLevel(Integer level){

        String url = "";

        switch (level) {
            case 1:
                url = "cringe_level_checked_1";
                break;
            case 2:
                url = "cringe_level_checked_2";
                break;
            case 3:
                url = "cringe_level_checked_3";
                break;
            case 4:
                url = "cringe_level_checked_4";
                break;
            case 5:
                url = "cringe_level_checked_5";
                break;
            default:
        }

        return url;
    }
    public String textLevel(Integer level){

        String text = "";

        switch (level) {
            case 1:
                text = "C'est un peu génant";
                break;
            case 2:
                text = "Je commence à rougir";
                break;
            case 3:
                text = "Je me sens opressé";
                break;
            case 4:
                text = "Je met met en position latérale de sécurité";
                break;
            case 5:
                text = "A+, je pars dans le Larzac";
                break;
            default:
        }

        return text;
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
