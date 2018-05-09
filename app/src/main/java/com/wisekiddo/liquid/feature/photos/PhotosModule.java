package com.wisekiddo.liquid.feature.photos;

import com.wisekiddo.liquid.root.ActivityScoped;
import com.wisekiddo.liquid.root.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wisekiddo.liquid.feature.photos.PhotosActivity.EXTRA_ITEM_ID;

/**
 * Created by ronald on 16/3/18.
 *
 */

@Module
public abstract class PhotosModule {


    @FragmentScoped
    @ContributesAndroidInjector
    abstract PhotosFragment photosFragment();

    @ActivityScoped
    @Binds
    abstract PhotosContract.Presenter photosPresenter(PhotosPresenter presenter);

    @Provides
    @ActivityScoped
    static String providePhotoId(PhotosActivity activity) {
        return activity.getIntent().getStringExtra(EXTRA_ITEM_ID);
    }
}
