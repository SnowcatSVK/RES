package sk.antik.res.logic;

/**
 * Created by Snowcat on 25.01.2016.
 */
public class Language {

    public String name;
    public String countryCode;
    public boolean selected;

    public Language(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
        selected = false;
    }
}
