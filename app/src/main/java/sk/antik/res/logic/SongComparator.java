package sk.antik.res.logic;

import java.util.Comparator;

/**
 * Created by Admin on 22.02.2016.
 */
public class SongComparator implements Comparator<Song> {
    @Override
    public int compare(Song song1, Song song2) {
        return song1.getNumber().compareTo(song2.getNumber());
    }
}
