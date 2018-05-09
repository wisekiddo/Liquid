
package com.wisekiddo.liquid.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Photo")
public class Photo {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @NonNull
    private Integer id;

    @SerializedName("albumId")
    @Expose
    @Nullable
    @ColumnInfo(name = "albumId")
    private Integer albumId;

    @SerializedName("title")
    @Expose
    @Nullable
    @ColumnInfo(name = "title")
    private String title;

    @SerializedName("url")
    @Expose
    @Nullable
    @ColumnInfo(name = "url")
    private String url;


    @SerializedName("thumbnailUrl")
    @Expose
    @Nullable
    @ColumnInfo(name = "thumbnailUrl")
    private String thumbnailUrl;

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

}
