package sk.antik.res;

import android.os.Bundle;
import android.app.Activity;

public class VODPlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);

        String source = getIntent().getStringExtra("VODSource");
    }


}