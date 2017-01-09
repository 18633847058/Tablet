package com.yang.nav.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.yang.nav.R;
import com.yang.nav.model.PointManager;
import com.yang.nav.utils.DialogUtils;
import com.yang.nav.utils.MyAsyncTask;
import com.yang.nav.utils.TimeUtils;
import com.yang.nav.utils.ToastUtils;

import java.text.SimpleDateFormat;


public class DataManagerActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener {

    private static final int REC_REQUEST_CODE = 1;
    private TimePickerDialog startTime;
    private TimePickerDialog endTime;
    private EditText et_start;
    private EditText et_end;
    private Long start;
    private Long end;
    private MyAsyncTask myAsyncTask;
    private PointManager pointManager;
    public SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manager);
        init();
        initView();
    }

    private void init(){
        pointManager = PointManager.getInstance(getApplication());
    }
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button btn_data_delete = (Button) findViewById(R.id.btn_data_delete);
        Button btn_data_export = (Button) findViewById(R.id.btn_data_export);
        Button btn_data_import = (Button) findViewById(R.id.btn_data_import);
        Button btn_data_review = (Button) findViewById(R.id.btn_data_review);
        btn_data_delete.setOnClickListener(this);
        btn_data_export.setOnClickListener(this);
        btn_data_import.setOnClickListener(this);
        btn_data_review.setOnClickListener(this);
        et_start = (EditText) findViewById(R.id.et_start);
        et_end = (EditText) findViewById(R.id.et_end);
        et_end.setOnClickListener(this);
        et_start.setOnClickListener(this);
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        startTime = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setTitleStringId("选择开始时间")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - tenYears)
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextSize(20)
                .build();
        endTime = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setTitleStringId("选择结束时间")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - tenYears)
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextSize(20)
                .build();
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_data_delete:
                dataManager(MyAsyncTask.Type.DELETE);
                break;
            case R.id.btn_data_export:
                dataManager(MyAsyncTask.Type.EXPORT);
                break;
            case R.id.btn_data_import:
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,REC_REQUEST_CODE);
                break;
            case R.id.btn_data_review:
                dataManager(MyAsyncTask.Type.REVIEW);
                break;
            case R.id.et_start:
                startTime.show(getSupportFragmentManager(),"start");
                break;
            case R.id.et_end:
                endTime.show(getSupportFragmentManager(),"end");
               break;
        }
    }

    private void dataManager(MyAsyncTask.Type type) {
        if(et_start.getText().toString().equals("")||et_end.getText().toString().equals("")){
            ToastUtils.showToast(DataManagerActivity.this,"开始时间或结束时间未设置！");
            return;
        }
        myAsyncTask = new MyAsyncTask(DataManagerActivity.this,pointManager);
        myAsyncTask.setStartTime(et_start.getText().toString());
        myAsyncTask.setEndTime(et_end.getText().toString());
        myAsyncTask.execute(type);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long seconds) {
        switch(timePickerView.getTag()){
            case "start":
                String start = TimeUtils.convertToNormalStr(seconds);
                et_start.setText(start);
                break;
            case "end":
                String end = TimeUtils.convertToNormalStr(seconds);
                et_end.setText(end);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REC_REQUEST_CODE){
            Uri uri = data.getData();
//            file:///storage/emulated/0/mapbar/app/test.xml
//            ToastUtils.showToast(DataManagerActivity.this, uri.toString());
            DialogUtils.showAlertDialog(DataManagerActivity.this,uri.toString(),pointManager);
        }
    }
}
