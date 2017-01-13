package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import application.MyApplication;
import imagehodler.ImageLoader;
import model.Announcement;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Administrator on 2016-11-29.
 */

public class AnnocementActivity extends AppCompatActivity {


    private ImageView iv_image;
    private TextView tv_title,tv_time,tv_content;
    private int selected_img;
    private Announcement announcement=null;
    private ImageLoader mimageLoader=ImageLoader.build(MyApplication.getContext());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annocement);

        iv_image = (ImageView) findViewById(R.id.iv_annocement_image);
        tv_title = (TextView) findViewById(R.id.tv_annocement_title);
        tv_time = (TextView) findViewById(R.id.tv_annocement_time);
        tv_content = (TextView) findViewById(R.id.tv_annocement_content);




        announcement= (Announcement) getIntent().getSerializableExtra("announcementitem");
        mimageLoader.bindBitmap(announcement.getImage_url(),iv_image);

        tv_title.setText(announcement.getTitle());
        String[] s = announcement.getCreate_at().split(" ");
        tv_time.setText(s[0]);
        tv_content.setText(announcement.getContent());

    }
}
