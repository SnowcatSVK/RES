package sk.antik.res.logic;

/**
 * Created by Snowcat on 19.01.2016.
 */
public class Channel {

    public String name;
    public String streamURL;
    public boolean selected;
    public boolean fullScreen;

    public Channel() {
        selected = false;
        fullScreen = false;
    }

    public String toString() {
        String returnString = "Name: " + name + "Source: " + streamURL;
        return returnString;
    }
}
