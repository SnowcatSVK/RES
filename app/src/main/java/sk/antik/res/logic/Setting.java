package sk.antik.res.logic;

import android.graphics.drawable.Drawable;

/**
 * Created by Snowcat on 25.01.2016.
 */
public class Setting {

    public String name;
    public Drawable icon;
    public Drawable iconWhite;
    public boolean selected;

    public Setting(String name, Drawable icon, Drawable whiteIcon) {
        this.name = name;
        this.icon = icon;
        iconWhite = whiteIcon;
        selected = false;
    }
}
