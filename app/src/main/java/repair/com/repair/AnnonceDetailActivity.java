package repair.com.repair;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import model.Announcement;

/**
 * Created by hsp on 2017/4/20.
 */

public class AnnonceDetailActivity extends AppCompatActivity{


    private Announcement annoucement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_layout);
        initData();
    }

    private void initData() {
        annoucement= (Announcement) getIntent().getSerializableExtra("annoucement");
    }


}
