package util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import fragment.ResetVisable;
import repair.com.repair.R;
import repari.com.adapter.DialogAdapter;

import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;

/**
 * Created by hsp on 2017/4/21.
 */

public class DialogUtil {

    public static DialogPlusBuilder getDialogBuilder(Context context, DialogAdapter dialogAdapter, int layout,final ResetVisable reset) {


        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(context)
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(layout)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        reset.resetVisible();
                    }
                })
                .setContentWidth((int) (windowWitch / 1.5))
                .setExpanded(true, (int) (windowHeigth / 1.5));

        return dialogPlusBuilder;

    }


}
