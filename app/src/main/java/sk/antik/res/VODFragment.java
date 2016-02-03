package sk.antik.res;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import sk.antik.res.logic.VOD;
import sk.antik.res.logic.VODAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class VODFragment extends Fragment {

    private TwoWayView vod;

    public VODFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod, container, false);

        vod = (TwoWayView) view.findViewById(R.id.vod_twoWayView);

        ArrayList<VOD> vods = new ArrayList<>();
        vods.add(new VOD("Twilight", "2008", R.drawable.vod1));
        vods.add(new VOD("AVATAR", "2009", R.drawable.vod2));
        vods.add(new VOD("TITANIC", "1997", R.drawable.vod3));
        vods.add(new VOD("Star Wars", "2011", R.drawable.vod4));
        vods.add(new VOD("Friends with Benefits", "2004", R.drawable.vod5));
        vods.add(new VOD("Rush", "2013", R.drawable.vod6));
        vods.add(new VOD("The Green Mile", "1999", R.drawable.vod7));


        VODAdapter adapter = new VODAdapter(getActivity(), vods);

        ImageView leftArrowImageView = (ImageView) view.findViewById(R.id.left_imageButton);
        ImageView rightArrowImageView = (ImageView) view.findViewById(R.id.right_imageButton);

        leftArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod.scrollBy(100);
            }
        });

        rightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod.scrollBy(-100);
            }
        });

        /*ArrayList<String> items = new ArrayList<String>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        items.add("Item 4");
        items.add("Item 5");


        ArrayAdapter<String> aItems = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item_1, items);*/
        vod.setAdapter(adapter);

        return view;
    }


}
