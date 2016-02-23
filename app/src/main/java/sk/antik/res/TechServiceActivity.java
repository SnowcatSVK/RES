package sk.antik.res;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class TechServiceActivity extends Activity {

    private EditText ipAddrEditText;
    private EditText seatNumberEditText;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_tech_service);

        prefs = getSharedPreferences("sk.antik.res", MODE_PRIVATE);
        ipAddrEditText = (EditText) findViewById(R.id.device_ip_editText);
        seatNumberEditText = (EditText) findViewById(R.id.seat_number_editText);
    }

    public void onCofirmButtonClick(View view) {
        String ip = ipAddrEditText.getText().toString();

        String ipAddr = "http://" + ip;
        String seatNumber = seatNumberEditText.getText().toString();
        prefs.edit().putString("API_IP", ipAddr).putString("SEAT_No", seatNumber).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {

    }
}
