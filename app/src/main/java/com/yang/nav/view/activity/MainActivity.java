package com.yang.nav.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.yang.nav.R;
import com.yang.nav.model.PointManager;
import com.yang.nav.model.entity.Point;
import com.yang.nav.utils.DialogUtils;
import com.yang.nav.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv_points;
    private ArrayAdapter<Point> arrayAdapter;
    private PointManager pointManager;
    private EditText et_start;
    private EditText et_end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_dialog = (Button) findViewById(R.id.btn_dialog);
        Button btn_insert = (Button) findViewById(R.id.btn_insert);
        Button btn_query = (Button) findViewById(R.id.btn_query);
        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        et_start = (EditText) findViewById(R.id.et_start);
        et_end = (EditText) findViewById(R.id.et_end);
        lv_points = (ListView) findViewById(R.id.lv_points);
        final List<Point> list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<Point>(this,android.R.layout.simple_list_item_1,list);
        lv_points.setAdapter(arrayAdapter);
        pointManager = PointManager.getInstance(getApplication());
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showLoadingDialog(MainActivity.this,"正在传输");
            }
        });
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Point> list = new ArrayList<Point>();
                for (Long i = 0L; i < 100L ; i++) {
                    list.add(new Point(null,1,1,i));
                }
                if(pointManager.insertMultObject(list)){
                    ToastUtils.showToast(MainActivity.this,"插入成功");
                }else {
                    ToastUtils.showToast(MainActivity.this,"插入失败");
                }
            }
        });
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long start = Long.valueOf(et_start.getText().toString());
                Long end = Long.valueOf(et_end.getText().toString());
                List<Point> points = pointManager.getPointsByTime(start,end);
                for (Point point: points) {
                    list.add(point);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long start = Long.valueOf(et_start.getText().toString());
                Long end = Long.valueOf(et_end.getText().toString());
                if(pointManager.deletePointsByTime(start,end)){
                    ToastUtils.showToast(MainActivity.this,"删除成功");
                }else {
                    ToastUtils.showToast(MainActivity.this,"删除失败");
                }
            }
        });
    }
}
