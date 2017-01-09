package com.yang.nav.view.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.mapbar.license.License;
import com.mapbar.mapdal.Auth;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.mapdal.NativeEnvParams;
import com.mapbar.mapdal.SdkAuth;
import com.mapbar.mapdal.WorldManager;
import com.yang.nav.R;
import com.yang.nav.model.PointManager;
import com.yang.nav.model.entity.Point;
import com.yang.nav.utils.DialogUtils;
import com.yang.nav.utils.HandlerUtils;
import com.yang.nav.utils.TimeUtils;
import com.yang.nav.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



public class TestActivity extends AppCompatActivity implements OnDateSetListener, View.OnClickListener, HandlerUtils.OnReceiveMessageListener {

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



    public static final String KEY = "jxz236-20141118-02-L-F-A11100";
    //应用跟目录
    private static String mAppPath = null;
    //应用名
    private static String mAppName = null;
    // 调试时所使用的输出信息
    private final static String TAG = "TestActivity";
    //dpi
    private static int mDensityDpi = 0;

    private HandlerUtils.HandlerHolder mHandler = new HandlerUtils.HandlerHolder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
        initLicense();
        initView();
    }

    private void initView() {
        Button btn_dialog = (Button) findViewById(R.id.btn_dialog);
        Button btn_map = (Button) findViewById(R.id.btn_map);
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
        btn_map.setOnClickListener(this);
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
                for (int i = 0; i < 10000; i++) {
                    points.add(new Point(null,1D,1D,random(start,end)));
                }
                if(pointManager.insertMultObject(points)){
                    ToastUtils.showToast(TestActivity.this,"插入成功");
                }else {
                    ToastUtils.showToast(TestActivity.this,"插入失败");
                }
                break;
            case R.id.btn_query:
                List<Point> pointList = pointManager.getPointsByTime(et_start.getText().toString(),
                        et_end.getText().toString());
                if (pointList != null) {
                    for (Point point: pointList) {
                        list.add(point);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }else {
                    ToastUtils.showToast(TestActivity.this,"数据为空");
                }
                break;
            case R.id.btn_delete:
                if(pointManager.deletePointsByTime(et_start.getText().toString(),
                        et_end.getText().toString())){
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
    /**
     * 初始化全局基础环境
     */
    private void init() {
        // 初始化图吧引擎基础环境
        // 设置应用程序数据根目录
        // 存放包括导航离线数据，资源数据，运行是的临时数据文件等
        mAppPath = "/storage/sdcard0/mapbar/app";
        //Path=/storage/emulated/0/mapbar/app
        //mAppPath = "/storage/sdcard1/mapbar/app";
        // 应用程序名称，可随意设置
        mAppName = "qyfw";
        // 获取屏幕对应的DPI
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        mDensityDpi = dm.densityDpi;
    }
    /**
     * 初始化license信息
     */
    private void initLicense(){
        //拷贝激活文件到应用目录下
        getActivityCodeFile();
        Log.d(TAG, mAppPath);
        License.init(this, mAppPath, mAppName, KEY, mHandler);
    }

    /**
     * 根据apk名称 获取asset里的apk文件
     * 注意：此处不需要每次都copy这个文件，检查如果有此文件，就不需要再copy；
     *      但是如果更换了激活key，则需要删除之前的这个文件，然后再copy
     * @return TODO
     */
    public void getActivityCodeFile() {
        String filename = "activation_codes.dat";
        String path = mAppPath+"/"+filename;
        AssetManager asset = getApplicationContext()
                .getAssets();
        try {
            File pathFile = new File(path);
            File pathFileParent = new File(pathFile.getParent());
            if(!pathFileParent.exists()){
                pathFileParent.mkdirs();
            }
            InputStream is = asset.open(filename+".png");
            FileOutputStream fos = new FileOutputStream(new File(path));
            final int fint3 = 1024;
            byte[] buffer = new byte[fint3];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case License.Initialize.succ: {
                //授权验证模块初始化成功
                String code = License.getActivationCodeNew();
                if(code==null){
                    //授权模式，判断是否激活成功
                    if (License.autoActivateNew()) {
                        //license激活成功，则进行sdk授权
                        NativeEnvironmentInit(mAppName,KEY);
                        ToastUtils.showToast(TestActivity.this,"license activity success");
                    }else{
                        ToastUtils.showToast(TestActivity.this,"license activity fail");
                    }
                }
            }
            break;
            case License.Initialize.deviceIdFailed:
                //获取不到设备ID，一般来说是MAC地址
                ToastUtils.showToast(TestActivity.this,"deviceIdFailed");
                break;
            case License.Initialize.noAvailableDataPath:
                //不存在有效的数据目录
                ToastUtils.showToast(TestActivity.this,"noAvailableDataPath");
                break;
            case License.Initialize.otherFailed:
                //授权验证模块初始化失败
                ToastUtils.showToast(TestActivity.this,"otherFailed");
                break;
            default:
                break;
        }
        NativeEnvironmentInit(mAppName,KEY);
    }

    /**
     * 初始化引擎
     */
    private void NativeEnvironmentInit(String appName, String key) {
        NativeEnvParams params = new NativeEnvParams(mAppPath, appName,
                mDensityDpi, key, new NativeEnv.AuthCallback() {
            @Override
            public void onDataAuthComplete(int errorCode) {
                String msg = null;
                switch(errorCode){
                    case Auth.Error.deviceIdReaderError:
                        msg="设备ID读取错误";
                        break;
                    case Auth.Error.expired:
                        msg="数据文件权限已经过期";
                        break;
                    case Auth.Error.licenseDeviceIdMismatch:
                        msg="授权文件与当前设备不匹配";
                        break;
                    case Auth.Error.licenseFormatError:
                        msg="授权文件格式错误";
                        break;
                    case Auth.Error.licenseIncompatible:
                        msg="授权文件存在且有效，但是不是针对当前应用程序产品的";
                        break;
                    case Auth.Error.licenseIoError:
                        msg="授权文件IO错误";
                        break;
                    case Auth.Error.licenseMissing:
                        msg="授权文件不存在";
                        break;
                    case Auth.Error.none:
                        msg="数据授权成功";
                        break;
                    case Auth.Error.noPermission:
                        msg="数据未授权";
                        break;
                    case Auth.Error.otherError:
                        msg="其他错误";
                        break;
                }
                if(msg!=null){
                    ToastUtils.showToast(TestActivity.this,msg);
                }
            }

            @Override
            public void onSdkAuthComplete(int errorCode) {
                String msg=null;
                switch(errorCode){
                    case SdkAuth.ErrorCode.deviceIdReaderError:
                        msg="授权设备ID读取错误";
                        break;
                    case SdkAuth.ErrorCode.expired:
                        msg="授权KEY已经过期";
                        break;
                    case SdkAuth.ErrorCode.keyIsInvalid:
                        msg="授权KEY是无效值，已经被注销";
                        break;
                    case SdkAuth.ErrorCode.keyIsMismatch:
                        msg="授权KEY不匹配";
                        break;
                    case SdkAuth.ErrorCode.keyUpLimit:
                        msg="授权KEY到达激活上线";
                        break;
                    case SdkAuth.ErrorCode.licenseDeviceIdMismatch:
                        msg="设备码不匹配";
                        break;
                    case SdkAuth.ErrorCode.licenseFormatError:
                        msg="SDK授权文件格式错误";
                        break;
                    case SdkAuth.ErrorCode.licenseIoError:
                        msg="SDK授权文件读取错误";
                        break;
                    case SdkAuth.ErrorCode.licenseMissing:
                        msg="SDK授权文件没有准备好";
                        break;
                    case SdkAuth.ErrorCode.networkContentError:
                        msg="网络返回信息格式错误";
                        break;
                    case SdkAuth.ErrorCode.netWorkIsUnavailable:
                        msg="网络不可用，无法请求SDK验证";
                        break;
                    case SdkAuth.ErrorCode.none:
                        msg="SDK验证通过";
                        break;
                    case SdkAuth.ErrorCode.noPermission:
                        msg="模块没有权限";
                        break;
                    case SdkAuth.ErrorCode.otherError:
                        msg="其他错误";
                        break;
                }
                if(msg!=null){
                    ToastUtils.showToast(TestActivity.this,msg);
                }
            }
        });
        params.sdkAuthOfflineOnly=true;
        NativeEnv.init(getApplicationContext(), params);
        WorldManager.getInstance().init();
    }
}
