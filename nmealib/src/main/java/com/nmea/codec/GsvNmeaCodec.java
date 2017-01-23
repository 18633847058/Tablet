package com.nmea.codec;

import com.nmea.core.ObserverPrintMsg;
import com.nmea.obj.AbstractNmeaObject;
import com.nmea.obj.GsvNmeaObject;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/1/22.
 */

public class GsvNmeaCodec extends AbstractNmeaSentenceCodec {

    public GsvNmeaCodec() {
        //增加观察者
        this.addObserver(new ObserverPrintMsg());
    }

    @Override
    public void decode(String content) throws Exception {
        nmeaObject = new GsvNmeaObject();
        if (!AbstractNmeaObject.GSV_PROTOL.equals(getContentType(content))) {
            throw new Exception("不是GSV语句");
        }
        String msgChecksum = getStringChecksum(getChecksum(content));

        int len = content.length();
        while (len > 0 && (content.charAt(len - 1) == '\r' || content.charAt(len - 1) == '\n')) {
            len--;
        }

        String checksum = null;
        //$GPGSV,3,1,10,20,78,331,45,01,59,235,47,22,41,069,,13,32,252,45*70
        List<String> fileds = new ArrayList<String>();
        int pos = 0;
        for (int i = 0; i < len; i++) {
            char ch = content.charAt(i);

            if (ch == ',') {
                fileds.add(StringUtils.substring(content, pos, i));
                pos = i + 1;
            }
            if (ch == '*') {
                fileds.add(StringUtils.substring(content, pos, i));
                pos = i + 1;
                checksum = StringUtils.substring(content, pos, len);
                break;
            }
        }
        //判断校验和
//		if(!StringUtils.isEmpty(checksum)&&!checksum.equals(msgChecksum)){
//			throw new Exception("数据校验和有误");
//		}
        nmeaObject.setMsgChecksum(msgChecksum);
        nmeaObject.setMsgFields(fileds);
        nmeaObject.setMsgId(fileds.get(0));//$GPGGA
        nmeaObject.setContent(content);

        setChanged();
        notifyObservers(nmeaObject);

//        encode(nmeaObject);
    }

    @Override
    public List<String> encode(AbstractNmeaObject obj) throws Exception {
        List<String> msg = new ArrayList<String>();
        List<String> fields = obj.getMsgFields();
        StringBuffer content = new StringBuffer();
        for (int i = 0; i < fields.size(); i++) {
            content.append(fields.get(i));
            if (i == fields.size() - 1) {
                break;
            }
            content.append(',');
        }
        content.append('*');
        content.append(obj.getMsgChecksum());
        content.append("\r");
        content.append("\n");
        msg.add(content.toString());
        return msg;
    }
}
