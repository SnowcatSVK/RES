package sk.antik.res.logic;

/**
 * Created by Admin on 01.02.2016.
 */
public class Song {

    private String number;
    private String name;
    private String lenght;

    public Song(String number, String name, String lenght) {
        this.number = number;
        this.name = name;
        this.lenght = lenght;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getLenght() {
        return lenght;
    }
}
