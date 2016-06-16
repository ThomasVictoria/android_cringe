package fr.stephenrichard.cringe.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.stephenrichard.cringe.R;

/**
 * Created by stephenrichard on 11/06/16.
 */
public class CringeViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public TextView authorView;
    public TextView dateCreationView;
    public TextView bodyView;
    public ImageView authorPicture;
    public ImageView cringeIcon;

    public CringeViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        authorView = (TextView) mView.findViewById(R.id.cringe_author_name);
        dateCreationView = (TextView) mView.findViewById(R.id.cringe_creation_date);
        bodyView = (TextView) mView.findViewById(R.id.cringe_desc);
        authorPicture = (ImageView) mView.findViewById(R.id.cringe_author_photo);
        cringeIcon = (ImageView) mView.findViewById(R.id.cringe_level_icon);
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

}
