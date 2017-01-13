package com.yang.nav.utils;

import android.util.Log;
import android.util.Xml;

import com.yang.nav.model.entity.Point;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2016/12/21.
 */

public class XmlUtils {
    public static final String SF = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String NAME_SPACE = "http://www.topografix.com/GPX/1/1";

    public static void XmlFileCreator(List<Point> pointList){
        File newXmlFile = new File("/storage/sdcard0/mapbar/app/test.gpx");
        try{
            if(!newXmlFile.exists())
                newXmlFile.createNewFile();
        }catch(IOException e){
            Log.e("IOException", "exception in createNewFile() method");
        }
        //we have to bind the new file with a FileOutputStream
        FileOutputStream fileos = null;
        try{
            fileos = new FileOutputStream(newXmlFile);
        }catch(FileNotFoundException e){
            Log.e("FileNotFoundException", e.toString());
        }
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fileos,"UTF-8");
            //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
            serializer.startDocument(null,false);
            //set indentation option
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.setPrefix("","http://www.topografix.com/GPX/1/1");
            serializer.setPrefix("xsi","http://www.w3.org/2001/XMLSchema-instance");
            serializer.setPrefix("schemaLocation","http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            serializer.startTag(NAME_SPACE,"gpx");
            serializer.startTag(null,"trk");
            serializer.startTag(null,"trkseg");
            for(Point point : pointList){
                serializer.startTag(null,"trkpt");
                serializer.attribute(null,"lat",point.getLatitude()+"");
                serializer.attribute(null,"lon",point.getLongitude()+"");
                serializer.startTag(null,"time");
                serializer.text(TimeUtils.convertToStr(point.getTime(), SF));
                serializer.endTag(null,"time");
                serializer.endTag(null,"trkpt");
            }
            serializer.endTag(null,"trkseg");
            serializer.endTag(null,"trk");
            serializer.endTag(NAME_SPACE,"gpx");
            serializer.endDocument();
            //write xml data into the FileOutputStream
            serializer.flush();
            //finally we close the file stream
            fileos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Point> parseXml(String file){
//        file:///storage/emulated/0/mapbar/app/test.xml
        List<Point> list = null;
        Point point = null;
        String path = file.substring(7);
        Log.e("文件路径", "parseXml: "+path);
        File importFile = new File(path);
        if(importFile.exists()){
            FileInputStream fileis = null;
            try {
                fileis = new FileInputStream(importFile);
            } catch (FileNotFoundException e) {
                Log.e("FileNotFoundException", e.toString());
            }
            XmlPullParser xmlParser = Xml.newPullParser();
            try {
                xmlParser.setInput(fileis,"UTF_8");
                int eventType = xmlParser.getEventType();
                while (eventType!= XmlPullParser.END_DOCUMENT){
                    switch (eventType) {
                        // 判断当前事件是否为文档开始事件
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<Point>(); // 初始化points集合
                            break;
                        // 判断当前事件是否为标签元素开始事件
                        case XmlPullParser.START_TAG:
                            if (xmlParser.getName().equals("trkpt")) { // 判断开始标签元素是否是book
                                point = new Point();
                                point.setLatitude(Double.valueOf(xmlParser.getAttributeValue(null,"lat")));
                                point.setLongitude(Double.valueOf(xmlParser.getAttributeValue(null,"lon")));
                            } else if (xmlParser.getName().equals("time")) {
                                eventType = xmlParser.next();//让解析器指向name属性的值
                                // 得到name标签的属性值，并设置beauty的name
                                point.setTime(TimeUtils.convertToMil(xmlParser.getText(), SF));
                            }
                            break;
                        // 判断当前事件是否为标签元素结束事件
                        case XmlPullParser.END_TAG:
                            if (xmlParser.getName().equals("trkpt")) { // 判断结束标签元素是否是book
                                list.add(point); // 将book添加到books集合
                                Log.e("定位信息", "parseXml: " + point.toString());
                                point = null;
                            }
                            break;
                    }
                    // 进入下一个元素并触发相应事件
                    eventType = xmlParser.next();
                }
            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException", e.toString());
            } catch (IOException e) {
                Log.e("IOException", e.toString());
            }
        }
        return list;
    }
}
