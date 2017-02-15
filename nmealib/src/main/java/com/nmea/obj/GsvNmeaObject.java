package com.nmea.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/1/19.
 */

public class GsvNmeaObject extends AbstractNmeaObject {

    private int numberOfMessages;//2

    private int messageNumber;//1

    private int satellitesInView;//5

    private List<ChannelInfo> channels;

    public GsvNmeaObject() {
        this.objType = GSV_PROTOL;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public int getSatellitesInView() {
        return satellitesInView;
    }

    public void setSatellitesInView(int satellitesInView) {
        this.satellitesInView = satellitesInView;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setChannels(ChannelInfo channel) {
        channels.add(channel);
    }

    //    $G2GSV,1,1,03,015,80,357,45,021,51,300,47,018,30,297,45*76
//    $G2GSV,2,1,05,068,55,001,40,083,44,230,40,082,08,187,35,084,36,309,38*64
//    $G2GSV,2,2,05,069,31,290,39*6C
//    $B2GSV,2,1,05,169,58,288,43,166,72,358,40,168,27,186,42,172,27,292,39*67
//    $B2GSV,2,2,05,173,44,211,39*60
    @Override
    public String toString() {
        if (this.msgFields == null || this.msgFields.size() < 8) {
            return "数据格式有误";
        }
        channels = new ArrayList<>();
        setNumberOfMessages(Integer.valueOf(this.msgFields.get(1)));
        setMessageNumber(Integer.valueOf(this.msgFields.get(2)));
        setSatellitesInView(Integer.valueOf(this.msgFields.get(3)));
//        int channelMessages = this.msgFields.size() - 5;
        if (getMessageNumber() < getNumberOfMessages()) {
            int j = 3;
            for (int i = 0; i < 4; i++) {
                ChannelInfo channelInfo = new ChannelInfo();
                channelInfo.setSatelliteID(this.msgFields.get(++j));
                channelInfo.setElevation(this.msgFields.get(++j));
                channelInfo.setAzimuth(this.msgFields.get(++j));
                channelInfo.setSNR(this.msgFields.get(++j));
                setChannels(channelInfo);
            }
        } else {
            int channelMessages = getSatellitesInView() % 4 == 0 ? 4 : getSatellitesInView() % 4;
            int j = 3;
            for (int i = 0; i < channelMessages; i++) {
                ChannelInfo channelInfo = new ChannelInfo();
                channelInfo.setSatelliteID(this.msgFields.get(++j));
                channelInfo.setElevation(this.msgFields.get(++j));
                channelInfo.setAzimuth(this.msgFields.get(++j));
                channelInfo.setSNR(this.msgFields.get(++j));
                setChannels(channelInfo);
            }
        }
        return "GsvNmeaObject{" +
                "numberOfMessages='" + numberOfMessages + '\'' +
                ", messageNumber='" + messageNumber + '\'' +
                ", satellitesInView='" + satellitesInView + '\'' +
                ", channels=" + channels +
                '}';
    }
}
