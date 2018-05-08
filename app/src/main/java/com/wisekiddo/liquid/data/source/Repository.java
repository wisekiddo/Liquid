package com.wisekiddo.liquid.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.data.source.local.Local;
import com.wisekiddo.liquid.data.source.remote.Remote;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class Repository implements DataSource {

    @Nullable
    private static Repository INSTANCE = null;
    @NonNull
    private final DataSource remoteDataSource;
    @NonNull
    private final DataSource localDataSource;

    @VisibleForTesting
    @Nullable
    Map<Integer, User> cachedUsers;

    @VisibleForTesting
    @Nullable
    Map<Integer, Album> cachedAlbums;

    @VisibleForTesting
    boolean cacheIsDirty = false;

    @Inject
    Repository(@Remote DataSource remoteDataSource,
               @Local DataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
    }
    public static Repository getInstance(@NonNull  DataSource usersRemoteDataSource,
                                         @NonNull DataSource usersLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(usersRemoteDataSource, usersLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public Flowable<List<User>> getUsers() {
        // Respond immediately with cache if available and not dirty
        if (cachedUsers != null && !cacheIsDirty) {
            return Flowable.fromIterable(cachedUsers.values()).toList().toFlowable();
        } else if (cachedUsers == null) {
            cachedUsers = new LinkedHashMap<>();
        }

        Flowable<List<User>> remoteUsers = getAndSaveRemoteUsers();

        if (cacheIsDirty) {
            return remoteUsers;
        } else {
            // Query the local storage if available. If not, query the network.
            Flowable<List<User>> localUsers = getAndCacheLocalUsers();
            return Flowable.concat(localUsers, remoteUsers)
                    .filter(users -> !users.isEmpty())
                    .firstOrError()
                    .toFlowable();
        }
    }
    private Flowable<List<User>> getAndCacheLocalUsers() {
        return localDataSource.getUsers()
                .flatMap(users -> Flowable.fromIterable(users)
                        .doOnNext(user -> cachedUsers.put(user.getId(), user))
                        .toList()
                        .toFlowable());
    }

    private Flowable<List<User>> getAndSaveRemoteUsers() {
        return remoteDataSource
                .getUsers()
                .flatMap(users -> Flowable.fromIterable(users).doOnNext(user -> {
                    localDataSource.saveUser(user);
                    cachedUsers.put(user.getId(), user);
                }).toList().toFlowable())
                .doOnComplete(() -> cacheIsDirty = false);
    }

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        remoteDataSource.saveUser(user);
        localDataSource.saveUser(user);

        if (cachedUsers == null) {
            cachedUsers = new LinkedHashMap<>();
        }
        cachedUsers.put(user.getId(), user);
    }

    @Override
    public Flowable<User> getUser(@NonNull final Integer userId) {
        checkNotNull(userId);

        final User cachedUser = getUserWithId(userId);

        if (cachedUser != null) {
            return Flowable.just(cachedUser);
        }

        if (cachedUsers == null) {
            cachedUsers = new LinkedHashMap<>();
        }

        Flowable<User> localUser = getUserWithIdFromLocalRepository(userId);
        Flowable<User> remoteUser = remoteDataSource.getUser(userId).doOnNext(user -> {
            if (user != null) {
                localDataSource.saveUser(user);
                cachedUsers.put(user.getId(), user);
            }
        });

        return Flowable.concat(localUser, remoteUser)
                .firstElement()
                .toFlowable();
    }

    @Override
    public void refreshUsers() {
        cacheIsDirty = true;
    }

    @Override
    public void deleteAllUsers() {
        remoteDataSource.deleteAllUsers();
        localDataSource.deleteAllUsers();

        if (cachedUsers == null) {
            cachedUsers = new LinkedHashMap<>();
        }
        cachedUsers.clear();
    }

    @Override
    public void deleteUser(@NonNull Integer userId) {
        remoteDataSource.deleteUser(checkNotNull(userId));
        localDataSource.deleteUser(checkNotNull(userId));

        cachedUsers.remove(userId);
    }

    @Nullable
    private User getUserWithId(@NonNull Integer id) {
        checkNotNull(id);
        if (cachedUsers == null || cachedUsers.isEmpty()) {
            return null;
        } else {
            return cachedUsers.get(id);
        }
    }

    @NonNull
    private Flowable<User> getUserWithIdFromLocalRepository(@NonNull final Integer id) {
        return localDataSource
                .getUser(id)
                .firstElement().toFlowable();
    }





    //************************************************//
    //                  FOR ALBUMS                    //
    //************************************************//

    @Override
    public Flowable<List<Album>> getAlbums(@NonNull Integer userId) {
        // Respond immediately with cache if available and not dirty
        if (cachedAlbums != null && !cacheIsDirty) {
            return Flowable.fromIterable(cachedAlbums.values()).toList().toFlowable();
        } else if (cachedAlbums == null) {
            cachedAlbums = new LinkedHashMap<>();
        }

        Flowable<List<Album>> remoteAlbums = getAndSaveRemoteAlbums(userId);

        if (cacheIsDirty) {
            return remoteAlbums;
        } else {
            // Query the local storage if available. If not, query the network.
            Flowable<List<Album>> localAlbums = getAndCacheLocalAlbums(userId);

            return Flowable.concat(localAlbums, remoteAlbums)
                    .filter(albums -> !albums.isEmpty())
                    .firstOrError()
                    .toFlowable();
        }
    }

    private Flowable<List<Album>> getAndCacheLocalAlbums(@NonNull Integer userId) {
        return localDataSource.getAlbums(userId)
                .flatMap(albums -> Flowable.fromIterable(albums)
                        .doOnNext(album -> {
                            cachedAlbums.put(album.getId(), album);
                            Log.e("LOCAL_REPOSITORY", album.getTitle());
                        })
                        .toList()
                        .toFlowable());
    }

    private Flowable<List<Album>> getAndSaveRemoteAlbums(@NonNull Integer userId) {
        return remoteDataSource
                .getAlbums(userId)
                .flatMap(albums -> Flowable.fromIterable(albums).doOnNext(album -> {
                    localDataSource.saveAlbum(album);
                    cachedAlbums.put(album.getId(), album);
                    Log.e("REMOTE_REPOSITORY", album.getTitle());

                }).toList().toFlowable())
                .doOnComplete(() -> cacheIsDirty = false);
    }

    @Override
    public Flowable<Album> getAlbum(@NonNull Integer userId) {
        checkNotNull(userId);

        final Album cachedAlbum = getAlbumWithId(userId);

        if (cachedAlbum != null) {
            return Flowable.just(cachedAlbum);
        }

        if (cachedAlbums == null) {
            cachedAlbums = new LinkedHashMap<>();
        }

        Flowable<Album> localAlbum = getAlbumWithIdFromLocalRepository(userId);
        Flowable<Album> remoteAlbum = remoteDataSource.getAlbum(userId).doOnNext(album -> {
            if (album != null) {
                localDataSource.saveAlbum(album);
                cachedAlbums.put(album.getId(), album);
            }
        });

        return Flowable.concat(localAlbum, remoteAlbum)
                .firstElement()
                .toFlowable();
    }

    @Override
    public void saveAlbum(@NonNull Album album) {
        checkNotNull(album);
        remoteDataSource.saveAlbum(album);
        localDataSource.saveAlbum(album);

        if (cachedAlbums == null) {
            cachedAlbums = new LinkedHashMap<>();
        }
        cachedAlbums.put(album.getId(), album);
    }

    @Nullable
    private Album getAlbumWithId(@NonNull Integer id) {
        checkNotNull(id);
        if (cachedAlbums == null || cachedAlbums.isEmpty()) {
            return null;
        } else {
            return cachedAlbums.get(id);
        }
    }

    @NonNull
    private Flowable<Album> getAlbumWithIdFromLocalRepository(@NonNull final Integer id) {
        return localDataSource
                .getAlbum(id)
                .firstElement().toFlowable();
    }

    @Override
    public void refreshAlbums() {
        cacheIsDirty = true;
    }
}
