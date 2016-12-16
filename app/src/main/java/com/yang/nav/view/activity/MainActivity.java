package com.yang.nav.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.yang.nav.model.entity.Point;
import com.yang.nav.utils.DialogUtils;
import com.yang.nav.utils.TimeUtils;
import com.yang.nav.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDateSetListener, View.OnClickListener {

    private ListView lv_points;
    private ArrayAdapter<Point> arrayAdapter;
    private PointManager pointManager;
    private EditText et_start;
    private EditText et_end;
    private TimePickerDialog startTime;
    private TimePickerDialog endTime;
    private Long start;
    private Long end;
    private  List<Point> list;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button btn_dialog = (Button) findViewById(R.id.btn_dialog);
        Button btn_insert = (Button) findViewById(R.id.btn_insert);
        Button btn_query = (Button) findViewById(R.id.btn_query);
        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        Button btn_time = (Button) findViewById(R.id.btn_time);
        et_start = (EditText) findViewById(R.id.et_start);
        et_end = (EditText) findViewById(R.id.et_end);
        et_start.setOnClickListener(this);
        et_end.setOnClickListener(this);
        et_start.setFocusable(false);
        et_end.setFocusable(false);
        btn_dialog.setOnClickListener(this);
        btn_insert.setOnClickListener(this);
        btn_query.setOnClickListener(this);
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
                String start = TimeUtils.convertToStr(seconds);
                et_start.setText(start);
                break;
            case "end":
                String end = TimeUtils.convertToStr(seconds);
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
                start = TimeUtils.convertToMil(et_start.getText().toString());
                end = TimeUtils.convertToMil(et_end.getText().toString());
                List<Point> points = new ArrayList<Point>();
                for (int i = 0; i < 10; i++) {
                    points.add(new Point(null,1,1,random(start,end)));
                }
                if(pointManager.insertMultObject(points)){
                    ToastUtils.showToast(MainActivity.this,"插入成功");
                }else {
                    ToastUtils.showToast(MainActivity.this,"插入失败");
                }
                break;
            case R.id.btn_query:
                start = TimeUtils.convertToMil(et_start.getText().toString());
                end = TimeUtils.convertToMil(et_end.getText().toString());
                List<Point> pointList = pointManager.getPointsByTime(start,end);
                for (Point point: pointList) {
                    list.add(point);
                }
                arrayAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_delete:
                start = TimeUtils.convertToMil(et_start.getText().toString());
                end = TimeUtils.convertToMil(et_end.getText().toString());
                if(pointManager.deletePointsByTime(start,end)){
                    ToastUtils.showToast(MainActivity.this,"删除成功");
                }else {
                    ToastUtils.showToast(MainActivity.this,"删除失败");
                }
                break;
            case R.id.btn_dialog:
                DialogUtils.showLoadingDialog(MainActivity.this,"正在传输");
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
