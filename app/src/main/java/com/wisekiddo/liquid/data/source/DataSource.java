package com.wisekiddo.liquid.data.source;

import android.support.annotation.NonNull;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.Photo;
import com.wisekiddo.liquid.data.model.User;

import java.util.List;

import io.reactivex.Flowable;


public interface DataSource {

    Flowable<List<User>> getUsers();
    Flowable<User> getUser(@NonNull Integer userId);

    Flowable<List<Album>> getAlbums(@NonNull Integer userId);
    Flowable<Album> getAlbum(@NonNull Integer userId);

    Flowable<List<Photo>> getPhotos(@NonNull Integer albumId);
    Flowable<Photo> getPhoto(@NonNull Integer albumId);

    void saveUser(@NonNull User user);
    void saveAlbum(@NonNull Album album);
    void savePhoto(@NonNull Photo photo);

    void refreshUsers();
    void refreshAlbums();
    void refreshPhotos();

    //For future use
    void deleteAllUsers();
    void deleteUser(@NonNull Integer userId);

}
