package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import application.MyApplication;
import imagehodler.ImageLoader;
import model.Announcement;
import repari.com.adapter.AnnocmentListAdapter;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Administrator on 2016-11-29.
 */

public class AnnocementActivity extends AppCompatActivity {


    ListView lvAnnocment;
    List<Announcement> list;
    AnnocmentListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annocement);

        list = (List<Announcement>) getIntent().getSerializableExtra("list");

        adapter = new AnnocmentListAdapter(list,this);
        lvAnnocment = (ListView) findViewById(R.id.lv_annocement_list);
        lvAnnocment.setAdapter(adapter);


    }
}
