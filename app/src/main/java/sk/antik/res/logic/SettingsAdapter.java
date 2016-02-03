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
 * Created by Snowcat on 25.01.2016.
 */
public class SettingsAdapter extends ArrayAdapter<Setting> {

    public SettingsAdapter(Context context, ArrayList<Setting> settings) {
        super(context, R.layout.item_setting, settings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Setting setting = getItem(position);
        if (setting.selected)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting_selected, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting, parent, false);
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.setting_icom_imageView);
        TextView settingNameTextView = (TextView) convertView.findViewById(R.id.setting_name_textView);
        if (setting.selected)
            iconImageView.setImageDrawable(setting.iconWhite);
        else
            iconImageView.setImageDrawable(setting.icon);
        settingNameTextView.setText(setting.name);
        return convertView;
    }
}
