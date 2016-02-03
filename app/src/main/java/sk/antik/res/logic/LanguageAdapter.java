package sk.antik.res.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sk.antik.res.R;

/**
 * Created by Snowcat on 25.01.2016.
 */
public class LanguageAdapter extends ArrayAdapter<Language> {

    public LanguageAdapter(Context context, ArrayList<Language> languages) {
        super(context, R.layout.item_setting, languages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Language language = getItem(position);
        if (language.selected)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_language_selected, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_language, parent, false);
        TextView settingNameTextView = (TextView) convertView.findViewById(R.id.language_name_textView);
        settingNameTextView.setText(language.name);
        return convertView;
    }

}
