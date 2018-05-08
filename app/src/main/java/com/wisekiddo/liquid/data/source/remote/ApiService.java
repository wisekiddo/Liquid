package com.wisekiddo.liquid.data.source.remote;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 *  * Created by ronald on 16/3/18.
 */

public interface ApiService {



    @GET("user/{id}")
    Flowable<User> getUser(
            @Path("id") int id);

    @GET("users/")
    Flowable<List<User>> getUsers();

    @GET("albums{userId}")
    Flowable<List<Album>> getAlbums(
            @Query("userId") Integer id);

}
