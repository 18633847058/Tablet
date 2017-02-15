package com.yang.nav.utils;

import android.util.Log;

import com.nmea.core.CodecManager;
import com.nmea.obj.AbstractNmeaObject;
import com.nmea.obj.ChannelInfo;
import com.nmea.obj.GgaNmeaObject;
import com.nmea.obj.GsvNmeaObject;
import com.nmea.obj.VelNmeaObject;
import com.yang.nav.model.PointManager;
import com.yang.nav.model.entity.Frame;
import com.yang.nav.model.entity.Point;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.yang.nav.BaseApplication.driver;


/**
 * Created by Yang on 2016/12/26.
 */

public class SerialPortUtils {

    private static final String SF = "yyyy年MM月dd日HH时mm分ss秒";
    private static SerialPortUtils portUtil;
    public int baudRate = 115200;
    public byte stopBit = 1;
    public byte dataBit = 8;
    public byte parity = 0;
    public byte flowControl = 0;
    CodecManager codecManager = CodecManager.getInstance();
    private WeakReference<OnDataReceiveListener> onDataReceiveListener;
    private WeakReference<OnGsvReceiveListener> onGsvReceiveListener;
    //缓存的字节数组
    private byte[] recvBuff = null;
    private byte[] oneFrameBuff = null;
    private int startPoint = 0;
    private int endPoint = 0;
    private int recvBuffLen = 0;
    private List<byte[]> frameList = null;
    private ReadThread thread = null;
    private Point point = null;
    private PointManager pointManager;
    private List<Frame> list = null;
    private List<ChannelInfo> g2channelInfos = null;
    private List<ChannelInfo> b2channelInfos = null;
    private int i = 0;
    private int j = 0;

    public SerialPortUtils(PointManager pointManager, OnDataReceiveListener onDataReceiveListener, OnGsvReceiveListener onGsvReceiveListener) {
        this.pointManager = pointManager;
        this.onDataReceiveListener = new WeakReference<>(onDataReceiveListener);
        this.onGsvReceiveListener = new WeakReference<>(onGsvReceiveListener);
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
        recvBuff = new byte[10240];
        oneFrameBuff = null;
        startPoint = 0;
        endPoint = 0;
        recvBuffLen = 0;
        frameList = new ArrayList<>();
        list = new ArrayList<>();
        g2channelInfos = new ArrayList<>();
        b2channelInfos = new ArrayList<>();
        i = 0;
        j = 0;
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

    private String getUTCDay() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int offset = calendar.get(Calendar.ZONE_OFFSET);
        calendar.add(Calendar.MILLISECOND, -offset);
        Date date = calendar.getTime();
        return sf.format(date);
    }

    public interface OnDataReceiveListener {
        public void onDataReceive(Point p);
    }

    public interface OnGsvReceiveListener {
        public void onG2GsvReceive(List<ChannelInfo> l);

        public void onB2GsvReceive(List<ChannelInfo> l);
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
                    size = driver.ReadData(buffer, 512);
                    String str = "";
                    if (size > 0) {
                        String s = new String(buffer, 0, size, "GB2312");
//                        Log.e("总数据：" , s);
                        inputStreamBuff(buffer, size);
                        while (setFrame()) {
                            oneFrameBuff = getFrame();
                            str = new String(oneFrameBuff, "GB2312");
                            Log.e("一帧数据：", str);
                            try {
                                codecManager.decode(str);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            AbstractNmeaObject nmeaObject = CodecManager.getNmeaObject();
                            if (nmeaObject != null) {
                                list.add(new Frame(null, nmeaObject.getContent()));
                                if (nmeaObject.getObjType().equals(AbstractNmeaObject.GGA_PROTOL)) {
                                    GgaNmeaObject ggaNmeaObject = (GgaNmeaObject) nmeaObject;
                                    if (ggaNmeaObject.getType().equals("G2")) {
                                        if (ggaNmeaObject.getLatitude() != null && ggaNmeaObject.getLongitude() != null && ggaNmeaObject.getGpa_flag() != 0) {
                                            point = new Point();
                                            point.setLatitude(Double.valueOf(ggaNmeaObject.getLatitude()));
                                            point.setLongitude(Double.valueOf(ggaNmeaObject.getLongitude()));
                                            point.setTime(TimeUtils.convertToMil(TimeUtils.getLocalTimeFromUTC(TimeUtils.getUTC(getUTCDay(), ggaNmeaObject.getUtc_Time()), SF), SF));
                                        }
                                    }
                                }
                                if (nmeaObject.getObjType().equals(AbstractNmeaObject.VEL_PROTOL)) {
                                    VelNmeaObject velNmeaObject = (VelNmeaObject) nmeaObject;
                                    if (velNmeaObject.getType().equals("G2")) {
                                        if (velNmeaObject.getHor_speed() != null) {
                                            if (point != null) {
                                                try {
                                                    point.setHor_speed(Double.valueOf(velNmeaObject.getHor_speed()));
                                                    point.setVer_speed(Double.valueOf(velNmeaObject.getVer_speed()));
                                                    point.setDirection(Double.valueOf(velNmeaObject.getAngel()));
                                                    Log.e("转化的Point", point.toString());
                                                } catch (Exception e) {
                                                    Log.e("异常", e.toString());
                                                }
                                            }
                                        }
                                    }
                                }

                                if (nmeaObject.getObjType().equals(AbstractNmeaObject.GSV_PROTOL)) {
                                    GsvNmeaObject gsvNmeaObject = (GsvNmeaObject) nmeaObject;
                                    if (gsvNmeaObject.getType().equals("G2")) {
                                        if (gsvNmeaObject.getChannels() == null) {
                                            g2channelInfos.clear();
                                            i = 0;
                                            break;
                                        }
                                        i = gsvNmeaObject.getSatellitesInView();
                                        if (gsvNmeaObject.getMessageNumber() == 1) {
                                            g2channelInfos.clear();
                                        }
                                        for (int m = 0; m < gsvNmeaObject.getChannels().size(); m++) {
                                            g2channelInfos.add(gsvNmeaObject.getChannels().get(m));
                                        }
                                    }
                                    if (gsvNmeaObject.getType().equals("B2")) {
                                        if (gsvNmeaObject.getChannels() == null) {
                                            b2channelInfos.clear();
                                            j = 0;
                                            break;
                                        }
                                        j = gsvNmeaObject.getSatellitesInView();
                                        if (gsvNmeaObject.getMessageNumber() == 1) {
                                            b2channelInfos.clear();
                                        }
                                        for (int m = 0; m < gsvNmeaObject.getChannels().size(); m++) {
                                            b2channelInfos.add(gsvNmeaObject.getChannels().get(m));
                                        }
                                    }
                                }
                            }
                            if (null != onDataReceiveListener && null != point && point.getTime() != null && point.getHor_speed() != null) {
                                onDataReceiveListener.get().onDataReceive(point);
                                point = null;
                            }
                            if (null != onGsvReceiveListener && i != 0 && i == g2channelInfos.size()) {
                                onGsvReceiveListener.get().onG2GsvReceive(g2channelInfos);
                                g2channelInfos.clear();
                                i = 0;
                            }
                            if (null != onGsvReceiveListener && j != 0 && j == b2channelInfos.size()) {
                                onGsvReceiveListener.get().onB2GsvReceive(b2channelInfos);
                                b2channelInfos.clear();
                                j = 0;
                            }
                            if (list.size() > 10) {
                                if (pointManager.insertFrames(list)) {
                                    list.clear();
                                    Log.e("连续十条", "插入10条");
                                }
                            }
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
}
