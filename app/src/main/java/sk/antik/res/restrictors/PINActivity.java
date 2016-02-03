package sk.antik.res.restrictors;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

import sk.antik.res.PINButton;
import sk.antik.res.R;

public class PINActivity extends Activity {

    private ArrayList<EditText> fields = new ArrayList<>();
    private ArrayList<PINButton> buttons = new ArrayList<>();
    private int currentEt = 0;
    private SharedPreferences prefs;
    private String userPinKey = "antik.com.antikscanner.user.pin";
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pin);
        final Intent intent = getIntent();
        final String mode = intent.getStringExtra("MODE");
        EditText et0 = (EditText) findViewById(R.id.EditText0);
        EditText et1 = (EditText) findViewById(R.id.EditText1);
        EditText et2 = (EditText) findViewById(R.id.EditText2);
        EditText et3 = (EditText) findViewById(R.id.EditText3);

        fields.add(et0);
        fields.add(et1);
        fields.add(et2);
        fields.add(et3);

        PINButton pinButton0 = (PINButton) findViewById(R.id.button_num_0);
        PINButton pinButton1 = (PINButton) findViewById(R.id.button_num_1);
        PINButton pinButton2 = (PINButton) findViewById(R.id.button_num_2);
        PINButton pinButton3 = (PINButton) findViewById(R.id.button_num_3);
        PINButton pinButton4 = (PINButton) findViewById(R.id.button_num_4);
        PINButton pinButton5 = (PINButton) findViewById(R.id.button_num_5);
        PINButton pinButton6 = (PINButton) findViewById(R.id.button_num_6);
        PINButton pinButton7 = (PINButton) findViewById(R.id.button_num_7);
        PINButton pinButton8 = (PINButton) findViewById(R.id.button_num_8);
        PINButton pinButton9 = (PINButton) findViewById(R.id.button_num_9);
        PINButton pinButtonDel = (PINButton) findViewById(R.id.button_num_del);
        pinButtonDel.setNumber("Delete");

        buttons.add(pinButton0);
        buttons.add(pinButton1);
        buttons.add(pinButton2);
        buttons.add(pinButton3);
        buttons.add(pinButton4);
        buttons.add(pinButton5);
        buttons.add(pinButton6);
        buttons.add(pinButton7);
        buttons.add(pinButton8);
        buttons.add(pinButton9);
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setNumber("" + i);
        }
        ;
        buttons.add(pinButtonDel);
        for (int i = 0; i < buttons.size() - 2; i++) {
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PINButton btn = (PINButton) v;
                    Log.e("Cislo", btn.getText().toString());
                    if (currentEt == 0) {
                        btn.putText(fields.get(0));
                        currentEt++;
                    } else {
                        if (currentEt == 1) {
                            btn.putText(fields.get(1));
                            currentEt++;

                        } else {
                            if (currentEt == 2) {
                                btn.putText(fields.get(2));
                                currentEt++;
                            } else {
                                if (currentEt == 3) {
                                    fields.get(3).setText(btn.getText());
                                    pin = "";
                                    for (EditText et : fields) {
                                        pin = pin + et.getText().toString();
                                    }
                                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                                    startActivity(launchIntent);
                                    finish();
                                }
                            }
                        }
                    }
                }
            });
        }

        pinButtonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PINButton btn = (PINButton) v;
                if (currentEt == 0) {
                    btn.deleteText(fields.get(currentEt));
                } else {
                    if (currentEt == 3 && fields.get(3).getText().toString().equalsIgnoreCase("")) {
                        currentEt--;
                        btn.deleteText(fields.get(currentEt));
                    } else {
                        if (currentEt == 3) {
                            btn.deleteText(fields.get(currentEt));
                        } else {
                            currentEt--;
                            btn.deleteText(fields.get(currentEt));
                        }

                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}


