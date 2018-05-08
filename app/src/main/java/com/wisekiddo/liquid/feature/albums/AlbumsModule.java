package com.wisekiddo.liquid.feature.albums;

import com.wisekiddo.liquid.root.ActivityScoped;
import com.wisekiddo.liquid.root.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wisekiddo.liquid.feature.albums.AlbumsActivity.EXTRA_ITEM_ID;

/**
 * Created by ronald on 16/3/18.
 *
 */

@Module
public abstract class AlbumsModule {


    @FragmentScoped
    @ContributesAndroidInjector
    abstract AlbumsFragment albumsFragment();

    @ActivityScoped
    @Binds
    abstract AlbumsContract.Presenter albumsPresenter(AlbumsPresenter presenter);

    @Provides
    @ActivityScoped
    static String provideAlbumId(AlbumsActivity activity) {
        return activity.getIntent().getStringExtra(EXTRA_ITEM_ID);
    }
}
