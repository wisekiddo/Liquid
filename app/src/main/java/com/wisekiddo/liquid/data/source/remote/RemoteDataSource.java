package com.wisekiddo.liquid.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.data.source.DataSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import io.reactivex.Flowable;

/**
 * *Created by ronald on 13/3/18.
 */

@Singleton
public class RemoteDataSource implements DataSource {

    private static final Map<Integer, User> mapUsers;
    private static final Map<Integer, Album> mapAlbums;

    private ApiService service;

    private static RemoteDataSource INSTANCE;
    
    static {
        //For test, TODO: make a mockito
        mapUsers = new LinkedHashMap<>();
        mapAlbums = new LinkedHashMap<>();
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    public RemoteDataSource(){

    }

    private static void addUser(User user) {
        mapUsers.put(user.getId(), user);
    }

    @Override
    public Flowable<List<User>> getUsers() {
        service = ApiClient.getClient().create(ApiService.class);
        return  service.getUsers()
                .flatMap(users -> Flowable.fromIterable(users).doOnNext(user -> {
                    addUser(user);
                }).toList().toFlowable());
    }


    @Override
    public Flowable<User> getUser(@NonNull Integer userId) {
        final User user = mapUsers.get(userId);
        if (user != null) {
            return Flowable.just(user);
        } else {
            return Flowable.empty();
        }
    }

    @Override
    public Flowable<List<Album>> getAlbums(Integer id) {
        service = ApiClient.getClient().create(ApiService.class);
        return  service.getAlbums(id)
                .flatMap(albums -> Flowable.fromIterable(albums).doOnNext(album -> {
                    addAlbum(album);
                    Log.e("API_QUERY", album.getTitle());

                }).toList().toFlowable());
    }

    private static void addAlbum(Album album) {mapAlbums.put(album.getId(), album);
    }


    @Override
    public Flowable<Album> getAlbum(@NonNull Integer userId) {
        final Album album = mapAlbums.get(userId);
        if (album != null) {
            return Flowable.just(album);
        } else {
            return Flowable.empty();
        }
    }

    @Override
    public void saveAlbum(@NonNull Album album) {
        mapAlbums.put(album.getId(), album);
    }

    @Override
    public void saveUser(@NonNull User user) {
        mapUsers.put(user.getId(), user);
    }



    @Override
    public void refreshUsers() {
        //Future Use
    }

    @Override
    public void refreshAlbums() {

    }

    @Override
    public void deleteAllUsers() {
        mapUsers.clear();
    }

    @Override
    public void deleteUser(@NonNull Integer userId) {
        mapUsers.remove(userId);
    }

}
