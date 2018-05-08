package com.wisekiddo.liquid.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.wisekiddo.liquid.data.source.local.ApplicationDatabase;
import com.wisekiddo.liquid.data.source.local.dao.AlbumsDao;
import com.wisekiddo.liquid.data.source.local.dao.UsersDao;
import com.wisekiddo.liquid.data.source.local.Local;
import com.wisekiddo.liquid.data.source.local.LocalDataSource;
import com.wisekiddo.liquid.data.source.remote.Remote;
import com.wisekiddo.liquid.data.source.remote.RemoteDataSource;
import com.wisekiddo.liquid.util.ApplicationExecutors;
import com.wisekiddo.liquid.util.DiskIOThreadExecutor;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class RepositoryModule {

    private static final int THREAD_COUNT = 3;

    @Singleton
    @Provides
    @Local
    DataSource provideLocalDataSource(UsersDao usersDao, AlbumsDao albumDao) {
        return new LocalDataSource(usersDao,albumDao);
    }

    @Singleton
    @Provides
    @Remote
    DataSource provideUsersRemoteDataSource() {
        return new RemoteDataSource();
    }

    @Singleton
    @Provides
    ApplicationDatabase provideDb(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), ApplicationDatabase.class, "Liquid.db")
                .build();
    }

    @Singleton
    @Provides
    UsersDao provideUsersDao(ApplicationDatabase db) {
        return db.usersDao();
    }

    @Singleton
    @Provides
    AlbumsDao provideAlbumDao(ApplicationDatabase db) {
        return db.albumDao();
    }

    @Singleton
    @Provides
    ApplicationExecutors provideAppExecutors() {
        return new ApplicationExecutors(new DiskIOThreadExecutor(),
                Executors.newFixedThreadPool(THREAD_COUNT),
                new ApplicationExecutors.MainThreadExecutor());
    }
}
