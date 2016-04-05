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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import sk.antik.res.loader.AppModel;
import sk.antik.res.logic.Apk;


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
                    findApk("candycrush.apk");
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
                    findApk("com.rovio.angrybirds.apk");
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
                    findApk("com.mobilityware.spider.apk");
                }
            }
        });
        return rootView;
    }

    public void findApk(String name) {
        for (Apk apk : MainActivity.apps) {
            Log.e("ApkResponse", apk.name);
            if (apk.name.equalsIgnoreCase(name)) {

                installPackage(name, apk.address, getActivity());
            }
        }
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
