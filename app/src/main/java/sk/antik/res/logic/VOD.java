package sk.antik.res.logic;

/**
 * Created by Admin on 20.01.2016.
 */
public class VOD {

    private int id;
    private String name;
    private String source;
    private String imgSource;

    public VOD(int id, String name, String source, String imgSource) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.imgSource = imgSource;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getImgSource() {
        return imgSource;
    }

}
