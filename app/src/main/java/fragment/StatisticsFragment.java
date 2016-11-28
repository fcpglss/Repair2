package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import repair.com.repair.R;

/**
 * Created by hsp on 2016/11/27.
 */

public class StatisticsFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        Log.d("MainFragment","Statistic_onCreateView");
        return inflater.inflate(R.layout.fragment3, null);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MainFragment", "Statisc_onAttach");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainFragment", "Statisc_onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MainFragment", "Statisc_onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainFragment", "Statisc_onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainFragment", "Statistics_onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainFragment", "Statistics_onStart");
    }

}
