package sk.antik.res.logic;

/**
 * Created by Admin on 01.02.2016.
 */
public class Song {

    private int id;
    private int albumId;
    private String name;
    private String source;
    private String number;

    public Song(int id, int albumId, String name, String source) {
        this.id = id;
        this.albumId = albumId;
        this.source = source;
        this.number = name.substring(0, 2);
        this.name = name.substring(3);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getSource() {
        return source;
    }

    public String getNumber() {
        return number;
    }
}
