package com.nmea.obj;

import java.util.List;

/**
 * Created by Yang on 2017/1/19.
 */

public class GsvNmeaObject extends AbstractNmeaObject {

    private String numberOfMessages;

    private String messageNumber;

    private String satellitesInView;

    private List<ChannelInfo> channels;

    public GsvNmeaObject() {
        this.objType = GSV_PROTOL;
    }

    public String getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(String numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public String getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(String messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getSatellitesInView() {
        return satellitesInView;
    }

    public void setSatellitesInView(String satellitesInView) {
        this.satellitesInView = satellitesInView;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        if (this.msgFields == null) {
            return "数据格式有误";
        }


        return "GsvNmeaObject{" +
                "numberOfMessages='" + numberOfMessages + '\'' +
                ", messageNumber='" + messageNumber + '\'' +
                ", satellitesInView='" + satellitesInView + '\'' +
                ", channels=" + channels +
                '}';
    }
}
