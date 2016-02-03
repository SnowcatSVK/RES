package sk.antik.res;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import sk.antik.res.loader.AppLoader;
import sk.antik.res.loader.AppModel;
import sk.antik.res.logic.Channel;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    private LiveTVFragment tvFragment;
    private RadioFragment radioFragment;
    private VODFragment vodFragment;
    private SettingFragment settingsFragment;
    private MODFragment modFragment;

    //private RelativeLayout rootLayout;
    private LinearLayout topBar;
    private LinearLayout bottomBarIcons;
    private LinearLayout bottomBarDescription;
    private boolean isTVFullscreen = false;
    private ArrayList<ImageButton> imageButtons;
    private TextView timeTextView;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(0, null, this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
        Log.e("Setting test", "onCreate - Main");
        tvFragment = new LiveTVFragment();
        radioFragment = new RadioFragment();
        vodFragment = new VODFragment();
        settingsFragment = new SettingFragment();
        modFragment = new MODFragment();
        topBar = (LinearLayout) findViewById(R.id.top_bar_linearLayout);
        bottomBarIcons = (LinearLayout) findViewById(R.id.bottom_bar_linearLayout);
        bottomBarDescription = (LinearLayout) findViewById(R.id.categories_names_layout);
        //rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        //getFragmentManager().beginTransaction().add(R.id.root_layout, tvFragment).commit();
        imageButtons = new ArrayList<>();
        imageButtons.add((ImageButton) findViewById(R.id.watch_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.radio_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.connect_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.games_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.vod_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.mod_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.voyage_imageButton));
        imageButtons.add((ImageButton) findViewById(R.id.setting_imageButton));
        timeTextView = (TextView) findViewById(R.id.time_main_activity_textView);
        dateTextView = (TextView) findViewById(R.id.date_main_activity_textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (!isMyServiceRunning(AppKillerService.class)) {
            Intent intent = new Intent(this, AppKillerService.class);
            startService(intent);
        }
        setupTime();
        registerReceiver();
        //AppKillerService.startingForbiddenApp = false;
    }

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
            Log.e("App package", "Name: " + app.getLabel() + ", package name: " + app.getApplicationPackageName());
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
        }
    }

    public void onTVClick(View view) {

    }

    public void onWatchButtonClick(View view) {
        getFragmentManager().beginTransaction().replace(R.id.root_layout, tvFragment).commit();
        setBackgrounds(view);
    }

    public void onRadioButtonClick(View view) {
        setBackgrounds(view);
        getFragmentManager().beginTransaction().replace(R.id.root_layout, radioFragment).commit();
        tvFragment.releasePlayer();
    }

    public void onConnectButtonClick(View view) {
        setBackgrounds(view);
    }

    public void onGamesButtonClick(View view) {
        setBackgrounds(view);
    }

    public void onVODButtonClick(View view) {
        setBackgrounds(view);
        getFragmentManager().beginTransaction().replace(R.id.root_layout, vodFragment).commit();
    }

    public void onMODButtonClick(View view) {
        setBackgrounds(view);
        getFragmentManager().beginTransaction().replace(R.id.root_layout, modFragment).commit();

    }

    public void onVoyageButtonClick(View view) {
        setBackgrounds(view);
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
    }

    public void setupTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format; //= new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy");
        format = new SimpleDateFormat("HH:mm dd MMM yyyy");
        Date newDate = c.getTime();
        String date = format.format(newDate);
        String time = date.substring(0, 5);
        String dmy = date.substring(6);
        timeTextView.setText(time);
        dateTextView.setText(dmy);
    }
}
