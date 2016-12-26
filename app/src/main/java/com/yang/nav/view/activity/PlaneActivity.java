package com.yang.nav.view.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yang.nav.R;
import com.yang.nav.utils.SerialPortUtil;
import com.yang.nav.utils.ToastUtils;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

public class PlaneActivity extends AppCompatActivity implements View.OnClickListener, SerialPortUtil.OnDataReceiveListener {

    public static final String TAG = "com.yang.nav.PlaneActivity";
    private static final String ACTION_USB_PERMISSION = "com.yang.nav.USB_PERMISSION";

    private Button btn_driver_open;
    private EditText et_receive_message;
    private CH34xUARTDriver driver;
    private SerialPortUtil portUtil;

    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plane);
        portUtil = SerialPortUtil.getInstance();
        driver = portUtil.getDriver(this);
        portUtil.setOnDataReceiveListener(this);
        //判断系统是否支持 USB HOST
        if(!driver.UsbFeatureSupported()){
            Dialog dialog = new AlertDialog.Builder(PlaneActivity.this)
                    .setTitle("提示")
                    .setMessage("您的手机不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        isOpen = false;
        initView();
    }

    private void initView() {
        btn_driver_open = (Button) findViewById(R.id.btn_driver_open);
        et_receive_message = (EditText) findViewById(R.id.et_receive_message);
        btn_driver_open.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_driver_open:
                if (!isOpen) {
                    if (!driver.ResumeUsbList())// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
                    {
                        ToastUtils.showToast(PlaneActivity.this, "打开设备失败!");
                        driver.CloseDevice();
                    } else {
                        if (!driver.UartInit()) {//对串口设备进行初始化操作
                            ToastUtils.showToast(PlaneActivity.this, "设备初始化失败!");
                            ToastUtils.showToast(PlaneActivity.this, "打开设备失败!");
                            return;
                        }
                        ToastUtils.showToast(PlaneActivity.this, "打开设备成功!");
                        isOpen = true;
                        btn_driver_open.setText("close");
                        new SerialPortUtil.ReadThread().start();//开启读线程读取串口接收的数据
                    }
                } else {
                    driver.CloseDevice();
                    btn_driver_open.setText("open");
                    isOpen = false;
                }
                break;
        }
    }

    @Override
    public void onDataReceive(byte[] buffer, int size) {
        String str = new String(buffer, 0, size);
        et_receive_message.setText(str);
    }
}
