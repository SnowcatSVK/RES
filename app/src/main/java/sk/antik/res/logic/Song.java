package sk.antik.res.logic;

/**
 * Created by Admin on 01.02.2016.
 */
public class Song {

    private int id;
    private int albumId;
    private String name;
    private String source;

    public Song(int id, int albumId, String name, String source) {
        this.id = id;
        this.albumId = albumId;
        this.name = name;
        this.source = source;
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
}
