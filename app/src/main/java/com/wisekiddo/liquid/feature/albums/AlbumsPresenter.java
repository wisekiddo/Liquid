package com.wisekiddo.liquid.feature.albums;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Optional;
import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.data.source.Repository;
import com.wisekiddo.liquid.root.ActivityScoped;
import com.wisekiddo.liquid.util.EspressoIdlingResource;
import com.wisekiddo.liquid.util.schedulers.BaseSchedulerProvider;
import com.wisekiddo.liquid.util.schedulers.SchedulerProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * *Created by ronald on 28/4/18.
 */

@ActivityScoped
final class AlbumsPresenter implements AlbumsContract.Presenter {

    @NonNull
    private final Repository repository;

    @NonNull
    private AlbumsContract.View albumsView;

    @NonNull
    private final BaseSchedulerProvider schedulerProvider = SchedulerProvider.getInstance();

    private boolean firstLoad = true;

    private String userId;


    @NonNull
    private CompositeDisposable compositeDisposable;

    @Inject
    AlbumsPresenter(@Nullable String userId, Repository repository) {
        this.repository = repository;
        this.userId = userId;
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void reload(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadAlbums(forceUpdate || firstLoad, true);
        getSelectedUser();

        firstLoad = false;
    }

    private void loadAlbums(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            if (albumsView != null) {
                albumsView.setLoadingIndicator(true);
            }
        }
        if (forceUpdate) {
            repository.refreshAlbums();
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        compositeDisposable.clear();
        Disposable disposable = repository
                .getAlbums(Integer.parseInt(userId))
                .flatMap(Flowable::fromIterable)
                .toList()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
                .subscribe(
                        // onNext
                        albums -> {
                            processAlbum(albums);
                            albumsView.setLoadingIndicator(false);
                        },
                        // onError
                        throwable -> albumsView.showLoadingError());

        compositeDisposable.add(disposable);

    }

    private void processAlbum(List<Album> albums) {
        if (albums.isEmpty()) {
            processEmptyAlbums();
        } else {
            albumsView.showAlbums(albums);
        }
    }


    private void getSelectedUser(){
        compositeDisposable.add(repository
                .getUser(Integer.parseInt(userId))
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        // onNext
                        this::provideHeaderData,
                        // onError
                        throwable -> {
                        },
                        // onCompleted
                        () -> albumsView.setLoadingIndicator(false)));
    }

    private void provideHeaderData(@NonNull User user) {
        albumsView.showHeader(user);
    }

    private void processEmptyAlbums() {
        albumsView.showNoList();
    }

    @Override
    public void openPhotos(@NonNull Album requestedAlbum) {
        checkNotNull(requestedAlbum, "requestedAlbum cannot be null!");
        if (albumsView != null) {
            albumsView.showPhotos(requestedAlbum.getId());
        }
    }

    @Override
    public void generateView(AlbumsContract.View view) {
        albumsView = view;
        reload(false);

    }

    @Override
    public void dropView() {
        albumsView = null;
    }

}
