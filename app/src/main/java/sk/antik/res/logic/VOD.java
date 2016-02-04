package sk.antik.res.logic;

/**
 * Created by Admin on 20.01.2016.
 */
public class VOD {

    private int id;
    private String name;
    private String source;

    public VOD(int id, String name, String source) {
        this.id = id;
        this.name = name;
        this.source = source;
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
}
