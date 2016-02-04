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
 * Created by Admin on 01.02.2016.
 */
public class MODSongAdapter extends ArrayAdapter<Song> {

    public MODSongAdapter(Context context, ArrayList<Song> songs) {
        super(context, R.layout.item_mod_folder, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Song song = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mod_song, parent, false);
        }
        TextView nameTextView = (TextView) convertView.findViewById(R.id.song_name_textView);
        ImageView playImageView = (ImageView) convertView.findViewById(R.id.play_imageView);

        nameTextView.setText(song.getName());

        if (position != 0) {
            playImageView.setImageResource(R.drawable.ic_play_arrow_white);
        } else {
            playImageView.setImageResource(R.drawable.ic_play_arrow_orange);
        }

        return convertView;
    }
}
