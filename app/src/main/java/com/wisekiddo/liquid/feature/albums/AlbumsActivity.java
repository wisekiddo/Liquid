package com.wisekiddo.liquid.feature.albums;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.wisekiddo.liquid.R;
import com.wisekiddo.liquid.util.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by ronald on 28/4/18.
 *
 * Displays user details screen.
 */

public class AlbumsActivity extends DaggerAppCompatActivity {
    public static final String EXTRA_ITEM_ID = "ITEM_ID";
    @Inject
    AlbumsFragment injectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums_activity);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        AlbumsFragment albumsFragment = (AlbumsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (albumsFragment == null) {
            albumsFragment = injectedFragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    albumsFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
