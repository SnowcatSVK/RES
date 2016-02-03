package sk.antik.res;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import sk.antik.res.logic.MODFolderAdapter;
import sk.antik.res.logic.MODSongAdapter;
import sk.antik.res.logic.Song;


/**
 * A simple {@link Fragment} subclass.
 */
public class MODFragment extends Fragment {

    private ListView foldersListView;
    private ListView songsListView;

    public MODFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mod, container, false);

        foldersListView = (ListView) view.findViewById(R.id.folders_listView);
        songsListView = (ListView) view.findViewById(R.id.songs_listView);

        ArrayList<String> folders = new ArrayList<>();
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");
        folders.add("ABBA - Arrival");
        folders.add("Queen - Innuendo");
        folders.add("Oldfield - The Songs of Distant Earth");

        MODFolderAdapter adapter = new MODFolderAdapter(getActivity(), folders);

        foldersListView.setAdapter(adapter);

        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("01", "In The Beginning", "01:24"));
        songs.add(new Song("02", "Let There Be Light", "04:52"));
        songs.add(new Song("03", "Supernova", "03:29"));
        songs.add(new Song("04", "Magellan", "04:41"));
        songs.add(new Song("05", "First Landing", "01:18"));
        songs.add(new Song("06", "Oceania", "03:27"));
        songs.add(new Song("07", "Only Time Will Tell", "02:10"));
        songs.add(new Song("08", "Prayer for the Earth", "02:44"));
        songs.add(new Song("09", "Lament for Atlantis", "01:19"));
        songs.add(new Song("10", "The Chamber", "03:32"));
        songs.add(new Song("11", "In The Beginning", "01:24"));
        songs.add(new Song("12", "Let There Be Light", "04:52"));
        songs.add(new Song("13", "Supernova", "03:29"));
        songs.add(new Song("14", "Magellan", "04:41"));
        songs.add(new Song("15", "First Landing", "01:18"));
        songs.add(new Song("16", "Oceania", "03:27"));
        songs.add(new Song("17", "Only Time Will Tell", "02:10"));
        songs.add(new Song("18", "Prayer for the Earth", "02:44"));
        songs.add(new Song("19", "Lament for Atlantis", "01:19"));
        songs.add(new Song("20", "The Chamber", "03:32"));

        MODSongAdapter songAdapter = new MODSongAdapter(getActivity(), songs);

        songsListView.setAdapter(songAdapter);

        return view;
    }


}