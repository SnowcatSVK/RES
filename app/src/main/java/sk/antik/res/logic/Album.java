package sk.antik.res.logic;

import java.util.ArrayList;

/**
 * Created by Admin on 04.02.2016.
 */
public class Album {

    private int id;
    private String name;
    private String source;
    private ArrayList<Song> songs;

    public Album(int id, String name, String source, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.songs = songs;
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
}
