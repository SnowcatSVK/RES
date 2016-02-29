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
public class GamesFragment extends Fragment {

    public boolean candyCrushInstalled = false;
    public boolean angryBirdsInstalled = false;
    public boolean spiderSolitaireInstalled = false;

    private ImageButton candyButton;
    private ImageButton birdsButton;
    private ImageButton spiderButton;
    ProgressDialog progressDialog;

    public GamesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        candyButton = (ImageButton) rootView.findViewById(R.id.candy_crush_button);
        if (candyCrushInstalled) {
            candyButton.setImageResource(R.drawable.candy_crush);
        } else {
            candyButton.setImageResource(R.drawable.candy_crush_grey);
        }
        candyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (candyCrushInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.king.candycrushsaga");
                    startActivity(launchIntent);
                } else {
                    installPackage("candycrush.apk",getActivity());
                }
            }
        });
        birdsButton = (ImageButton) rootView.findViewById(R.id.angry_birds_button);
        if (angryBirdsInstalled) {
            birdsButton.setImageResource(R.drawable.angry_birds);
        } else {
            birdsButton.setImageResource(R.drawable.angry_birds_grey);
        }
        birdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (angryBirdsInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.rovio.angrybirds");
                    startActivity(launchIntent);
                } else {
                    installPackage("com.rovio.angrybirds.apk",getActivity());
                }
            }
        });
        spiderButton = (ImageButton) rootView.findViewById(R.id.spider_solitaire_button);
        if (spiderSolitaireInstalled) {
            spiderButton.setImageResource(R.drawable.spider_solitaire);
        } else {
            spiderButton.setImageResource(R.drawable.spider_solitaire_grey);
        }
        spiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spiderSolitaireInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.mobilityware.spider");
                    startActivity(launchIntent);
                } else {
                    installPackage("com.mobilityware.spider.apk",getActivity());
                }
            }
        });
        return rootView;
    }


    public void switchIcon(String name) {
        switch (name) {
            case "Angry Birds":
                birdsButton.setImageResource(R.drawable.angry_birds);
                break;
            case "Candy Crush":
                candyButton.setImageResource(R.drawable.candy_crush);
                break;
            case "Spider Solitaire":
                spiderButton.setImageResource(R.drawable.spider_solitaire);
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
