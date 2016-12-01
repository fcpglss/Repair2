package repair.com.repair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import model.Test2;

/**
 * Created by hsp on 2016/12/1.
 */

public class DetailsActivity extends AppCompatActivity {

    private Test2 applys;

    private TextView textveiew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        applys= (Test2) getIntent().getSerializableExtra("applys");
        textveiew= (TextView) findViewById(R.id.tv_details);
        textveiew.setText(applys.getA_name());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
