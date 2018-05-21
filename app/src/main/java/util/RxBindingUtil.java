package util;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import repair.com.repair.R;

/**
 * Created by hsp on 2017/4/25.
 */

public class RxBindingUtil {
    public static void setClickThrottleFirst(View view, int time) {
        RxView.clicks(view)
                .throttleFirst(time, TimeUnit.SECONDS)
                .subscribe();
    }

    public static void changColorAndVisable(final EditText editText, final LinearLayout line, final ImageView imageView) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    line.setBackgroundResource(R.color.ColorDarkPrimaryColor);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    line.setBackgroundResource(R.color.ColorDividerColor);
                    imageView.setVisibility(View.GONE);
                }
            }
        });
    }


    public static void setClearText(final View view, final EditText editText) {
        try {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editText.setText("");
                }
            });
        } catch (Exception e) {
            return;
        }
    }


    public static void changColorAndVisable(final EditText editText, final LinearLayout line) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    line.setBackgroundResource(R.color.ColorDarkPrimaryColor);

                } else {
                    line.setBackgroundResource(R.color.ColorDividerColor);
                }
            }
        });
    }
}
