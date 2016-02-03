package sk.antik.res;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Snowcat on 14.01.2016.
 */
public class PINButton extends Button {
    private String number;

    public PINButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void putText(EditText et1) {
        et1.setText(number);
    }

    public void deleteText(EditText et1) {
        et1.setText("");
    }

}
