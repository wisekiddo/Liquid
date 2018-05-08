package com.wisekiddo.liquid.feature.users;

import com.wisekiddo.liquid.root.ActivityScoped;
import com.wisekiddo.liquid.root.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ronald on 28/4/18.
 */


@Module
public abstract class UsersModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract UsersFragment usersFragment();

    @ActivityScoped
    @Binds
    abstract UsersContract.Presenter usersPresenter(UsersPresenter presenter);
}