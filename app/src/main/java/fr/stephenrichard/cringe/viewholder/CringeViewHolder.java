package fr.stephenrichard.cringe.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;

/**
 * Created by stephenrichard on 11/06/16.
 */
public class CringeViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public TextView authorView;
    public TextView dateCreationView;
    public TextView bodyView;
    public ImageView authorPicture;

    public CringeViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        authorView = (TextView) mView.findViewById(R.id.cringe_author_name);
        dateCreationView = (TextView) mView.findViewById(R.id.cringe_creation_date);
        bodyView = (TextView) mView.findViewById(R.id.cringe_desc);
        authorPicture = (ImageView) mView.findViewById(R.id.cringe_author_photo);
    }

    public void bindToCringe(Cringe cringe) {
        authorView.setText(cringe.author);
        dateCreationView.setText(cringe.created_at);
        bodyView.setText(cringe.desc);
    }

    public void setAuthorName(String name) {
        authorView.setText(name);
    }

    public void setDateCreationView(String date) {
        dateCreationView.setText(date);
    }

    public void setBodyView(String text) {
        bodyView.setText(text);
    }

    public void setAuthorPicture(String url) {

        // authorPicture.setImageBitmap(getBitmapFromURL(url));
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
