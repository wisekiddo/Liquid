package com.wisekiddo.liquid.data.source.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.wisekiddo.liquid.data.model.User;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by ronald on 28/4/18.
 *
 * Data Access Object for the users table.
 */

@Dao
public interface UsersDao {

    @Query("SELECT * FROM User")
    Flowable<List<User>> getUsers();

    @Query("SELECT * FROM User WHERE id = :userId")
    Flowable<User> getUserById(Integer userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

}
