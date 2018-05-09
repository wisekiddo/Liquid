package com.wisekiddo.liquid.feature.photos;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.wisekiddo.liquid.R;
import com.wisekiddo.liquid.feature.photos.PhotosFragment;
import com.wisekiddo.liquid.util.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by ronald on 28/4/18.
 *
 * Displays user_cardview details screen.
 */

public class PhotosActivity extends DaggerAppCompatActivity {
    public static final String EXTRA_ITEM_ID = "ITEM_ID";
    @Inject
    PhotosFragment injectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        PhotosFragment photosFragment = (PhotosFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (photosFragment == null) {
            photosFragment = injectedFragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    photosFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
