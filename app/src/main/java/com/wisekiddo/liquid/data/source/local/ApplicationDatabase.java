package com.wisekiddo.liquid.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.data.source.local.dao.AlbumsDao;
import com.wisekiddo.liquid.data.source.local.dao.UsersDao;

/**
 * Created by ronald on 13/3/18.
 *
 * The Room ApplicationDatabase that contains the User table.
 */

@Database(entities = {User.class, Album.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {

    public abstract UsersDao usersDao();
    public abstract AlbumsDao albumDao();
}
