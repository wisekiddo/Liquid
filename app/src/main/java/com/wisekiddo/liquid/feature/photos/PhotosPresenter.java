package com.wisekiddo.liquid.feature.photos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wisekiddo.liquid.data.model.Photo;
import com.wisekiddo.liquid.data.source.Repository;
import com.wisekiddo.liquid.feature.photos.PhotosContract;
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
final class PhotosPresenter implements PhotosContract.Presenter {

    @NonNull
    private final Repository repository;

    @NonNull
    private PhotosContract.View photosView;

    @NonNull
    private final BaseSchedulerProvider schedulerProvider = SchedulerProvider.getInstance();

    private boolean firstLoad = true;

    private String albumId;


    @NonNull
    private CompositeDisposable compositeDisposable;

    @Inject
    PhotosPresenter(@Nullable String albumId, Repository repository) {
        this.repository = repository;
        this.albumId = albumId;
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void reload(boolean forceUpdate) {
        Log.i("--->","--");

        // Simplification for sample: a network reload will be forced on first load.
        loadPhotos(forceUpdate || firstLoad, true);
        firstLoad = false;
    }

    private void loadPhotos(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            if (photosView != null) {
                photosView.setLoadingIndicator(true);
            }
        }
        if (forceUpdate) {
            repository.refreshPhotos();
        }

        EspressoIdlingResource.increment(); // App is busy until further notice
        Log.e("----->", albumId+"***");
        compositeDisposable.clear();
        Disposable disposable = repository
                .getPhotos(Integer.parseInt(albumId))
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
                        photos -> {
                            Log.i("--->",photos.get(0).getTitle()+"");
                            processPhoto(photos);
                            photosView.setLoadingIndicator(false);
                        },
                        // onError
                        throwable -> photosView.showLoadingError());

        compositeDisposable.add(disposable);

    }

    private void processPhoto(List<Photo> photos) {
        if (photos.isEmpty()) {
            processEmptyPhotos();
        } else {
            photosView.showPhotos(photos);
        }
    }

    private void processEmptyPhotos() {
        photosView.showNoList();
    }


    @Override
    public void generateView(PhotosContract.View view) {
        photosView = view;
        reload(false);
    }

    @Override
    public void dropView() {
        photosView = null;
    }

}
