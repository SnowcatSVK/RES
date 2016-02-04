package sk.antik.res.logic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Snowcat on 04.02.2016.
 */
public class ChannelFactory {

    public static Channel channelFromJSON(JSONObject jsonObject) {
        Channel channel = new Channel();
        try {

            channel.name = jsonObject.getString("name");
            channel.streamURL = jsonObject.getString("source");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Channel", channel.toString());
        return channel;
    }

    public static ArrayList<Channel> fromJSON(JSONArray jsonArray) throws JSONException {
        ArrayList<Channel> channels = new ArrayList<>(jsonArray.length());
        JSONObject object = jsonArray.getJSONObject(0);
        JSONArray actualChannels = object.getJSONArray("content");
        for (int i = 0; i < actualChannels.length(); i++) {
            JSONObject channelJson;
            try {
                channelJson = actualChannels.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            Channel channel = channelFromJSON(channelJson);
            if (channel != null)
                channels.add(channel);
        }
        return channels;
    }
}
