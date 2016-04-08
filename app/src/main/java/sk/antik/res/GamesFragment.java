package sk.antik.res;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
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
public class GamesFragment extends Fragment {

    public boolean candyCrushInstalled = false;
    public boolean sudokuInstalled = false;
    public boolean spiderSolitaireInstalled = false;
    public boolean chessInstalled = false;
    public boolean batakInstalled = false;
    public boolean backgammonInstalled = false;
    public boolean yuzbirInstalled = false;
    public boolean fruitNinjaInstalled = false;

    private ImageButton candyButton;
    private ImageButton sudokuButton;
    private ImageButton spiderButton;
    private ImageButton chessButton;
    private ImageButton batakButton;
    private ImageButton backgammonButton;
    private ImageButton yuzbirButton;
    private ImageButton fruitNinjaButton;
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

        sudokuButton = (ImageButton) rootView.findViewById(R.id.sudoku_button);
        if (sudokuInstalled) {
            sudokuButton.setImageResource(R.drawable.sudoku);
        } else {
            sudokuButton.setImageResource(R.drawable.sudoku_grey);
        }
        sudokuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sudokuInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.brainium.sudoku.free");
                    startActivity(launchIntent);
                } else {
                    findApk("com.brainium.sudoku.free.apk");
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

        chessButton = (ImageButton) rootView.findViewById(R.id.chess_button);
        if (chessInstalled) {
            chessButton.setImageResource(R.drawable.chess);
        } else {
            chessButton.setImageResource(R.drawable.chess_grey);
        }
        chessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chessInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("uk.co.aifactory.chessfree");
                    startActivity(launchIntent);
                } else {
                    findApk("uk.co.aifactory.chessfree.apk");
                }
            }
        });

        batakButton = (ImageButton) rootView.findViewById(R.id.batak_button);
        if (batakInstalled) {
            batakButton.setImageResource(R.drawable.batak);
        } else {
            candyButton.setImageResource(R.drawable.batak_grey);
        }
        batakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (batakInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.game.pisti");
                    startActivity(launchIntent);
                } else {
                    findApk("com.game.pisti.apk");
                }
            }
        });

        backgammonButton = (ImageButton) rootView.findViewById(R.id.backgammon_button);
        if (backgammonInstalled) {
            backgammonButton.setImageResource(R.drawable.back_gammon);
        } else {
            backgammonButton.setImageResource(R.drawable.back_gammon_grey);
        }
        backgammonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backgammonInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("uk.co.aifactory.backgammonfree");
                    startActivity(launchIntent);
                } else {
                    findApk("uk.co.aifactory.backgammonfree.apk");
                }
            }
        });

        yuzbirButton = (ImageButton) rootView.findViewById(R.id.yuzbir_okey_button);
        if (yuzbirInstalled) {
            yuzbirButton.setImageResource(R.drawable.yuzbir_okey);
        } else {
            yuzbirButton.setImageResource(R.drawable.yuzbir_okey_grey);
        }
        yuzbirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yuzbirInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("net.peakgames.Yuzbir");
                    startActivity(launchIntent);
                } else {
                    findApk("net.peakgames.Yuzbir.apk");
                }
            }
        });

        fruitNinjaButton = (ImageButton) rootView.findViewById(R.id.fruit_ninja_button);
        if (fruitNinjaInstalled) {
            fruitNinjaButton.setImageResource(R.drawable.fruit_ninja);
        } else {
            fruitNinjaButton.setImageResource(R.drawable.fruit_ninja_grey);
        }
        fruitNinjaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fruitNinjaInstalled) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.halfbrick.fruitninjafree");
                    startActivity(launchIntent);
                } else {
                    findApk("com.halfbrick.fruitninjafree.apk");
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


    public void switchIcon(String name) {
        switch (name) {
            case "Sudoku":
                sudokuButton.setImageResource(R.drawable.sudoku);
                break;
            case "Candy Crush":
                candyButton.setImageResource(R.drawable.candy_crush);
                break;
            case "Spider Solitaire":
                spiderButton.setImageResource(R.drawable.spider_solitaire);
                break;
            case "Chess":
                chessButton.setImageResource(R.drawable.chess);
                break;
            case "Batak":
                batakButton.setImageResource(R.drawable.batak);
                break;
            case "Backgammon":
                backgammonButton.setImageResource(R.drawable.back_gammon);
                break;
            case "Yuzbir":
                yuzbirButton.setImageResource(R.drawable.yuzbir_okey);
                break;
            case "Fruit Ninja":
                fruitNinjaButton.setImageResource(R.drawable.fruit_ninja);
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
