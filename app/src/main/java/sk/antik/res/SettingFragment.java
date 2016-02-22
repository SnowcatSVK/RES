package sk.antik.res;


import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Locale;

import sk.antik.res.logic.Language;
import sk.antik.res.logic.LanguageAdapter;
import sk.antik.res.logic.Setting;
import sk.antik.res.logic.SettingsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private ListView settingsListView;
    private ListView languagesListView;
    private int selectedPosition;
    private boolean activityRestarted;
    private SeekBar volumeSeekbar = null;
    private SeekBar brightnessSeekBar = null;
    private AudioManager audioManager = null;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        // Inflate the layout for this fragment
        settingsListView = (ListView) rootView.findViewById(R.id.settings_listView);
        languagesListView = (ListView) rootView.findViewById(R.id.languages_listView);
        volumeSeekbar = (SeekBar) rootView.findViewById(R.id.volume_settings_seekBar);
        brightnessSeekBar = (SeekBar) rootView.findViewById(R.id.brightness_settings_seekBar);
        final ArrayList<Setting> settings = new ArrayList<>();
        settings.add(new Setting(getString(R.string.setting_language), getActivity().getResources().getDrawable(R.drawable.ic_translate), getActivity().getResources().getDrawable(R.drawable.ic_translate_white)));
        settings.add(new Setting(getString(R.string.setting_volume), getActivity().getResources().getDrawable(R.drawable.ic_volume), getActivity().getResources().getDrawable(R.drawable.ic_volume_white)));
        settings.add(new Setting(getString(R.string.setting_brightness), getActivity().getResources().getDrawable(R.drawable.ic_brightness), getActivity().getResources().getDrawable(R.drawable.ic_brightness_white)));
        settings.add(new Setting(getString(R.string.settings_tech_service), getActivity().getResources().getDrawable(R.drawable.ic_tech_service), getActivity().getResources().getDrawable(R.drawable.ic_tech_service_white)));
        final SettingsAdapter adapter = new SettingsAdapter(getActivity(), settings);
        settingsListView.setAdapter(adapter);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Setting setting : settings) {
                    setting.selected = false;
                }
                settings.get(position).selected = true;
                adapter.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        brightnessSeekBar.setVisibility(View.GONE);
                        volumeSeekbar.setVisibility(View.GONE);
                        languagesListView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        languagesListView.setVisibility(View.GONE);
                        brightnessSeekBar.setVisibility(View.GONE);
                        volumeSeekbar.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        languagesListView.setVisibility(View.GONE);
                        brightnessSeekBar.setVisibility(View.VISIBLE);
                        volumeSeekbar.setVisibility(View.GONE);
                }
            }
        });
        final ArrayList<Language> languages = new ArrayList<>();
        languages.add(new Language("English", "en"));
        languages.add(new Language("Türkçe", "tr"));
        /*
        languages.add(new Language("Deutsch", "de"));
        languages.add(new Language("Suomi", "fn"));
        languages.add(new Language("Pусский", "ru"));
        languages.add(new Language("中国的", "cn"));
        */
        final LanguageAdapter languageAdapter = new LanguageAdapter(getActivity(), languages);
        languagesListView.setAdapter(languageAdapter);
        languagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Language language : languages) {
                    language.selected = false;
                }

                languages.get(position).selected = true;
                languageAdapter.notifyDataSetChanged();
                Resources res = getActivity().getResources();
                // Change locale settings in the app.
                DisplayMetrics dm = res.getDisplayMetrics();
                android.content.res.Configuration conf = res.getConfiguration();
                conf.locale = new Locale(languages.get(position).countryCode.toLowerCase());
                res.updateConfiguration(conf, dm);
                activityRestarted = true;
                selectedPosition = position;
                getActivity().recreate();
            }
        });
        if (savedInstanceState != null) {
            settings.get(0).selected = true;
            adapter.notifyDataSetChanged();
            selectedPosition = savedInstanceState.getInt("selected_language");
            languages.get(selectedPosition).selected = true;
            languageAdapter.notifyDataSetChanged();
            languagesListView.setVisibility(View.VISIBLE);

        }
        setControls();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_language",selectedPosition);
    }

    public void setControls() {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumeSeekbar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        volumeSeekbar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_SYSTEM));


        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        brightnessSeekBar.setMax(255);
        brightnessSeekBar.setProgress((int) (lp.screenBrightness*255));
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.screenBrightness = progress / (float)255;
                getActivity().getWindow().setAttributes(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
