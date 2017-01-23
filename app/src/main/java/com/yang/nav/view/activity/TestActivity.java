package com.yang.nav.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.yang.nav.R;
import com.yang.nav.model.PointManager;
import com.yang.nav.model.entity.Frame;
import com.yang.nav.model.entity.Point;
import com.yang.nav.utils.DialogUtils;
import com.yang.nav.utils.TimeUtils;
import com.yang.nav.utils.ToastUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class TestActivity extends AppCompatActivity implements OnDateSetListener, View.OnClickListener {

    public static final String F = "yyyy-MM-dd HH:mm";
    public static final String KEY = "jxz236-20141118-02-L-F-A11100";
    // 调试时所使用的输出信息
    private final static String TAG = "TestActivity";
    //应用跟目录
    private static String mAppPath = null;
    //应用名
    private static String mAppName = null;
    //dpi
    private static int mDensityDpi = 0;
    private ListView lv_points;
    private ArrayAdapter<Frame> arrayAdapter;
    private PointManager pointManager;
    private EditText et_start;
    private EditText et_end;
    private TimePickerDialog startTime;
    private TimePickerDialog endTime;
    private Long start;
    private Long end;
    private List<Frame> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        Button btn_dialog = (Button) findViewById(R.id.btn_dialog);
        Button btn_map = (Button) findViewById(R.id.btn_map);
        Button btn_insert = (Button) findViewById(R.id.btn_insert);
        Button btn_query = (Button) findViewById(R.id.btn_query);
        Button btn_export = (Button) findViewById(R.id.btn_export);
        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        Button btn_time = (Button) findViewById(R.id.btn_time);
        et_start = (EditText) findViewById(R.id.et_start);
        et_end = (EditText) findViewById(R.id.et_end);
        et_start.setOnClickListener(this);
        et_end.setOnClickListener(this);
        et_start.setFocusable(false);
        et_end.setFocusable(false);
        btn_dialog.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_insert.setOnClickListener(this);
        btn_query.setOnClickListener(this);
        btn_export.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_time.setOnClickListener(this);
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
                .setWheelItemTextSize(12)
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
                .setWheelItemTextSize(12)
                .build();
        lv_points = (ListView) findViewById(R.id.lv_points);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        lv_points.setAdapter(arrayAdapter);
        pointManager = PointManager.getInstance(getApplication());
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long seconds) {

        switch(timePickerView.getTag()){
            case "start":
                String start = TimeUtils.convertToStr(seconds, F);
                et_start.setText(start);
                break;
            case "end":
                String end = TimeUtils.convertToStr(seconds, F);
                et_end.setText(end);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_time:
                startTime.show(getSupportFragmentManager(),"start");
                break;
            case R.id.et_start:
                startTime.show(getSupportFragmentManager(),"start");
                break;
            case R.id.et_end:
                endTime.show(getSupportFragmentManager(),"end");
                break;
            case R.id.btn_insert:
                start = TimeUtils.convertToMil(et_start.getText().toString(), F);
                end = TimeUtils.convertToMil(et_end.getText().toString(), F);
                List<Point> points = new ArrayList<Point>();
                for (int i = 0; i < 10000; i++) {
                    points.add(new Point(null, 1D, 1D, random(start, end), 1D, 1D, 90D));
                }
                if(pointManager.insertMultObject(points)){
                    ToastUtils.showToast(TestActivity.this,"插入成功");
                }else {
                    ToastUtils.showToast(TestActivity.this,"插入失败");
                }
                break;
            case R.id.btn_query:
                List<Frame> pointList = pointManager.queryAllFrames(Frame.class);
                list.clear();
                if (pointList != null) {
                    for (Frame frame : pointList) {
                        list.add(frame);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }else {
                    ToastUtils.showToast(TestActivity.this,"数据为空");
                }
                break;
            case R.id.btn_export:
                if (!list.isEmpty()) {
                    File log = new File("/storage/sdcard0/mapbar/app/log.txt");
                    try {
                        if (!log.exists())
                            log.createNewFile();
                    } catch (IOException e) {
                        Log.e("IOException", "exception in createNewFile() method");
                    }
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
                        for (int i = 0; i < list.size(); i++) {
                            Log.e("Frame", list.get(i).getContent());
                            bw.write(list.get(i).getContent() + "\r\n");
                        }
                        //保证输出缓冲区中的所有内容
                        bw.flush();
                        //后打开的先关闭，逐层向内关闭
                        bw.close();
                        ToastUtils.showToast(TestActivity.this, "写入成功");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_delete:
                if (pointManager.deleteAll(Frame.class)) {
                    ToastUtils.showToast(TestActivity.this,"删除成功");
                }else {
                    ToastUtils.showToast(TestActivity.this,"删除失败");
                }
                break;
            case R.id.btn_dialog:
                DialogUtils.showLoadingDialog(TestActivity.this,"正在传输");
                break;
            case R.id.btn_map:
                startActivity(new Intent(TestActivity.this,MapViewActivity.class));
                break;
        }
    }
    public Long random(Long start,Long end){
        Long rtn = start + (long)(Math.random() * (end - start));
        //如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if(rtn == start || rtn == end){
            return random(start,end);
        }
        return rtn;
    }
}
