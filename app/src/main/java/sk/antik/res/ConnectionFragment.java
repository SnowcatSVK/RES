package sk.antik.res;


import android.content.Intent;
import android.net.Uri;
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
public class ConnectionFragment extends Fragment {


    public ConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        ImageButton candyButton = (ImageButton) rootView.findViewById(R.id.facebook_button);
        candyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                launchIntent.setData(Uri.parse("https://www.facebook.com"));
                startActivity(launchIntent);
            }
        });
        ImageButton birdsButton = (ImageButton) rootView.findViewById(R.id.twitter_button);
        birdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                startActivity(launchIntent);
            }
        });
        ImageButton spiderButton = (ImageButton) rootView.findViewById(R.id.google_chrome_button);
        spiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
            }
        });
        return rootView;
    }


}
