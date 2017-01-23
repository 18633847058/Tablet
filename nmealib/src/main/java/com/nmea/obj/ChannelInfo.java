package com.nmea.obj;

/**
 * Created by Yang on 2017/1/19.
 */
public class ChannelInfo {

    private String satelliteID;

    private String elevation;

    private String azimuth;

    private String SNR;

    public String getSatelliteID() {
        return satelliteID;
    }

    public void setSatelliteID(Object satelliteID) {
        this.satelliteID = (String) satelliteID;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(Object elevation) {
        this.elevation = (String) elevation;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(Object azimuth) {
        this.azimuth = (String) azimuth;
    }

    public String getSNR() {
        return SNR;
    }

    public void setSNR(Object SNR) {
        this.SNR = (String) SNR;
    }

    @Override
    public String toString() {
        return satelliteID + "," + elevation + "," + azimuth + "," + SNR;
    }
}
