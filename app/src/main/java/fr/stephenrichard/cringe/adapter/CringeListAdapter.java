package fr.stephenrichard.cringe.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.stephenrichard.cringe.R;
import fr.stephenrichard.cringe.model.Cringe;

/**
 * Created by stephenrichard on 12/06/16.
 */
public class CringeListAdapter extends ArrayAdapter<Cringe> {

    private Context mContext;

    public CringeListAdapter(Context context, int resourceId, List<Cringe> items) {
        super(context, resourceId, items);
        this.mContext = context;
    }

    private class ViewHolder {
        TextView authorView;
        TextView dateCreationView;
        TextView bodyView;
        ImageView authorPicture;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Cringe cringe = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_cringe, null);
            holder = new ViewHolder();
            holder.authorView = (TextView) convertView.findViewById(R.id.cringe_author_name);
            holder.dateCreationView = (TextView) convertView.findViewById(R.id.cringe_creation_date);
            holder.bodyView = (TextView) convertView.findViewById(R.id.cringe_desc);
            holder.authorPicture = (ImageView) convertView.findViewById(R.id.cringe_author_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.authorView.setText(cringe.getAuthor());
        holder.dateCreationView.setText(cringe.getDateCreation());
        holder.bodyView.setText(cringe.getDesc());
        holder.authorPicture.setImageBitmap(cringe.getAuthor_picture());

        return convertView;
    }

}
