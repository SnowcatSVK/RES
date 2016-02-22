package sk.antik.res.logic;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Admin on 04.02.2016.
 */
public class Album {

    private int id;
    private String name;
    private String source;
    private String imageSource;
    private ArrayList<Song> songs;

    public Album(int id, String name, String source, String imageSource, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.source = source;
        Collections.sort(songs, new SongComparator());
        this.songs = songs;
        this.imageSource = imageSource;
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public String getImageSource() {
        return imageSource;
    }
}
