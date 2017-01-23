package com.yang.nav.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Yang on 2017/1/22.
 */
@Entity
public class Frame {
    @Id(autoincrement = true)
    private Long id = null;
    //经度
    @NotNull
    private String content;

    @Generated(hash = 2018183415)
    public Frame(Long id, @NotNull String content) {
        this.id = id;
        this.content = content;
    }

    @Generated(hash = 1944764402)
    public Frame() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
