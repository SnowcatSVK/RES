package sk.antik.res.logic;

/**
 * Created by Admin on 20.01.2016.
 */
public class VOD {

    private String name;
    private String year;
    private int image;

    public VOD(String name, String year, int image) {
        this.name = name;
        this.year = year;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getYear() {
        return year;
    }
}
