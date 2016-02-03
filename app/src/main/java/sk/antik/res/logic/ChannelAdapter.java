package sk.antik.res.logic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import sk.antik.res.R;

/**
 * Created by Snowcat on 19.01.2016.
 */
public class ChannelAdapter extends ArrayAdapter<Channel> {


    public ChannelAdapter(Context context, ArrayList<Channel> channels) {
        super(context, R.layout.item_channel, channels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Channel channel = getItem(position);
        if (!channel.fullScreen)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_channel, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_channel_fullscreen, parent, false);
        TextView numberTextView = (TextView) convertView.findViewById(R.id.channel_number_textView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.channel_name_textView);
        nameTextView.setText(channel.name);
        String number = String.valueOf(position + 1);
        numberTextView.setText(number);
        return convertView;
    }
}
