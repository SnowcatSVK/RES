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
public class MODFolderAdapter extends ArrayAdapter<String> {

    public MODFolderAdapter(Context context, ArrayList<String> folders) {
        super(context, R.layout.item_mod_folder, folders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String folderName = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mod_folder, parent, false);
        }
        TextView folderTextView = (TextView) convertView.findViewById(R.id.folder_textView);
        folderTextView.setText(folderName);

        return convertView;
    }
}
