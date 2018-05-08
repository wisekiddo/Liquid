package com.wisekiddo.liquid.root;

import com.wisekiddo.liquid.feature.albums.AlbumsActivity;
import com.wisekiddo.liquid.feature.albums.AlbumsModule;
import com.wisekiddo.liquid.feature.users.UsersActivity;
import com.wisekiddo.liquid.feature.users.UsersModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ronald on 28/4/18.
 */

@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = UsersModule.class)
    abstract UsersActivity usersActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AlbumsModule.class)
    abstract AlbumsActivity albumsActivity();
}