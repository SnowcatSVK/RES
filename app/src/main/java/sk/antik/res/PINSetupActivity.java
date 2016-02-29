package sk.antik.res;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import sk.antik.res.io.SHA_256;

public class PINSetupActivity extends Activity {

    private EditText pinEditText;
    private EditText repeatPinEditText;
    private CheckBox pinCheckBox;
    private CheckBox repeatPinCheckBox;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinsetup);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        pinEditText = (EditText) findViewById(R.id.pin_editText);
        repeatPinEditText = (EditText) findViewById(R.id.repeat_pin_editText);
        pinCheckBox = (CheckBox) findViewById(R.id.pin_checkBox);
        repeatPinCheckBox = (CheckBox) findViewById(R.id.repeat_pin_checkBox);
        prefs = getSharedPreferences("sk.antik.res", MODE_PRIVATE);

        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    pinCheckBox.setChecked(true);
                } else {
                    pinCheckBox.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        repeatPinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4 && s.toString().equalsIgnoreCase(pinEditText.getText().toString())) {
                    repeatPinCheckBox.setChecked(true);
                } else {
                    repeatPinCheckBox.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onCofirmButtonClick(View view) {
        if (pinEditText.getText().toString().equalsIgnoreCase(repeatPinEditText.getText().toString())) {
            prefs.edit().putString("PIN", SHA_256.getHashString(repeatPinEditText.getText().toString())).apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("FROM_SETUP", true);
            startActivity(intent);
            finish();
        }
    }
}
