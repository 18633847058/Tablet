package com.yang.nav.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yang.nav.R;
import com.yang.nav.model.PointManager;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Yang on 2016/12/14.
 */

public class DialogUtils {


    private static AlertDialog.Builder loadingBuilder;
    private static AlertDialog loadingDialog;

    /**
     * 显示正在加载的对话框
     *
     * @param activity
     */
    public static void showLoadingDialog(final Activity activity,String string) {
        if (!activity.isFinishing()) {
            loadingBuilder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_process, null);
            TextView text = (TextView) view.findViewById(R.id.tv_process);
            text.setText(string);
            loadingBuilder.setView(view);
            loadingBuilder.setCancelable(true);//点击非对话框关闭
            loadingDialog = loadingBuilder.create();
            loadingDialog.setCanceledOnTouchOutside(false);//点击非对话框关闭取消
            loadingDialog.show();
        }
    }

    /**
     * 关闭正在加载的对话框
     */
    public static void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
    public static void showAlertDialog(final Context context, final String file, final PointManager pointManager){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认导入该文件？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MyAsyncTask myAsyncTask = new MyAsyncTask(context,pointManager);
                myAsyncTask.setFile(file);
                myAsyncTask.execute(MyAsyncTask.Type.IMPORT);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();}
        });
        builder.create().show();
    }
}
