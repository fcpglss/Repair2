package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Administrator on 2016-11-29.
 */

public class AnnocementActivity extends AppCompatActivity {
    private int selected_img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment2);
        //  Bundle bundle =new Bundle();
        //  selected_img =bundle.getInt("img_id");
        Intent intent =new Intent();
        selected_img =intent.getIntExtra("img_id",500);
        Log.d("app","selected_img="+intent.getIntExtra("img_id",500));
    }
}
