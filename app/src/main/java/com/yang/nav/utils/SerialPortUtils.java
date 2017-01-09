package com.yang.nav.utils;

import android.util.Log;

import com.nmea.core.CodecManager;
import com.nmea.obj.AbstractNmeaObject;
import com.nmea.obj.GgaNmeaObject;
import com.yang.nav.model.entity.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yang.nav.BaseApplication.driver;


/**
 * Created by Yang on 2016/12/26.
 */

public class SerialPortUtils {

    private OnDataReceiveListener onDataReceiveListener = null;
    private static SerialPortUtils portUtil;
    //缓存的字节数组
    private byte[] recvBuff = new byte[10240];
    private byte[] oneFrameBuff = null;
    private int startPoint = 0;
    private int endPoint = 0;
    private int recvBuffLen = 0;
    private List<byte[]> frameList = new ArrayList<>();
    CodecManager codecManager = CodecManager.getInstance();
    public int baudRate = 9600;
    public byte stopBit = 1;
    public byte dataBit = 8;
    public byte parity = 0;
    public byte flowControl = 0;
    private ReadThread thread = null;

    private SerialPortUtils() {
    }

    public interface OnDataReceiveListener {
        public void onDataReceive(String string);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener onDataReceiveListener) {
        this.onDataReceiveListener = onDataReceiveListener;
    }
    public static SerialPortUtils getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPortUtils();
        }
        return portUtil;
    }

    public boolean onConfig(){
        boolean flag = false;
        if (!driver.ResumeUsbList())// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
        {
            driver.CloseDevice();
            return flag;
        } else {
            if (!driver.UartInit()) {//对串口设备进行初始化操作
                return flag;
            }
            flag = true;
            driver.SetConfig(baudRate, stopBit, dataBit, parity, flowControl);
        }
        return flag;
    }
    public void onStart(){
        thread = new ReadThread();
        thread.start();
    }
    public void onDestroy() {
        try {
            if (null != thread) {
                try {
                    Thread.sleep(500);
                    thread.interrupt();
                    driver.CloseDevice();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thread = null;
        }
    }

    private class ReadThread extends Thread {

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
                    String str = "";
                    if (size > 0) {
                        String s = new String(buffer,0,size,"GB2312");
//                        Log.e("总数据：" , s);
                        inputStreamBuff(buffer,size);
                        while (setFrame()){
                            oneFrameBuff = getFrame();
                            str = new String(oneFrameBuff,"GB2312");
                            Log.e("一帧数据：" , str);
                            try {
                                codecManager.decode(str);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Point point = null;
                            AbstractNmeaObject nmeaObject = CodecManager.getNmeaObject();
                            if (nmeaObject != null) {
                                if(nmeaObject.getObjType() == AbstractNmeaObject.GGA_PROTOL){
                                    GgaNmeaObject ggaNmeaObject = (GgaNmeaObject) nmeaObject;
//                                    Log.e("-----", "----------------------------");
                                    System.out.println(ggaNmeaObject.getLatitude());
                                    System.out.println(ggaNmeaObject.getLongitude());
                                    if(ggaNmeaObject.getLatitude()!=null&&ggaNmeaObject.getLongitude()!=null) {
                                        point = new Point();
                                        point.setLatitude(Double.valueOf(ggaNmeaObject.getLatitude()));
                                        point.setLongitude(Double.valueOf(ggaNmeaObject.getLongitude()));
                                        point.setTime(TimeUtils.currentTimeToMil(new Date(), ggaNmeaObject.getUtc_Time()));
                                        Log.e("转化的Point", point.toString());
                                    }
                                }
                            }
                        }
//                          String str = new String(buffer, 0, size);
//                          Logger.d("length is:"+size+",data is:"+new String(buffer, 0, size));
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceive(str);
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
    private void inputStreamBuff(byte[] bytes,int length){
        if(recvBuffLen + length > 10240){
            recvBuffLen = 0;
        }
        //拷贝数据
        System.arraycopy(bytes, 0, recvBuff, recvBuffLen, length);
        recvBuffLen += length;
    }
    private boolean setFrame(){
        //确定是否找到开始点
        endPoint = 0;
        startPoint = 0;
        boolean flag = false;
        if(recvBuffLen == 0){
            return false;
        }
        for (int i = 0; i < recvBuffLen; i++) {
            if(recvBuff[i]== '$'){
                startPoint = i;
                flag = true;
                continue;
            }
            if(i == recvBuffLen-1){
                return false;
            }
            if (recvBuff[i] == '*'){
                if(i + 2 < recvBuffLen){
                    //必须在缓存长度内才能转换
                    //找到开头找到结尾才能结束循环
                    //只找到结尾不能结束循环
                    if(flag){
                        endPoint = i+2;
                        break;
                    }
                }
            }
        }

        if((endPoint- startPoint + 1)<0){
            return false;
        }

        oneFrameBuff = new byte[endPoint- startPoint + 1];
        //复制这一帧信息到oneFrameBuff
        System.arraycopy(recvBuff,startPoint,oneFrameBuff,0,endPoint-startPoint + 1);
        //整理帧缓存数组
        recvBuffLen = recvBuffLen-endPoint-1;
        System.arraycopy(recvBuff,endPoint + 1,recvBuff,0,recvBuffLen);
        frameList.add(oneFrameBuff);
        return true;
    }
    private byte[] getFrame(){
        return frameList.remove(0);
    }
}
