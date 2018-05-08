package com.wisekiddo.liquid.data.source.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by ronald on 28/4/18.
 *
 * Data Access Object for the album table.
 */

@Dao
public interface AlbumsDao {

    @Query("SELECT * FROM Album WHERE userId = :userId")
    Flowable<List<Album>> getAlbums(Integer userId);

    @Query("SELECT * FROM Album WHERE id = :userId")
    Flowable<Album> getAlbumsByUser(Integer userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbum(Album album);

}
