package sk.antik.res.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sk.antik.res.R;

/**
 * Created by Admin on 20.01.2016.
 */
public class VODAdapter extends ArrayAdapter<VOD> {

    public VODAdapter(Context context, ArrayList<VOD> vods) {
        super(context, R.layout.item_vod, vods);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VOD vod = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_vod, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name_textView);
        TextView year = (TextView) convertView.findViewById(R.id.year_textView);
        ImageView image = (ImageView) convertView.findViewById(R.id.vod_imageView);

        name.setText(vod.getName());
        year.setText(vod.getYear());
        image.setImageResource(vod.getImage());

        return convertView;
    }
}
