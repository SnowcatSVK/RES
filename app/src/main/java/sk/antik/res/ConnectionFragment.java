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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import sk.antik.res.loader.AppModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionFragment extends Fragment {

    public boolean chromeInstalled = false;

    private ImageButton facebookButton;
    private ImageButton twitterButton;
    private ImageButton chromeButton;
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
                    installPackage("com.android.chrome.apk", getActivity());
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
                    installPackage("com.android.chrome.apk", getActivity());
                }
            }
        });
        chromeButton = (ImageButton) rootView.findViewById(R.id.google_chrome_button);
        if (chromeInstalled) {
            chromeButton.setImageResource(R.drawable.chrome);
            facebookButton.setImageResource(R.drawable.facebook);
            twitterButton.setImageResource(R.drawable.twitter);
        } else {
            chromeButton.setImageResource(R.drawable.chrome_grey);
            facebookButton.setImageResource(R.drawable.facebook_grey);
            twitterButton.setImageResource(R.drawable.twitter_grey);
        }
        chromeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chromeInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchIntent);
                } else {

                    installPackage("com.android.chrome.apk", getActivity());
                }
            }
        });
        return rootView;
    }

    public void switchIcon(String name) {
        switch (name) {
            case "Facebook":
                facebookButton.setImageResource(R.drawable.facebook);
                break;
            case "Twitter":
                twitterButton.setImageResource(R.drawable.twitter);
                break;
            case "Chrome":
                chromeButton.setImageResource(R.drawable.chrome);
                break;
        }
    }

    public void installPackage(final String packageName, final Context context) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(context, "Transfering files",
                        "please wait...", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                AssetManager assetManager = context.getAssets();

                InputStream in = null;
                OutputStream out = null;

                try {

                    in = assetManager.open(packageName);
                    out = new FileOutputStream(context.getExternalFilesDir(null).getPath() + "/" + packageName);

                    byte[] buffer = new byte[1024];

                    int read;
                    Log.e("Installations_instPkg", "Transfer start: " + packageName);
                    while ((read = in.read(buffer)) != -1) {

                        out.write(buffer, 0, read);

                    }
                    Log.e("Installations_instPkg", "Transfer end: " + packageName);
                    in.close();
                    in = null;

                    out.flush();
                    out.close();
                    out = null;

                    Log.e("Installations_instPkg", packageName.substring(0, packageName.length() - 4));
                } catch (Exception e) {
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
