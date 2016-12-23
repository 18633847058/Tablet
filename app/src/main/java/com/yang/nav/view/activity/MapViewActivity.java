package com.yang.nav.view.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbar.map.MapRenderer;
import com.mapbar.map.Overlay;
import com.mapbar.map.PolylineOverlay;
import com.yang.nav.R;
import com.yang.nav.map.Config;
import com.yang.nav.map.CustomMapView;
import com.yang.nav.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yang.nav.map.Config.online;

public class MapViewActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener, View.OnClickListener {

    private static final String TAG = MapViewActivity.class.getSimpleName();
    //地图控制类
    private CustomMapView customMapView;
    //地图渲染
    private MapRenderer mRenderer;
    // 地图放大缩小控件
    public ImageView mZoomInImageView = null;
    private ImageView mZoomOutImageView = null;
    private PolylineOverlay pl1 = null;
    private HandlerUtils.HandlerHolder mHandler = new HandlerUtils.HandlerHolder(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map_view);
        initMap();
        initView();
    }
    private void initMap () {
        try {
            if (Config.DEBUG) {
                Log.d(TAG, "Before - Initialize the GLMapRenderer Environment");
            }
            // 加载地图
            customMapView = (CustomMapView) findViewById(R.id.mv_show);
            customMapView.setZoomHandler(mHandler);
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
        mZoomInImageView = (ImageView) findViewById(R.id.btn_zoom_in); // 地图放大按钮
        mZoomOutImageView = (ImageView) findViewById(R.id.btn_zoom_out); // 地图缩小按钮
        mZoomInImageView.setOnClickListener(this);
        mZoomOutImageView.setOnClickListener(this);
    }


    @Override
    public void handlerMessage(Message msg) {
        switch(msg.what){
            case 1:
                //地图控件加载完毕
                mRenderer = customMapView.getMapRenderer();
                mRenderer.setDataMode(online?MapRenderer.DataMode.online:MapRenderer.DataMode.offline);
                showRoute();
                //加载卫星图
//				mRenderer.setSatellitePicProvider(MapRenderer.SatellitePicProvider.Bing);
//				mRenderer.enableSatelliteMap(true);
//				boolean flag = mRenderer.loadStyleSheet("res/map3ss_satellite.json");
                //取消卫星图
//				mRenderer.enableSatelliteMap(false);
//				boolean flag = mRenderer.loadStyleSheet("res/map3_style_sheet.json");
//				Log.i(TAG, "loadStyleSheet:"+flag);
                //加载影像图
//				mRenderer.setDataUrlPrefix(UrlType.satellite, "http://112.124.12.16/");
//				mRenderer.setSatellitePicProvider(MapRenderer.SatellitePicProvider.Default);
//				mRenderer.enableSatelliteMap(true);
//				boolean flag = mRenderer.loadStyleSheet("res/map3ss_satellite.json");

                //hauye 消除出现红线，
//				Point p1 = Config.NAVI_START_POINT;
//				Point p2 = Config.NAVI_END_POINT;
                List<Point> list = new ArrayList<Point>();
//				// 循环x坐标
//				for (int i = p1.x + 1; i < p2.x; i+=10){
//					// 计算斜率
//					double k = ((double) (p2.y - p1.y))/(p2.x - p1.x);
//					// 根据斜率,计算y坐标
//					double y = k*(i - p1.x) + p1.y;
//					System.out.println(i+","+y);
//					Point pp = new Point(i,(int)y);
//					list.add(pp);
//				}
                Point[] plist = new Point[list.size()];
                for(int i=0;i<list.size();i++){
                    plist[i]=list.get(i);
                }
                if(pl1!=null){
                    pl1.setHidden(true);
                    mRenderer.removeOverlay(pl1);
                }
                pl1 = new PolylineOverlay(plist, false);
                pl1.setColor(0xffaa0000);
                pl1.setStrokeStyle(Overlay.StrokeStyle.solid);
                pl1.setWidth(5.0f);
                mRenderer.addOverlay(pl1);
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
        }
    }
    public void showRoute(){
        // 线
//        PolylineOverlay pl1 = new PolylineOverlay(new Point[] {
//                new Point(centerPoint.x+1000, centerPoint.y),
//                new Point(centerPoint.x-1000, centerPoint.y-800),
//                new Point(centerPoint.x, centerPoint.y-1800) }, false);
//        pl1.setColor(0xffaa0000);
//        pl1.setStrokeStyle(Overlay.StrokeStyle.solid);
//        pl1.setWidth(5.0f);
//        mRenderer.addOverlay(pl1);
//        ArrowOverlay arrowOverlay = new ArrowOverlay(new Point[] {
//                new Point(centerPoint.x+1000, centerPoint.y),
//                new Point(centerPoint.x-1000, centerPoint.y-800),
//                new Point(centerPoint.x, centerPoint.y-1800) },false);
//        arrowOverlay.setColor(0xffaa0000);
//        arrowOverlay.setWidth(5.0f);
//        mRenderer.addOverlay(arrowOverlay);
    }
}
