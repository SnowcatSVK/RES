package sk.antik.res;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class GamesFragment extends Fragment {


    public GamesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        ImageButton candyButton = (ImageButton) rootView.findViewById(R.id.candy_crush_button);
        candyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.king.candycrushsaga");
                startActivity(launchIntent);
            }
        });
        ImageButton birdsButton = (ImageButton) rootView.findViewById(R.id.angry_birds_button);
        birdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.rovio.angrybirds");
                startActivity(launchIntent);
            }
        });
        ImageButton spiderButton = (ImageButton) rootView.findViewById(R.id.spider_solitaire_button);
        spiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.mobilityware.spider");
                startActivity(launchIntent);
            }
        });
        return rootView;
    }


}
