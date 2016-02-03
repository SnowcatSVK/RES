package sk.antik.res;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import sk.antik.res.logic.MODFolderAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MODFragment extends Fragment {

    private ListView foldersListView;

    public MODFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mod, container, false);

        foldersListView = (ListView) view.findViewById(R.id.folders_listView);

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

        return view;
    }


}
