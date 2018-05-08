
package com.wisekiddo.liquid.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Album")
public class Album {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @NonNull
    private Integer id;

    @SerializedName("userId")
    @Expose
    @Nullable
    @ColumnInfo(name = "userId")
    private Integer userId;

    @SerializedName("title")
    @Expose
    @Nullable
    @ColumnInfo(name = "title")
    private String title;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
