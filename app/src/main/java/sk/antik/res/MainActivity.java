package sk.antik.res;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import sk.antik.res.io.RequestHandler;
import sk.antik.res.loader.AppLoader;
import sk.antik.res.loader.AppModel;
import sk.antik.res.logic.Album;
import sk.antik.res.logic.Channel;
import sk.antik.res.logic.ChannelFactory;
import sk.antik.res.logic.Song;
import sk.antik.res.logic.VOD;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    private LiveTVFragment tvFragment;
    private RadioFragment radioFragment;
    private VODFragment vodFragment;
    private SettingFragment settingsFragment;
    private MODFragment modFragment;
    private GamesFragment gamesFragment;
    private ConnectionFragment connectionFragment;

    //private RelativeLayout rootLayout;
    private LinearLayout topBar;
    private LinearLayout bottomBarIcons;
    private LinearLayout bottomBarDescription;
    private boolean isTVFullscreen = false;
    private ArrayList<ImageButton> imageButtons;
    private TextView timeTextView;
    private TextView dateTextView;
    private TextView seatNumberTextView;
    private String API;
    private RequestHandler handler;
    private SharedPreferences prefs = null;
    private String seatNumber;
    public static String language;
    private boolean startFromFirstSetup;
    private boolean tvConnected = false;
    private boolean radCnnected = false;
    private boolean vodConnected = false;
    private boolean modConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
        prefs = getSharedPreferences("sk.antik.res", MODE_PRIVATE);
        API = prefs.getString("API_IP", "10.252.61.83");
        handler = new RequestHandler(API + ":81");
        seatNumber = prefs.getString("SEAT_No", "00");
        language = prefs.getString("APP_LANGUAGE", "us");
        Log.e("Setting test", "onCreate - Main");
        tvFragment = new LiveTVFragment();
        radioFragment = new RadioFragment();
        vodFragment = new VODFragment();
        settingsFragment = new SettingFragment();
        modFragment = new MODFragment();
        gamesFragment = new GamesFragment();
        connectionFragment = new ConnectionFragment();
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCache(new LimitedAgeDiskCache(cacheDir, 10));
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
        Resources res = getResources();
        // Change locale settings in the app.
        android.content.res.Configuration conf = res.getConfiguration();
        Intent intent = getIntent();
        startFromFirstSetup = intent.getBooleanExtra("FROM_SETUP", false);
        Log.e("Locale", conf.locale.getCountry());
        if (savedInstanceState == null && !startFromFirstSetup && !language.equalsIgnoreCase(conf.locale.getCountry())) {
            DisplayMetrics dm = res.getDisplayMetrics();
            conf.locale = new Locale(language);
            res.updateConfiguration(conf, dm);
            recreate();
        }

        topBar = (LinearLayout) findViewById(R.id.top_bar_linearLayout);
        bottomBarIcons = (LinearLayout) findViewById(R.id.bottom_bar_linearLayout);
        bottomBarDescription = (LinearLayout) findViewById(R.id.categories_names_layout);
        imageButtons = new ArrayList<>();
        imageButtons.add((ImageButton) findViewById(R.id.watch_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.radio_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.connect_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.games_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.vod_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.mod_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.setting_imageButton));

        timeTextView = (TextView) findViewById(R.id.time_main_activity_textView);
        dateTextView = (TextView) findViewById(R.id.date_main_activity_textView);
        seatNumberTextView = (TextView) findViewById(R.id.seat_number_textView);
        seatNumberTextView.setText(seatNumber);

        getLoaderManager().initLoader(0, null, this);


        /*KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyServiceRunning(AppKillerService.class)) {
            Intent intent = new Intent(this, AppKillerService.class);
            startService(intent);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //settingsFragment.setButton(imageButtons.get(imageButtons.size() - 1));
        loadVOD();
        loadMOD();
        loadChannels();
        loadRadios();
        setupTime();
        registerReceiver();
    }

    //AppKillerService.startingForbiddenApp = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle args) {
        return new AppLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> data) {
        for (AppModel app : data) {
            switch (app.getLabel()) {
                case "Candy Crush Saga":
                    gamesFragment.candyCrushInstalled = true;
                    break;
                case "Angry Birds":
                    gamesFragment.angryBirdsInstalled = true;
                    break;
                case "Spider":
                    gamesFragment.spiderSolitaireInstalled = true;
                    break;
                case "Chrome":
                    connectionFragment.chromeInstalled = true;
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {

    }

    public void onFullScreenButtonClick(View view) {
        if (isTVFullscreen) {
            topBar.setVisibility(View.VISIBLE);
            bottomBarIcons.setVisibility(View.VISIBLE);
            bottomBarDescription.setVisibility(View.VISIBLE);
            tvFragment.channelsListView.setVisibility(View.VISIBLE);
            final RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            final RelativeLayout.LayoutParams listViewrelativeParams = new RelativeLayout.LayoutParams(tvFragment.channelsListView.getWidth(),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            final RelativeLayout.LayoutParams controlsrelativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    tvFragment.controlsLayout.getHeight());
            relativeParams.addRule(RelativeLayout.END_OF, tvFragment.channelsListView.getId());
            listViewrelativeParams.removeRule(RelativeLayout.ABOVE);
            controlsrelativeParams.addRule(RelativeLayout.END_OF, tvFragment.channelsListView.getId());
            controlsrelativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            tvFragment.parent.updateViewLayout(tvFragment.tvFrame, relativeParams);
            tvFragment.parent.updateViewLayout(tvFragment.channelsListView, listViewrelativeParams);
            tvFragment.parent.updateViewLayout(tvFragment.controlsLayout, controlsrelativeParams);
            tvFragment.channelsListView.setBackgroundColor(Color.parseColor("#EEFFFFFF"));
            for (Channel channel : tvFragment.channels) {
                channel.fullScreen = false;
            }
            tvFragment.adapter.notifyDataSetChanged();
            tvFragment.channelListButton.setVisibility(View.GONE);
            tvFragment.separatorLayout.setVisibility(View.GONE);
            tvFragment.buttonSeparatorLayout.setVisibility(View.GONE);
            isTVFullscreen = false;
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFragment.setSize(tvFragment.mVideoWidth, tvFragment.mVideoHeight);
                            t.cancel();
                            t.purge();
                        }
                    });
                }
            }, 50);
        } else {
            topBar.setVisibility(View.GONE);
            bottomBarIcons.setVisibility(View.GONE);
            bottomBarDescription.setVisibility(View.GONE);
            tvFragment.channelsListView.setVisibility(View.GONE);
            tvFragment.channelsListView.setBackgroundColor(Color.parseColor("#AA000000"));
            for (Channel channel : tvFragment.channels) {
                channel.fullScreen = true;
            }
            tvFragment.channelsListView.invalidateViews();
            tvFragment.adapter.notifyDataSetChanged();
            tvFragment.channelListButton.setVisibility(View.VISIBLE);
            tvFragment.buttonSeparatorLayout.setVisibility(View.VISIBLE);
            isTVFullscreen = true;
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFragment.setSize(tvFragment.mVideoWidth, tvFragment.mVideoHeight);
                            t.cancel();
                            t.purge();
                        }
                    });
                }
            },50);

        }
    }

    public void onTVClick(View view) {

    }

    public void onWatchButtonClick(View view) {
        if (tvConnected) {
            getFragmentManager().beginTransaction().replace(R.id.root_layout, tvFragment).commit();
            setBackgrounds(view);
        } else {
            Toast.makeText(this, R.string.no_tv, Toast.LENGTH_LONG).show();
        }
    }

    public void onRadioButtonClick(View view) {
        if (radCnnected) {
            setBackgrounds(view);
            getFragmentManager().beginTransaction().replace(R.id.root_layout, radioFragment).commit();
            //tvFragment.releasePlayer();
        } else {
            Toast.makeText(this, R.string.no_tv, Toast.LENGTH_LONG).show();
        }
    }

    public void onConnectButtonClick(View view) {
        setBackgrounds(view);
        getFragmentManager().beginTransaction().replace(R.id.root_layout, connectionFragment).commit();
        //tvFragment.releasePlayer();
    }

    public void onGamesButtonClick(View view) {
        setBackgrounds(view);
        getFragmentManager().beginTransaction().replace(R.id.root_layout, gamesFragment).commit();
        //tvFragment.releasePlayer();
    }

    public void onVODButtonClick(View view) {
        if (vodConnected) {
            setBackgrounds(view);
            getFragmentManager().beginTransaction().replace(R.id.root_layout, vodFragment).commit();
        } else {
            Toast.makeText(this, R.string.no_tv, Toast.LENGTH_LONG).show();
        }
    }

    public void onMODButtonClick(View view) {
        if (modConnected) {
            setBackgrounds(view);
            getFragmentManager().beginTransaction().replace(R.id.root_layout, modFragment).commit();
        } else {
            Toast.makeText(this, R.string.no_tv, Toast.LENGTH_LONG).show();
        }
    }

    public void onSettingsButtonClick(View view) {
        setBackgrounds(view);
        getFragmentManager().beginTransaction().replace(R.id.root_layout, settingsFragment).commit();
    }

    public void onChannelListButtonClick(View view) {
        if (isTVFullscreen) {
            tvFragment.channelsListView.setVisibility(View.VISIBLE);
            tvFragment.separatorLayout.setVisibility(View.VISIBLE);
            final RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            relativeParams.removeRule(RelativeLayout.END_OF);
            tvFragment.parent.updateViewLayout(tvFragment.tvFrame, relativeParams);
            final RelativeLayout.LayoutParams listViewrelativeParams = new RelativeLayout.LayoutParams(tvFragment.channelsListView.getWidth(), ViewGroup.LayoutParams.MATCH_PARENT);
            final RelativeLayout.LayoutParams controlsrelativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tvFragment.controlsLayout.getHeight());
            controlsrelativeParams.removeRule(RelativeLayout.END_OF);
            controlsrelativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            listViewrelativeParams.addRule(RelativeLayout.ABOVE, tvFragment.controlsLayout.getId());
            tvFragment.parent.updateViewLayout(tvFragment.channelsListView, listViewrelativeParams);
            tvFragment.parent.updateViewLayout(tvFragment.controlsLayout, controlsrelativeParams);
            tvFragment.channelsListView.bringToFront();
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isTVFullscreen) {
                                tvFragment.channelsListView.setVisibility(View.GONE);
                                tvFragment.separatorLayout.setVisibility(View.GONE);
                                relativeParams.addRule(RelativeLayout.END_OF, tvFragment.channelsListView.getId());
                                listViewrelativeParams.removeRule(RelativeLayout.ABOVE);
                                controlsrelativeParams.addRule(RelativeLayout.END_OF, tvFragment.channelsListView.getId());
                                tvFragment.parent.updateViewLayout(tvFragment.tvFrame, relativeParams);
                                tvFragment.parent.updateViewLayout(tvFragment.channelsListView, listViewrelativeParams);
                                tvFragment.parent.updateViewLayout(tvFragment.controlsLayout, controlsrelativeParams);
                                t.cancel();
                                t.purge();
                            }
                        }
                    });
                }
            }, 5000);
        }
    }

    private void setBackgrounds(View v) {
        for (ImageButton button : imageButtons) {
            button.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        }
        v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onBackPressed() {
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void registerReceiver() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setupTime();
            }
        }, new IntentFilter(Intent.ACTION_TIME_TICK));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");
        registerReceiver(new AppInstallReceiver(), filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setupTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format; //= new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy");
        Locale locale = new Locale(language);
        Log.e("Locale", language);
        format = new SimpleDateFormat("HH:mm dd MMM yyyy", locale);
        Date newDate = c.getTime();
        String date = format.format(newDate);
        String time = date.substring(0, 5);
        String dmy = date.substring(6);
        timeTextView.setText(time);
        dateTextView.setText(dmy);
    }


    public void loadMOD() {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject responseJson = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject json = new JSONObject();
                try {
                    json.put("function", "GetContent");
                    json.put("content_type_id", 5);
                    responseJson = handler.handleRequest(json);
                    if (responseJson != null)
                        return true;
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);

                if (aVoid) {
                    Log.e("MODResponse", responseJson.toString());
                    try {
                        JSONArray list = responseJson.getJSONArray("Content list");
                        Log.e("MODJSON", list.toString());
                        JSONObject json = list.getJSONObject(0);
                        JSONArray modJsonArray = json.getJSONArray("content");
                        ArrayList<Album> albums = new ArrayList<>();
                        for (int i = 0; i < modJsonArray.length(); i++) {
                            JSONObject albumJson = modJsonArray.getJSONObject(i);
                            JSONArray songJsonArray = albumJson.optJSONArray("item");
                            if (songJsonArray != null) {
                                ArrayList<Song> songs = new ArrayList<>();
                                for (int j = 0; j < songJsonArray.length(); j++) {
                                    JSONObject songJson = songJsonArray.getJSONObject(j);
                                    Song song = new Song(songJson.getInt("id"),
                                            songJson.getInt("parent_id"),
                                            songJson.getString("name"),
                                            API + songJson.getString("source"));
                                    songs.add(song);
                                }
                                Album album = new Album(albumJson.getInt("id"),
                                        albumJson.getString("name"),
                                        API + albumJson.getString("source"),
                                        API + albumJson.getString("img_source"), songs);
                                albums.add(album);
                            }
                        }
                        modFragment.setAlbums(albums);
                        modConnected = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    public void loadVOD() {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject responseJson = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject json = new JSONObject();
                try {
                    json.put("function", "GetContent");
                    json.put("content_type_id", 4);
                    responseJson = handler.handleRequest(json);
                    if (responseJson != null)
                        return true;
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);

                if (aVoid) {
                    Log.e("VODResponse", responseJson.toString());
                    try {
                        JSONArray list = responseJson.getJSONArray("Content list");
                        JSONObject json = list.getJSONObject(0);
                        JSONArray vodJsonArray = json.getJSONArray("content");
                        ArrayList<VOD> vods = new ArrayList<>();
                        for (int i = 0; i < vodJsonArray.length(); i++) {
                            JSONObject vodJson = vodJsonArray.getJSONObject(i);
                            VOD vod = new VOD(vodJson.getInt("id"),
                                    vodJson.getString("name"),
                                    API + vodJson.getString("source"),
                                    API + vodJson.getString("img_source"));
                            vods.add(vod);
                        }
                        vodFragment.setVods(vods);
                        vodConnected = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    public void loadChannels() {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject responseJson = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject json = new JSONObject();
                try {
                    json.put("function", "GetContent");
                    json.put("content_type_id", 1);
                    responseJson = handler.handleRequest(json);
                    if (responseJson != null) {
                        return true;
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid) {
                    Log.e("ChannelsResponse", responseJson.toString());
                    try {
                        tvFragment.channels = ChannelFactory.fromJSON(responseJson.getJSONArray("Content list"));
                        tvConnected = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    public void loadRadios() {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject responseJson = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject json = new JSONObject();
                try {
                    json.put("function", "GetContent");
                    json.put("content_type_id", 2);
                    responseJson = handler.handleRequest(json);
                    if (responseJson != null) {
                        return true;
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid) {
                    Log.e("RadiosResponse", responseJson.toString());
                    try {
                        radioFragment.channels = ChannelFactory.fromJSON(responseJson.getJSONArray("Content list"));
                        radCnnected = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    /*public void startInstallations() {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("com.mobilityware.spider.apk");
            out = new FileOutputStream(getExternalFilesDir(null).getPath() + "/com.mobilityware.spider.apk");

            byte[] buffer = new byte[1024];

            int read;
            while ((read = in.read(buffer)) != -1) {

                out.write(buffer, 0, read);

            }

            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

            Intent install = new Intent(Intent.ACTION_VIEW);

            install.setDataAndType(Uri.fromFile(new File(getExternalFilesDir(null).getPath() + "/com.mobilityware.spider.apk")),
                    "application/vnd.android.package-archive");

            startActivity(install);

        } catch (Exception e) {
        }
    }*/

    public class AppInstallReceiver extends BroadcastReceiver {
        public AppInstallReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();
            Log.e("Installations", packageName);
            String externalPath = context.getExternalFilesDir(null).getPath();
            File file;
            switch (packageName) {
                case "com.mobilityware.spider":
                    file = new File(externalPath + "/com.mobilityware.spider.apk");
                    file.delete();
                    gamesFragment.spiderSolitaireInstalled = true;
                    gamesFragment.switchIcon("Spider Solitaire");
                    break;
                case "com.rovio.angrybirds":
                    file = new File(externalPath + "/com.rovio.angrybirds.apk");
                    file.delete();
                    gamesFragment.angryBirdsInstalled = true;
                    gamesFragment.switchIcon("Angry Birds");
                    break;
                case "com.king.candycrushsaga":
                    file = new File(externalPath + "/candycrush.apk");
                    file.delete();
                    gamesFragment.candyCrushInstalled = true;
                    gamesFragment.switchIcon("Candy Crush");
                    break;
                case "com.android.chrome":
                    file = new File(externalPath + "/com.android.chrome.apk");
                    file.delete();
                    connectionFragment.chromeInstalled = true;
                    connectionFragment.switchIcon("Chrome");
                    connectionFragment.switchIcon("Facebook");
                    connectionFragment.switchIcon("Twitter");
                    break;
            }
        }
    }
}

