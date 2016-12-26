package com.yang.nav.utils;

import android.content.Context;
import android.hardware.usb.UsbManager;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

/**
 * Created by Yang on 2016/12/26.
 */

public class SerialPortUtil {

    private static final String ACTION_USB_PERMISSION = "com.yang.nav.USB_PERMISSION";
    private OnDataReceiveListener onDataReceiveListener = null;
    private static SerialPortUtil portUtil;
    private static CH34xUARTDriver driver;


    private SerialPortUtil() {
    }

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener onDataReceiveListener) {
        this.onDataReceiveListener = onDataReceiveListener;
    }
    public static SerialPortUtil getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPortUtil();
        }
        return portUtil;
    }

    public static class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    if (driver == null)
                        return;
                    byte[] buffer = new byte[512];
                    size = driver.ReadData(buffer,512);
                    if (size > 0) {
//                          String str = new String(buffer, 0, size);
//                          Logger.d("length is:"+size+",data is:"+new String(buffer, 0, size));
                        if (null != portUtil.onDataReceiveListener) {
                            portUtil.onDataReceiveListener.onDataReceive(buffer, size);
                        }
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

    }
    public static CH34xUARTDriver getDriver(Context context){
        CH34xUARTDriver instance = null;
        if(driver == null){
            synchronized (CH34xUARTDriver.class){
                if(instance == null){
                    instance = new CH34xUARTDriver((UsbManager) context.getSystemService(Context.USB_SERVICE),
                            context,ACTION_USB_PERMISSION);
                    driver = instance;
                }
            }
        }
        return driver;
    }
}
