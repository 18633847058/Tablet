package com.yang.nav.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.yang.nav.utils.TimeUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Yang on 2016/12/14.
 */
@Entity
public class Point implements Parcelable {
    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
    @Id(autoincrement = true)
    private Long id = null;
    //经度
    @NotNull
    private Double longitude;
    //纬度
    @NotNull
    private Double latitude;
    //时间
    @NotNull
    private Long time;
    //速度
    @NotNull
    private Double hor_speed;
    //速度
    @NotNull
    private Double ver_speed;
    //速度
    @NotNull
    private Double direction;


    protected Point(Parcel in) {
        this.id = in.readLong();
        this.time = in.readLong();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.hor_speed = in.readDouble();
        this.ver_speed = in.readDouble();
        this.direction = in.readDouble();
    }

    @Generated(hash = 1962998924)
    public Point(Long id, @NotNull Double longitude, @NotNull Double latitude,
                 @NotNull Long time, @NotNull Double hor_speed, @NotNull Double ver_speed,
                 @NotNull Double direction) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.hor_speed = hor_speed;
        this.ver_speed = ver_speed;
        this.direction = direction;
    }

    @Generated(hash = 1977038299)
    public Point() {
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", speed=" + hor_speed +
                ", time='" + TimeUtils.convertToStr(time, "yyyy-MM-dd HH:mm:ss") + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(time);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeDouble(hor_speed);
        dest.writeDouble(ver_speed);
        dest.writeDouble(direction);
    }

    public Double getHor_speed() {
        return this.hor_speed;
    }

    public void setHor_speed(Double hor_speed) {
        this.hor_speed = hor_speed;
    }

    public Double getVer_speed() {
        return this.ver_speed;
    }

    public void setVer_speed(Double ver_speed) {
        this.ver_speed = ver_speed;
    }

    public Double getDirection() {
        return this.direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }
}
