package com.yang.nav.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Yang on 2016/12/14.
 */

public class ToastUtils {

    //弹出吐司
    public static void showToast(Context ctx, int id, String str) {
        if (str == null) {
            return;
        }

        Toast toast = Toast.makeText(ctx, ctx.getString(id) + str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context ctx, String errInfo) {
        if (errInfo == null) {
            return;
        }

        Toast toast = Toast.makeText(ctx, errInfo, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
