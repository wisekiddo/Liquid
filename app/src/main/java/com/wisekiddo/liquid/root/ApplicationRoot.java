package com.wisekiddo.liquid.root;

import android.support.annotation.VisibleForTesting;

import com.wisekiddo.liquid.data.source.Repository;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by ronald on 28/4/18.
 */

public class ApplicationRoot extends DaggerApplication {

    @Inject
    Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent.builder().application(this).build();
    }


    /**
     * Our Espresso tests need to be able to get an instance of the {@link Repository}
     * so that we can delete all tasks before running each test
     */
    @VisibleForTesting
    public Repository getRepository() {
        return repository;
    }
}
