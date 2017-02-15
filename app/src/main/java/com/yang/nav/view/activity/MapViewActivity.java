package com.yang.nav.view.activity;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.BarChartView;
import com.mapbar.map.MapRenderer;
import com.mapbar.map.Overlay;
import com.mapbar.map.PolylineOverlay;
import com.nmea.obj.ChannelInfo;
import com.yang.nav.R;
import com.yang.nav.map.CustomMapView;
import com.yang.nav.model.PointManager;
import com.yang.nav.utils.HandlerUtils;
import com.yang.nav.utils.PointUtils;
import com.yang.nav.utils.SerialPortUtils;
import com.yang.nav.utils.TimeUtils;

import java.util.List;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

import static com.yang.nav.BaseApplication.driver;

public class MapViewActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener, View.OnClickListener, SerialPortUtils.OnDataReceiveListener, CustomMapView.OnScrollListener, SerialPortUtils.OnGsvReceiveListener {

    private static final String TAG = MapViewActivity.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.yang.nav.USB_PERMISSION";
    private static final String SF = "HH：mm：ss";
    //地图控制类
    private CustomMapView customMapView;
    //地图渲染
    private MapRenderer mRenderer;
    // 地图放大缩小控件
    private ImageButton mZoomInImageView = null;
    private ImageButton mZoomOutImageView = null;
    private ImageButton mLocationMode = null;
    private Button mStartLocate = null;
    private Button mStartCollect = null;
    private TextView tv_utc_time;
    private TextView tv_hor_speed;
    private TextView tv_ver_speed;
    private TextView tv_direction;
    private TextView tv_longitude;
    private TextView tv_latitude;
    private BarChartView mChart1;
    private BarChartView mChart2;
    private CardView mCard1;
    private CardView mCard2;

    private String[] mLabels;

    private float[] mValues;

    private String[] mLabels2;

    private float[] mValues2;


    private HandlerUtils.HandlerHolder mHandler = new HandlerUtils.HandlerHolder(this);

    //串口
    private SerialPortUtils portUtil;
    private boolean isOpen = false;
    private boolean isCollect = false;

    //数据库
    private PointManager pointManager;
    private Point lastPoint = null;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map_view);
        initMap();
        initView();
        pointManager = PointManager.getInstance(getApplicationContext());
        initPort();
//        initChart();
    }

    private void initPort() {
        if(driver == null){
            driver = new CH34xUARTDriver(
                    (UsbManager) getSystemService(Context.USB_SERVICE), this.getApplicationContext(),
                    ACTION_USB_PERMISSION);
        }
        portUtil = new SerialPortUtils(pointManager, this, this);
    }

    private void initMap () {
        try {
            // 加载地图
            customMapView = (CustomMapView) findViewById(R.id.mv_show);
            customMapView.setZoomHandler(mHandler);
            customMapView.setOnScrollListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化控件
     */
    private void initView() {
        tv_utc_time = (TextView) findViewById(R.id.tv_time);
        tv_direction = (TextView) findViewById(R.id.tv_direction);
        tv_hor_speed = (TextView) findViewById(R.id.tv_hor_speed);
        tv_ver_speed = (TextView) findViewById(R.id.tv_ver_speed);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        mCard1 = (CardView) findViewById(R.id.card1);
        mCard2 = (CardView) findViewById(R.id.card2);
        mZoomInImageView = (ImageButton) findViewById(R.id.btn_zoom_in); // 地图放大按钮
        mZoomOutImageView = (ImageButton) findViewById(R.id.btn_zoom_out); // 地图缩小按钮
        mLocationMode = (ImageButton) findViewById(R.id.btn_location_mode); // 地图模式改变
        mLocationMode.setTag(2);
        mStartLocate = (Button) findViewById(R.id.btn_start_location);
        mStartCollect = (Button) findViewById(R.id.btn_start_collect);
        mZoomInImageView.setOnClickListener(this);
        mZoomOutImageView.setOnClickListener(this);
        mLocationMode.setOnClickListener(this);
        mStartLocate.setOnClickListener(this);
        mStartCollect.setOnClickListener(this);
    }

    private void initChart(BarChartView chartView, String[] mLabels, float[] mValues) {
        Tooltip tip = new Tooltip(this);
        tip.setBackgroundColor(Color.parseColor("#CC7B1F"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
            tip.setEnterAnimation(alpha).setDuration(150);

            alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
            tip.setExitAnimation(alpha).setDuration(150);
        }

        chartView.setTooltips(tip);

        BarSet dataset = new BarSet(mLabels, mValues);
        dataset.setColor(Color.CYAN);
        chartView.addData(dataset);

        chartView.setBarSpacing(Tools.fromDpToPx(10f));

        chartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE)
                .setYLabels(AxisRenderer.LabelPosition.OUTSIDE)
                .setXAxis(true)
                .setYAxis(true)
                .setAxisBorderValues(0, 60, 5);

//        Animation anim = new Animation().setEasing(new AccelerateDecelerateInterpolator()).setDuration(500);
//
//        chartView.show(anim);
        chartView.show();
    }

    @Override
    public void handlerMessage(Message msg) {
        switch(msg.what){
            case 1:
                //地图控件加载完毕
                mRenderer = customMapView.getMapRenderer();
                mRenderer.setDataMode(MapRenderer.DataMode.offline);
                break;
            case 2:
                com.yang.nav.model.entity.Point point = (com.yang.nav.model.entity.Point) msg.obj;
                java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
                Double lon = point.getLongitude();
                Double lat = point.getLatitude();
                Double hor_speed = point.getHor_speed();
                Double ver_speed = point.getVer_speed();
                Double direction = point.getDirection();
                tv_hor_speed.setText(df.format(hor_speed));
                tv_ver_speed.setText(df.format(ver_speed));
                tv_direction.setText(df.format(direction));
                tv_utc_time.setText(TimeUtils.convertToStr(point.getTime(), SF));
                tv_longitude.setText(lon.toString());
                tv_latitude.setText(lat.toString());

                Point p = PointUtils.gps84_To_Gcj02(point.getLongitude(), point.getLatitude());
                if (lastPoint == null) {
                    lastPoint = p;
                }
                Log.e(TAG, "handlerMessage: " + i++);
//                ToastUtils.showToast(MapViewActivity.this, p.toString());
                customMapView.setCarPosition(p);
                showRoute(p);
                break;
            case 3:
                mCard1.removeAllViews();
                mChart1 = new BarChartView(this);
                ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mChart1.setLayoutParams(vlp);
                mCard1.addView(mChart1);
                initChart(mChart1, mLabels, mValues);
                break;
            case 4:
                mCard2.removeAllViews();
                mChart2 = new BarChartView(this);
                ViewGroup.LayoutParams vlp1 = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mChart2.setLayoutParams(vlp1);
                mCard2.addView(mChart2);
                initChart(mChart2, mLabels2, mValues2);
                break;
            case 100:
                //监听地图缩放 修改按钮状态
                Bundle b = msg.getData();
                mZoomInImageView.setEnabled(b.getBoolean("zoomOut"));
                mZoomOutImageView.setEnabled(b.getBoolean("zoomIn"));
                break;
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
                switch ((int)mLocationMode.getTag()){
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
            case R.id.btn_start_location:
//                readPoints();
                if (!isOpen) {
                    if (portUtil.onConfig()) {
                        isOpen = true;
                        mStartLocate.setText("结束定位");
                        portUtil.onStart();//开启读线程读取串口接收的数据
                    }
                } else {
                    portUtil.onDestroy();
                    mStartLocate.setText("开始定位");
                    isOpen = false;
                }
                break;
            case R.id.btn_start_collect:
                if (!isCollect) {
                    mCard1.setVisibility(View.VISIBLE);
                    mCard2.setVisibility(View.VISIBLE);
                    isCollect = true;
                } else {
                    mCard1.setVisibility(View.GONE);
                    mCard2.setVisibility(View.GONE);
                    mChart1 = null;
                    mChart2 = null;
                    isCollect = false;
                }
        }
    }

    public void showRoute(Point p) {
//        customMapView.setCarPosition(new Point(11640162, 3990778));
        // 线
        if (!lastPoint.equals(p)) {
            Point[] route = new Point[2];
            route[0] = lastPoint;
            route[1] = p;
            PolylineOverlay pl1 = new PolylineOverlay(route, false);
            pl1.setColor(0xffaa0000);
            pl1.setStrokeStyle(Overlay.StrokeStyle.solid);
            pl1.setWidth(5.0f);
            mRenderer.addOverlay(pl1);
            lastPoint = p;
        } else {
            mRenderer.setWorldCenter(p);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 其他操作
        // 暂停地图
        mCard1.setVisibility(View.GONE);
        mCard2.setVisibility(View.GONE);
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

        portUtil.onDestroy();
        if (customMapView != null) {
            // 此Activity销毁时，销毁地图控件
            customMapView.onDestroy();
        }
        customMapView = null;
    }

    @Override
    public void onDataReceive(com.yang.nav.model.entity.Point point) {
        if (pointManager.insertObject(point)) {
            Log.e(TAG, "插入成功");

            Message m = mHandler.obtainMessage(2);
            m.obj = point;
            mHandler.sendMessage(m);
        } else {
            Log.e(TAG, "插入失败");
        }
    }

    @Override
    public void onG2GsvReceive(List<ChannelInfo> l) {
        Log.e(TAG, "onG2GsvReceive: " + l.size() + "-----------------------------------");
        if (mCard1.getVisibility() == View.VISIBLE && Integer.valueOf(l.get(0).getSatelliteID()) < 33) {
            mLabels = new String[l.size()];
            mValues = new float[l.size()];
            for (int i = 0; i < l.size(); i++) {
                mLabels[i] = l.get(i).getSatelliteID();
                mValues[i] = Float.valueOf(l.get(i).getSNR());
            }
            mHandler.sendEmptyMessage(3);
        }
    }

    @Override
    public void onB2GsvReceive(List<ChannelInfo> l) {
        Log.e(TAG, "onB2GsvReceive: " + l.size() + "-----------------------------------");
        if (mCard2.getVisibility() == View.VISIBLE) {
            mLabels2 = new String[l.size()];
            mValues2 = new float[l.size()];
            for (int i = 0; i < l.size(); i++) {
                mLabels2[i] = l.get(i).getSatelliteID();
                mValues2[i] = Float.valueOf(l.get(i).getSNR());
            }
            mHandler.sendEmptyMessage(4);
        }
    }


    @Override
    public void onScroll() {
        if (mRenderer.getWorldCenter() != customMapView.getCarPosition()) {
            mLocationMode.setImageResource(R.mipmap.ic_qu_direction_location_lost);
            mLocationMode.setTag(1);
        }
    }
}
