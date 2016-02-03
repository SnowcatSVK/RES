package sk.antik.res.logic;

/**
 * Created by Snowcat on 19.01.2016.
 */
public class Channel {

    public String name;
    public String streamURL;
    public boolean selected;
    public boolean fullScreen;

    public Channel(String name) {
        this.name = name;
        selected = false;
        streamURL = "http://88.212.15.23/live/doma_1200/playlist.m3u8";
    }
}
