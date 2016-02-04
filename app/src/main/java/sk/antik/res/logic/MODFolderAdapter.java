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
 * Created by Admin on 28.01.2016.
 */
public class MODFolderAdapter extends ArrayAdapter<Album> {

    public MODFolderAdapter(Context context, ArrayList<Album> albums) {
        super(context, R.layout.item_mod_folder, albums);
    }

    @Override
    public Album getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Album album = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mod_folder, parent, false);
        }
        TextView folderTextView = (TextView) convertView.findViewById(R.id.folder_textView);
        folderTextView.setText(album.getName());

        return convertView;
    }
}
