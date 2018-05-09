package com.wisekiddo.liquid.data.source.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.Photo;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by ronald on 28/4/18.
 *
 * Data Access Object for the album table.
 */

@Dao
public interface PhotosDao {

    @Query("SELECT * FROM Photo WHERE albumId = :albumId")
    Flowable<List<Photo>> getPhotos(Integer albumId);

    //Future use
    @Query("SELECT * FROM Photo WHERE id = :id")
    Flowable<Photo> getPhotosById(Integer id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbum(Photo photo);

}
