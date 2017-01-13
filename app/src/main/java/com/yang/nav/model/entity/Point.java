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
    private Double speed;

    @Generated(hash = 1493700571)
    public Point(Long id, @NotNull Double longitude, @NotNull Double latitude,
                 @NotNull Long time, @NotNull Double speed) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.speed = speed;
    }


    @Generated(hash = 1977038299)
    public Point() {
    }

    protected Point(Parcel in) {
        this.id = in.readLong();
        this.time = in.readLong();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.speed = in.readDouble();
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", speed=" + speed +
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

    public Double getSpeed() {
        return this.speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
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
        dest.writeDouble(speed);
    }
}
