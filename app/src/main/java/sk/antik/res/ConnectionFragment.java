package sk.antik.res;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import sk.antik.res.loader.AppModel;
import sk.antik.res.logic.Apk;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionFragment extends Fragment {

    public boolean chromeInstalled = false;

    private ImageButton facebookButton;
    private ImageButton twitterButton;
    private ImageButton chromeButton;
    private ImageButton youtubeButton;
    private ImageButton bookingButton;
    private ProgressDialog progressDialog;

    public ConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        facebookButton = (ImageButton) rootView.findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chromeInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    launchIntent.setData(Uri.parse("https://www.facebook.com"));
                    startActivity(launchIntent);
                } else {
                    findApk("com.android.chrome.apk");
                }
            }
        });
        twitterButton = (ImageButton) rootView.findViewById(R.id.twitter_button);

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chromeInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    launchIntent.setData(Uri.parse("https://www.twitter.com"));
                    startActivity(launchIntent);
                } else {
                    findApk("com.android.chrome.apk");
                }
            }
        });

        youtubeButton = (ImageButton) rootView.findViewById(R.id.youtube_button);
        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chromeInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    launchIntent.setData(Uri.parse("https://www.youtube.com"));
                    startActivity(launchIntent);
                } else {
                    findApk("com.android.chrome.apk");
                }
            }
        });

        bookingButton = (ImageButton) rootView.findViewById(R.id.booking_button);
        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chromeInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    launchIntent.setData(Uri.parse("https://www.booking.com"));
                    startActivity(launchIntent);
                } else {
                    findApk("com.android.chrome.apk");
                }
            }
        });

        chromeButton = (ImageButton) rootView.findViewById(R.id.google_chrome_button);
        if (chromeInstalled) {
            chromeButton.setImageResource(R.drawable.chrome);
            facebookButton.setImageResource(R.drawable.facebook);
            twitterButton.setImageResource(R.drawable.twitter);
            youtubeButton.setImageResource(R.drawable.youtube);
            bookingButton.setImageResource(R.drawable.booking);
        } else {
            chromeButton.setImageResource(R.drawable.chrome_grey);
            facebookButton.setImageResource(R.drawable.facebook_grey);
            twitterButton.setImageResource(R.drawable.twitter_grey);
            youtubeButton.setImageResource(R.drawable.youtube_grey);
            bookingButton.setImageResource(R.drawable.booking_grey);
        }
        chromeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chromeInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchIntent);
                } else {
                    findApk("com.android.chrome.apk");
                }
            }
        });
        return rootView;
    }

    public void findApk(String name) {
        if (MainActivity.apps != null) {
            for (Apk apk : MainActivity.apps) {
                Log.e("ApkResponse", apk.name);
                if (apk.name.equalsIgnoreCase(name)) {

                    installPackage(name, apk.address, getActivity());
                } else {
                    Toast.makeText(getActivity(), "Can't find installation file", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Can't find installation file", Toast.LENGTH_LONG).show();
        }
    }

    public void switchIcon() {
        facebookButton.setImageResource(R.drawable.facebook);
        twitterButton.setImageResource(R.drawable.twitter);
        chromeButton.setImageResource(R.drawable.chrome);
        youtubeButton.setImageResource(R.drawable.youtube);
        bookingButton.setImageResource(R.drawable.booking);
    }

    public void installPackage(final String packageName, final String address, final Context context) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(context, "Transfering files",
                        "please wait...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MainActivity.handler.downloadFile(address, packageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Intent install = new Intent(Intent.ACTION_VIEW);

                install.setDataAndType(Uri.fromFile(new File(context.getExternalFilesDir(null).getPath() + "/" + packageName)),
                        "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
                progressDialog.dismiss();
            }
        }.execute();


    }
}
