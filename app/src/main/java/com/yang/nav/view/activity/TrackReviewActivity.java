package com.yang.nav.view.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mapbar.map.MapRenderer;
import com.mapbar.map.Overlay;
import com.mapbar.map.PolylineOverlay;
import com.yang.nav.R;
import com.yang.nav.map.CustomMapView;
import com.yang.nav.model.entity.Point;
import com.yang.nav.utils.HandlerUtils;

import java.util.ArrayList;

public class TrackReviewActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener, CustomMapView.OnScrollListener, View.OnClickListener {

    private static final String TAG = TrackReviewActivity.class.getSimpleName();
    //地图控制类
    private CustomMapView customMapView;
    //地图渲染
    private MapRenderer mRenderer;
    // 地图放大缩小控件
    private ImageButton mZoomInImageView = null;
    private ImageButton mZoomOutImageView = null;
    private ImageButton mLocationMode = null;

    private PolylineOverlay pl1 = null;
    private ArrayList<Point> points = null;
    private HandlerUtils.HandlerHolder mHandler = new HandlerUtils.HandlerHolder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_track_review);
        Bundle bundle = getIntent().getExtras();
        points = bundle.getParcelableArrayList("points");
        initMap();
        initView();
        Log.e(TAG, "onCreate: " + points.toString());
    }

    private void initMap() {
        try {
            // 加载地图
            customMapView = (CustomMapView) findViewById(R.id.mv_show);
            customMapView.setZoomHandler(mHandler);
            customMapView.setOnScrollListener(this);
            if (!customMapView.isOpenNet()) {
                Toast.makeText(this, "请连接网络", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mZoomInImageView = (ImageButton) findViewById(R.id.btn_zoom_in); // 地图放大按钮
        mZoomOutImageView = (ImageButton) findViewById(R.id.btn_zoom_out); // 地图缩小按钮
        mLocationMode = (ImageButton) findViewById(R.id.btn_location_mode); // 地图模式改变
        mLocationMode.setTag(2);
        mZoomInImageView.setOnClickListener(this);
        mZoomOutImageView.setOnClickListener(this);
        mLocationMode.setOnClickListener(this);
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case 1:
                //地图控件加载完毕
                mRenderer = customMapView.getMapRenderer();
                mRenderer.setDataMode(MapRenderer.DataMode.offline);
                showRoute();
                break;
            case 100:
                //监听地图缩放 修改按钮状态
                Bundle b = msg.getData();
                mZoomInImageView.setEnabled(b.getBoolean("zoomOut"));
                mZoomOutImageView.setEnabled(b.getBoolean("zoomIn"));
                break;
        }
    }

    private void showRoute() {
        if (points != null) {
            android.graphics.Point[] ps = new android.graphics.Point[points.size()];
            for (int i = 0; i < points.size(); i++) {

                Point point = points.get(i);
                Double lon = point.getLongitude() * 100000;
                Double lat = point.getLatitude() * 100000;
                android.graphics.Point p = new android.graphics.Point(lon.intValue(), lat.intValue());
                if (i == points.size() - 1) {
                    customMapView.setCarPosition(p);
                }
                ps[i] = p;
            }
            Log.e(TAG, "showRoute: " + ps);
            pl1 = new PolylineOverlay(ps, false);
            pl1.setColor(0xffaa0000);
            pl1.setStrokeStyle(Overlay.StrokeStyle.solid);
            pl1.setWidth(5.0f);
            mRenderer.addOverlay(pl1);
        }
    }

    @Override
    public void onScroll() {
        if (mRenderer.getWorldCenter() != customMapView.getCarPosition()) {
            mLocationMode.setImageResource(R.mipmap.ic_qu_direction_location_lost);
            mLocationMode.setTag(1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zoom_in:
                // 放大地图操作
                customMapView.mapZoomIn(mZoomInImageView, mZoomOutImageView);
                // }
                break;
            case R.id.btn_zoom_out:
                // 地图缩小
                customMapView.mapZoomOut(mZoomInImageView, mZoomOutImageView);
                // }
                break;
            case R.id.btn_location_mode:
                //改变地图状态
//                Toast.makeText(this,mRenderer.getWorldCenter().toString(),Toast.LENGTH_LONG).show();
//                customMapView.setCarPosition(new Point(11640162, 3990778));
                switch ((int) mLocationMode.getTag()) {
                    case 1:
                        mRenderer.beginAnimations();
                        mRenderer.setWorldCenter(customMapView.getCarPosition());
                        mRenderer.setHeading(0);
                        mRenderer.setElevation(90);
                        mRenderer.commitAnimations(2000, MapRenderer.Animation.linear);
                        mLocationMode.setImageResource(R.mipmap.ic_qu_direction_location);
                        mLocationMode.setTag(2);
                        break;
                    case 2:
                        // 右转56度3D视图
                        mRenderer.beginAnimations();
                        mRenderer.setHeading(0);
                        mRenderer.setElevation(27.5f);
                        mRenderer.commitAnimations(2000, MapRenderer.Animation.linear);
                        mLocationMode.setImageResource(R.mipmap.ic_qu_explore);
                        mLocationMode.setTag(3);
                        break;
                    case 3:
                        mRenderer.beginAnimations();
                        mRenderer.setHeading(0);
                        mRenderer.setElevation(90);
                        mRenderer.commitAnimations(2000, MapRenderer.Animation.linear);
                        mLocationMode.setImageResource(R.mipmap.ic_qu_direction_location);
                        mLocationMode.setTag(2);
                        break;
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 其他操作
        // 暂停地图
        if (customMapView != null) {
            customMapView.onPause();
        }
        // 其他操作
    }

    @Override
    public void onResume() {
        super.onResume();
        // 其他操作
        // 恢复地图
        if (customMapView != null) {
            customMapView.onResume();
        }
        // 其他操作
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customMapView != null) {
            // 此Activity销毁时，销毁地图控件
            customMapView.onDestroy();
        }
        customMapView = null;
    }
}
