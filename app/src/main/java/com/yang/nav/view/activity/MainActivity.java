package com.yang.nav.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yang.nav.BaseApplication;
import com.yang.nav.R;
import com.yang.nav.model.entity.DaoSession;
import com.yang.nav.model.entity.Point;
import com.yang.nav.model.entity.PointDao;
import com.yang.nav.utils.DialogUtils;
import com.yang.nav.utils.ToastUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_dialog = (Button) findViewById(R.id.btn_dialog);
        Button btn_insert = (Button) findViewById(R.id.btn_insert);
        Button btn_query = (Button) findViewById(R.id.btn_query);
        DaoSession daoSession = ((BaseApplication) getApplication()).getDaoSession();
        final PointDao pointDao =daoSession.getPointDao();
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showLoadingDialog(MainActivity.this,"正在传输");
            }
        });
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointDao.insert(new Point(1L,2,3));
            }
        });
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryBuilder qb = pointDao.queryBuilder();
                //获取QueryBuilder // 设置查询条件  //设置排序
                List<Point> points = qb.list();qb.where(PointDao.Properties.Id.eq("1")).list();
                //.orderAsc(PointDao.Properties.LastName)
                for(Point point : points){
                    ToastUtils.showToast(MainActivity.this,point.toString());
                }
            }
        });
    }
}
