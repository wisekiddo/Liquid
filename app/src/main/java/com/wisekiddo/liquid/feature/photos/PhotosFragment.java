package com.wisekiddo.liquid.feature.photos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.wisekiddo.liquid.R;
import com.wisekiddo.liquid.data.model.Photo;
import com.wisekiddo.liquid.feature.photos.PhotosActivity;
import com.wisekiddo.liquid.feature.photos.PhotosContract;
import com.wisekiddo.liquid.feature.photos.PhotosRefreshLayout;
import com.wisekiddo.liquid.root.ActivityScoped;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ronald on 28/4/18.
 *
 * Main UI for the user detail screen.
 */

@ActivityScoped
public class PhotosFragment extends DaggerFragment implements PhotosContract.View {

    @Inject
    PhotosContract.Presenter presenter;

    private PhotoAdapter listAdapter;
    private View noView;
    private TextView mNoMainView;
    private LinearLayout linearLayout;
    private TextView mFilteringLabelView;

    @Inject
    public PhotosFragment() {
        // Requires empty public constructor
    }

    //Future Use
    public interface PhotoListener {
        void onClickPhoto(Photo clickPhoto);
    }

    PhotoListener photoListener = new PhotoListener() {
        @Override
        public void onClickPhoto(Photo clickedPhoto) {
            //presenter.openPhotos(clickedPhoto);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new PhotoAdapter(new ArrayList<Photo>(0), photoListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.generateView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //presenter.result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.photos_fragment, container, false);

        // Set up users view
        GridView gridView = root.findViewById(R.id.photos_list);
        gridView.setAdapter(listAdapter);
        //mFilteringLabelView = root.findViewById(R.id.filteringLabel);
        linearLayout = root.findViewById(R.id.photosLayout);

        // Set up  no users view
        noView = root.findViewById(R.id.noPhotos);
        mNoMainView = root.findViewById(R.id.noPhotosMain);

        // Set up progress indicator
        final PhotosRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(gridView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.reload(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }



    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showPhotos(List<Photo> photos) {
        listAdapter.replaceData(photos);
        linearLayout.setVisibility(View.VISIBLE);
        noView.setVisibility(View.GONE);
    }

    @Override
    public void showNoList() {
        showNoPhotosViews(
                getResources().getString(R.string.preparing_list),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    private void showNoPhotosViews(String mainText, int iconRes, boolean showAddView) {
        linearLayout.setVisibility(View.GONE);
        noView.setVisibility(View.VISIBLE);
        mNoMainView.setText(mainText);
    }


    @Override
    public void showLoadingError() {
        //showMessage(getString(R.string.loading_users_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }









    private static class PhotoAdapter extends BaseAdapter {

        private List<Photo> mPhotos;
        private PhotoListener mPhotoListener;

        public PhotoAdapter(List<Photo> photos, PhotoListener photoListener) {
            setList(photos);
            mPhotoListener = photoListener;
        }

        public void replaceData(List<Photo> photos) {
            setList(photos);
            notifyDataSetChanged();
        }

        private void setList(List<Photo> photos) {
            mPhotos = checkNotNull(photos);
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }

        @Override
        public Photo getItem(int i) {
            return mPhotos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.photo, viewGroup, false);
            }

            final Photo photo = getItem(i);

            TextView titleView = rowView.findViewById(R.id.title);
            titleView.setText(photo.getTitle().trim());

            TextView descriptionView = rowView.findViewById(R.id.description);
            descriptionView.setText(photo.getTitle());

            ImageView imageView = rowView.findViewById(R.id.image);
            Picasso.get().load(photo.getUrl())
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(imageView);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPhotoListener.onClickPhoto(photo);
                }
            });

            return rowView;
        }
    }

}
