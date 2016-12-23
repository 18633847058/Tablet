package com.yang.nav.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.yang.nav.model.PointManager;
import com.yang.nav.model.entity.Point;
import com.yang.nav.view.activity.MapViewActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Yang on 2016/12/22.
 */

public class MyAsyncTask extends AsyncTask <MyAsyncTask.Type,Integer,MyAsyncTask.Message> {

    private WeakReference<Context> weakReference;
    private PointManager pointManager;
    private String startTime;
    private String endTime;
    private String file;
    Type type;
    private List<Point> points;

//    public interface OnQueryListener{
//        void onQuery(List<Point> list);
//    }
    public enum Type {
        DELETE, REVIEW, EXPORT, IMPORT, INSERT
    }
    public enum Message {
        SUCCESS, FAILURE, NULL,
    }

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
            case DELETE:
                if(m == Message.SUCCESS){
                    ToastUtils.showToast(weakReference.get(),"删除成功！");
                }else {
                    ToastUtils.showToast(weakReference.get(),"删除失败！");
                }
                break;
            case EXPORT:
                if (m == Message.NULL){
                    ToastUtils.showToast(weakReference.get(),"查询结果为空！");
                }else if(m == Message.SUCCESS){
                    ToastUtils.showToast(weakReference.get(),"导出成功！");
                }else{
                    ToastUtils.showToast(weakReference.get(),"出现错误导出失败！");
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
            case REVIEW:
                if (m == Message.NULL){
                    ToastUtils.showToast(weakReference.get(),"查询数据为空！");
                }else if(m == Message.SUCCESS) {
                    ToastUtils.showToast(weakReference.get(), "数据查询到，正在转入地图！");
                    weakReference.get().startActivity(new Intent(weakReference.get(), MapViewActivity.class));
                }
                break;
        }
    }


    private Message worker(MyAsyncTask.Type type) {
        Message message = Message.FAILURE;
        switch (type){
            case DELETE:
                if(pointManager.deletePointsByTime(startTime,endTime)){
                   message = Message.SUCCESS;
                }
                break;
            case EXPORT:
                points = pointManager.getPointsByTime(startTime,endTime);
                if (points != null) {
                    XmlUtils.XmlFileCreator(points);
                    message = Message.SUCCESS;
                }else {
                    message = Message.NULL;
                }
                break;
            case IMPORT:
                points = XmlUtils.parseXml(file);
                if (points == null){
                    message = Message.NULL;
                    break;
                }
                if(pointManager.insertMultObject(points)){
                    message = Message.SUCCESS;
                }else{
                    message = Message.FAILURE;
                }
                break;
            case INSERT:

                break;
            case REVIEW:
                points = pointManager.getPointsByTime(startTime,endTime);
                if (points != null) {
                    message = Message.SUCCESS;
                }else {
                    message = Message.NULL;
                }
                break;
        }
        return message;
    }
}
