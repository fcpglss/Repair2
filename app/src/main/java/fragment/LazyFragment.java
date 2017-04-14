package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by hsp on 2017/4/14.
 */

public abstract class LazyFragment extends Fragment {

    private static final String TAG = "LazyFragment";
    //Frament的View加载完毕标记
    private boolean  isViewCreated;
    
    //Fragment对用户可见的标记
    private boolean isUIVisible;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated=true;
        lazyLoad();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        //isVisibleToUser这个boolean值表示：该Fragment的UI 是否可见
        if(isVisibleToUser)
        {
            isUIVisible=true;
            lazyLoad();
        }
        else 
        {
            isUIVisible=false;
        }
    }

    //懒加载需要双重标记通过后，才执行加载数据的方法
    private void lazyLoad() {
        if(isViewCreated&&isUIVisible)
        {
            loadData();
            //数据加载完毕之后,需要恢复标志位,防止重复加载
            isViewCreated=false;
            isUIVisible=false;
        }
    }

    protected abstract void loadData();
}
