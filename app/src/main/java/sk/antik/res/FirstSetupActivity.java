package sk.antik.res;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class FirstSetupActivity extends Activity {

    private SharedPreferences prefs = null;
    private EditText ipAddrEditText;
    private EditText seatNumberEditText;
    //private EditText busPlateEditText;
    private CheckBox englishCheckbox;
    private CheckBox turkishCheckBox;
    private CheckBox chineseCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_first_setup);
        prefs = getSharedPreferences("sk.antik.res", MODE_PRIVATE);
        if (!prefs.getBoolean("FIRST_START", true)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("FROM_SETUP", false);
            startActivity(intent);
            finish();
        }
        ipAddrEditText = (EditText) findViewById(R.id.device_ip_editText);
        seatNumberEditText = (EditText) findViewById(R.id.seat_number_editText);
        //busPlateEditText = (EditText) findViewById(R.id.bus_plate_editText);
        englishCheckbox = (CheckBox) findViewById(R.id.english_language_checkBox);
        turkishCheckBox = (CheckBox) findViewById(R.id.turkish_language_checkBox);
        chineseCheckBox = (CheckBox) findViewById(R.id.chinese_language_checkBox);

        Resources res = getResources();
        android.content.res.Configuration conf = res.getConfiguration();
        Log.e("Locale", conf.locale.getCountry());
        if (conf.locale.getCountry().equalsIgnoreCase("tr")) {
            turkishCheckBox.setChecked(true);
        } else {
            if (conf.locale.getCountry().equalsIgnoreCase("zh")) {
                chineseCheckBox.setChecked(true);
            } else {
                englishCheckbox.setChecked(true);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        englishCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (englishCheckbox.isChecked()) {
                    turkishCheckBox.setChecked(false);
                    chineseCheckBox.setChecked(false);
                    prefs.edit().putString("APP_LANGUAGE", "us").apply();
                    Resources res = getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    if (!conf.locale.getCountry().equalsIgnoreCase("us")) {
                        conf.locale = new Locale("us");
                        res.updateConfiguration(conf, dm);
                        recreate();
                    }
                }
            }

        });

        turkishCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (turkishCheckBox.isChecked()) {
                    englishCheckbox.setChecked(false);
                    chineseCheckBox.setChecked(false);
                    prefs.edit().putString("APP_LANGUAGE", "tr").apply();
                    Resources res = getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    if (!conf.locale.getCountry().equalsIgnoreCase("tr")) {
                        conf.locale = new Locale("tr");
                        res.updateConfiguration(conf, dm);
                        recreate();

                    }
                }
            }
        });

        chineseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chineseCheckBox.isChecked()) {
                    englishCheckbox.setChecked(false);
                    turkishCheckBox.setChecked(false);
                    prefs.edit().putString("APP_LANGUAGE", "zh").apply();
                    Resources res = getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    if (!conf.locale.getCountry().equalsIgnoreCase("zh")) {
                        conf.locale = new Locale("zh");
                        res.updateConfiguration(conf, dm);
                        recreate();

                    }
                }
            }
        });
    }

    public void onCofirmButtonClick(View view) {
        if (ipAddrEditText.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.toast_API_IP_missing), Toast.LENGTH_LONG).show();
        } else if (seatNumberEditText.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.toast_seat_number_missing), Toast.LENGTH_LONG).show();
        } /*else if (busPlateEditText.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.bus_plate_missing), Toast.LENGTH_LONG).show();
        }*/ else {
            String ip = ipAddrEditText.getText().toString();

            if (ip.contains("-") || ip.contains("/")) {
                Toast.makeText(this, "API IP Address contains wrong symbols.", Toast.LENGTH_LONG).show();
            } else {
                String ipAddr = "http://" + ip;
                String seatNumber = seatNumberEditText.getText().toString();
                prefs.edit().putString("API_IP", ipAddr).putString("SEAT_No", seatNumber).putBoolean("FIRST_START", false)
                        /*.putString("BUS_PLATE", busPlateEditText.getText().toString())*/.apply();
                Intent intent = new Intent(this, PINSetupActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("RESTARTED", true);
    }
}
