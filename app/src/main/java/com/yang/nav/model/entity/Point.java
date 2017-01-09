package com.yang.nav.model.entity;

import com.yang.nav.utils.TimeUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Yang on 2016/12/14.
 */
@Entity
public class Point {
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


    @Generated(hash = 959848393)
    public Point(Long id, @NotNull Double longitude, @NotNull Double latitude,
            @NotNull Long time) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
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
                ", time='" + TimeUtils.convertToNormalStr(time) + '\'' +
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
}
