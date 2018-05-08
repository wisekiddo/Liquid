package com.wisekiddo.liquid.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.data.source.DataSource;
import com.wisekiddo.liquid.data.source.local.dao.AlbumsDao;
import com.wisekiddo.liquid.data.source.local.dao.UsersDao;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * *Created by ronald on 28/4/18.
 *
 * Concrete implementation of a data source as a db.
 */

@Singleton
public class LocalDataSource implements DataSource {

    private final UsersDao usersDao;
    private final AlbumsDao albumsDao;

    @Nullable
    private static LocalDataSource INSTANCE;

    @Inject
    public LocalDataSource(@NonNull UsersDao userDao, @NonNull AlbumsDao albumDao) {
        usersDao = userDao;
        albumsDao = albumDao;
    }

    public static LocalDataSource getInstance(UsersDao userDao,AlbumsDao albumDao) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(userDao, albumDao);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Flowable<List<User>> getUsers() {
        return  usersDao.getUsers();
    }

    @Override
    public Flowable<User> getUser(@NonNull Integer userId) {
        return  usersDao.getUserById(userId);
    }

    @Override
    public Flowable<List<Album>> getAlbums(@NonNull Integer userId) {return  albumsDao.getAlbums(userId);}

    @Override
    public Flowable<Album> getAlbum(@NonNull Integer userId) {return albumsDao.getAlbumsByUser(userId);}

    @Override
    public void saveUser(@NonNull final User user) {
        checkNotNull(user);
        Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    usersDao.insertUser(user);
                } catch (Exception e) {
                   Log.e(this.getClass().getSimpleName(),e.getMessage());
                }
                return null;
            }
        });
    }

    @Override
    public void saveAlbum(@NonNull final Album album) {
        checkNotNull(album);
        Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    albumsDao.insertAlbum(album);
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(),e.getMessage());
                }
                return null;
            }
        });
    }




    @Override
    public void refreshUsers() {
    }

    @Override
    public void refreshAlbums() {
    }

    @Override
    public void deleteAllUsers() {

        Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    //usersDao.deleteUsers();
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(),e.getMessage());
                }
                return null;
            }
        });
    }

    @Override
    public void deleteUser(@NonNull final Integer taskId) {

        Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    //usersDao.deleteUserById(taskId);
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(),e.getMessage());
                }
                return null;
            }
        });
    }
}
