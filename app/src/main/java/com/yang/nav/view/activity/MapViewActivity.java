package com.yang.nav.view.activity;

import android.content.Context;
import android.graphics.Point;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mapbar.map.MapRenderer;
import com.mapbar.map.Overlay;
import com.mapbar.map.PolylineOverlay;
import com.yang.nav.R;
import com.yang.nav.map.Config;
import com.yang.nav.map.CustomMapView;
import com.yang.nav.utils.HandlerUtils;
import com.yang.nav.utils.SerialPortUtils;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

import static com.yang.nav.BaseApplication.driver;
import static com.yang.nav.map.Config.centerPoint;
import static com.yang.nav.map.Config.online;

public class MapViewActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener, View.OnClickListener, SerialPortUtils.OnDataReceiveListener, CustomMapView.OnScrollListener {

    private static final String TAG = MapViewActivity.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.yang.nav.USB_PERMISSION";
    //地图控制类
    private CustomMapView customMapView;
    //地图渲染
    private MapRenderer mRenderer;
    // 地图放大缩小控件
    private ImageButton mZoomInImageView = null;
    private ImageButton mZoomOutImageView = null;
    private ImageButton mLocationMode = null;
    private Button mStartLocate = null;

    private PolylineOverlay pl1 = null;
    private volatile Point[] p  =  null;
    private HandlerUtils.HandlerHolder mHandler = new HandlerUtils.HandlerHolder(this);

    //串口
    private SerialPortUtils portUtil;
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map_view);
        initMap();
        initView();
        initPort();
    }

    private void initPort() {
        if(driver == null){
            driver = new CH34xUARTDriver(
                    (UsbManager) getSystemService(Context.USB_SERVICE), this.getApplicationContext(),
                    ACTION_USB_PERMISSION);
        }
        portUtil = SerialPortUtils.getInstance();
        portUtil.setOnDataReceiveListener(this);
    }

    private void initMap () {
        try {
            if (Config.DEBUG) {
                Log.d(TAG, "Before - Initialize the GLMapRenderer Environment");
            }
            // 加载地图
            customMapView = (CustomMapView) findViewById(R.id.mv_show);
            customMapView.setZoomHandler(mHandler);
            customMapView.setOnFlingListener(this);
            if(online&&!customMapView.isOpenNet()){
                Toast.makeText(this, "请连接网络", Toast.LENGTH_SHORT	).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化控件
     */
    private void initView () {
        mZoomInImageView = (ImageButton) findViewById(R.id.btn_zoom_in); // 地图放大按钮
        mZoomOutImageView = (ImageButton) findViewById(R.id.btn_zoom_out); // 地图缩小按钮
        mLocationMode = (ImageButton) findViewById(R.id.btn_location_mode); // 地图模式改变
        mLocationMode.setTag(2);
        mStartLocate = (Button) findViewById(R.id.btn_start_location);
        mZoomInImageView.setOnClickListener(this);
        mZoomOutImageView.setOnClickListener(this);
        mLocationMode.setOnClickListener(this);
        mStartLocate.setOnClickListener(this);
    }


    @Override
    public void handlerMessage(Message msg) {
        switch(msg.what){
            case 1:
                //地图控件加载完毕
                mRenderer = customMapView.getMapRenderer();
                mRenderer.setDataMode(online?MapRenderer.DataMode.online:MapRenderer.DataMode.offline);
                mRenderer.setWorldCenter(new Point(11634990, 3997617));
                break;
            case 2:
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
                readPoints();

//                if (!isOpen) {
//                    if(portUtil.onConfig()){
//                        isOpen = true;
//                        mStartLocate.setText("结束定位");
//                        portUtil.onStart();//开启读线程读取串口接收的数据
//                    }
//                } else {
//                    portUtil.onDestroy();
//                    mStartLocate.setText("开始定位");
//                    isOpen = false;
//                }
                break;
        }
    }
    public void showRoute(){
//        customMapView.setCarPosition(new Point(11640162, 3990778));
        // 线
        PolylineOverlay pl1 = new PolylineOverlay(p, false);
        pl1.setColor(0xffaa0000);
        pl1.setStrokeStyle(Overlay.StrokeStyle.solid);
        pl1.setWidth(5.0f);
        mRenderer.addOverlay(pl1);
    }
    private void readPoints() {
        p  = new Point[coords.length/2 ];
        for (int i = 0 ; i < coords.length; i += 2) {
//            p.add(new Point(coords[i + 1], coords[i]));
            p[i/2] = new Point(coords[i], coords[i + 1]);
            Log.e(TAG, "readPoints: "+ p[i/2].toString());
        }
        mHandler.sendEmptyMessage(2);
    }
//                          11640152
    private int[] coords = {11634990, 3997617,
        11634978, 3997619, 11634967,
        3997623, 11634955, 3997626,
        11634943, 3997628, 11634930,
        3997628, 11634918, 3997626,
        11634906, 3997623, 11634895,
        3997621, 11634886, 3997628,
        11634873, 3997628, 11634860,
        3997626, 11634846, 3997630,
        11634834, 3997635, 11634831,
        3997645, 11634827, 3997655,
        11634824, 3997665, 11634825,
        3997675, 11634825, 3997686,
        11634822, 3997698, 11634822,
        3997707, 11634822, 3997718,
        11634821, 3997730, 11634820,
        3997741, 11634821, 3997753,
        11634821, 3997765, 11634820,
        3997776, 11634820, 3997786,
        11634822, 3997795, 11634822,
        3997807, 11634820, 3997817,
        11634819, 3997826, 11634820,
        3997838, 11634819, 3997848,
        11634816, 3997859, 11634818,
        3997870, 11634818, 3997879,
        11634817, 3997889, 11634816,
        3997899, 11634815, 3997911,
        11634812, 3997920, 11634809,
        3997930, 11634805, 3997939,
        11634803, 3997949, 11634800,
        3997958, 11634799, 3997968,
        11634798, 3997980, 11634799,
        3997990, 11634798, 3998000,
        11634799, 3998011, 11634799,
        3998021, 11634798, 3998032,
        11634797, 3998042, 11634796,
        3998054, 11634797, 3998065,
        11634793, 3998074, 11634792,
        3998085, 11634789, 3998098,
        11634782, 3998108, 11634774,
        3998115, 11634761, 3998115,
        11634749, 3998114, 11634734,
        3998114, 11634722, 3998115,
        11634708, 3998114, 11634696,
        3998112, 11634681, 3998113,
        11634669, 3998114, 11634658,
        3998112, 11634643, 3998111,
        11634631, 3998108, 11634614,
        3998108, 11634602, 3998104,
        11634588, 3998104, 11634575,
        3998105, 11634562, 3998104,
        11634549, 3998105, 11634537,
        3998105, 11634535, 3998095
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        portUtil.onDestroy();
    }

    @Override
    public void onDataReceive(String string) {

    }

    @Override
    public void onScroll() {
        if(mRenderer.getWorldCenter() != centerPoint){
            mLocationMode.setImageResource(R.mipmap.ic_qu_direction_location_lost);
            mLocationMode.setTag(1);
        }
    }
}
