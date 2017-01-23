package com.yang.nav.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.yang.nav.model.PointManager;
import com.yang.nav.model.entity.Point;
import com.yang.nav.view.activity.DataManagerActivity;
import com.yang.nav.view.activity.TrackReviewActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.yang.nav.view.activity.DataManagerActivity.points;

/**
 * Created by Yang on 2016/12/22.
 */

public class MyAsyncTask extends AsyncTask <MyAsyncTask.Type,Integer,MyAsyncTask.Message> {

    Type type;
    private WeakReference<Context> weakReference;
    private PointManager pointManager;
    private String startTime;
    private String endTime;
    private String file;
    private DataManagerActivity activity;

    public MyAsyncTask(Context context,PointManager pointManager) {
        weakReference = new WeakReference<>(context);
        this.pointManager = pointManager;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (weakReference != null && weakReference.get() != null) {
            DialogUtils.showLoadingDialog((Activity) weakReference.get(), "正在处理");
        }
    }

    @Override
    protected Message doInBackground(Type... params) {
        type = params[0];
        return worker(type);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Message m) {
        super.onPostExecute(m);
        DialogUtils.hideLoadingDialog();
        switch (type){
            case SELECT:
                if (m == Message.SUCCESS) {
                    ToastUtils.showToast(weakReference.get(), "查找成功！");
                    activity = (DataManagerActivity) weakReference.get();
                    activity.setDealVisibility(true, points.size());
                } else {
                    ToastUtils.showToast(weakReference.get(), "查找失败！");
                }
                break;
            case DELETE:
                if(m == Message.SUCCESS){
                    ToastUtils.showToast(weakReference.get(),"删除成功！");
                }else {
                    ToastUtils.showToast(weakReference.get(),"删除失败！");
                }
                break;
            case EXPORT:
                if (m == Message.SUCCESS) {
                    ToastUtils.showToast(weakReference.get(),"导出成功！");
                }else{
                    ToastUtils.showToast(weakReference.get(), "导出失败！");
                }
                break;
            case REVIEW:
                if (m == Message.SUCCESS) {
                    ToastUtils.showToast(weakReference.get().getApplicationContext(), "数据传输中，正在转入地图！");
                    Intent intent = new Intent(weakReference.get(), TrackReviewActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelableArrayList("points", points);
//                    intent.putExtras(bundle);
                    weakReference.get().startActivity(intent);
                }
                break;
            case IMPORT:
                if (m == Message.NULL){
                    ToastUtils.showToast(weakReference.get(),"导入文件错误！");
                }else if(m == Message.SUCCESS){
                    ToastUtils.showToast(weakReference.get(),"导入成功！");
                }else{
                    ToastUtils.showToast(weakReference.get(),"写入数据库时出现错误！");
                }
                break;
            case INSERT:

                break;

        }
    }

    private Message worker(MyAsyncTask.Type type) {
        Message message = Message.FAILURE;
        switch (type){
            case SELECT:
                DataManagerActivity.points = (ArrayList<Point>) pointManager.getPointsByTime(startTime, endTime);
                if (points == null) {
                    message = Message.NULL;
                } else {
                    message = Message.SUCCESS;
                }
                break;
            case DELETE:
                if (points != null) {
                    if (pointManager.deleteMultObject(points, Point.class)) {
                        message = Message.SUCCESS;
                    }
                }
                break;
            case EXPORT:
                if (points != null) {
                    XmlUtils.XmlFileCreator(points);
                    message = Message.SUCCESS;
                }
                break;

            case INSERT:

                break;
            case REVIEW:
                if (points != null) {
                    message = Message.SUCCESS;
                }
                break;




            case IMPORT:
                points = (ArrayList<Point>) XmlUtils.parseXml(file);
                if (points == null){
                    message = Message.NULL;
                    break;
                }
                if(pointManager.insertMultObject(points)){
                    message = Message.SUCCESS;
                    points = null;
                }else{
                    message = Message.FAILURE;
                    points = null;
                }
                break;
        }
        return message;
    }

    public enum Type {
        DELETE, REVIEW, EXPORT, IMPORT, INSERT, SELECT
    }


    public enum Message {
        SUCCESS, FAILURE, NULL,
    }
}
