package com.wisekiddo.liquid.feature.albums;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wisekiddo.liquid.R;
import com.wisekiddo.liquid.data.model.Album;
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
public class AlbumsFragment extends DaggerFragment implements AlbumsContract.View {

    @Inject
    AlbumsContract.Presenter presenter;

    private AlbumAdapter listAdapter;
    private View noView;
    private TextView mNoMainView;
    private LinearLayout linearLayout;
    private TextView mFilteringLabelView;

    @Inject
    public AlbumsFragment() {
        // Requires empty public constructor
    }

    public interface AlbumListener {
        void onClickAlbum(Album clickAlbum);
    }

    AlbumListener albumListener = new AlbumListener() {
        @Override
        public void onClickAlbum(Album clickedAlbum) {
            presenter.openDetails(clickedAlbum);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new AlbumAdapter(new ArrayList<Album>(0), albumListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
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
        View root = inflater.inflate(R.layout.albums_fragment, container, false);

        // Set up users view
        ListView listView = root.findViewById(R.id.albums_list);
        listView.setAdapter(listAdapter);
        //mFilteringLabelView = root.findViewById(R.id.filteringLabel);
        linearLayout = root.findViewById(R.id.albumsLayout);

        // Set up  no users view
        noView = root.findViewById(R.id.noAlbums);
        mNoMainView = root.findViewById(R.id.noAlbumsMain);


        // Set up progress indicator
        final AlbumsRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

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
    public void showAlbums(List<Album> albums) {
        listAdapter.replaceData(albums);

        linearLayout.setVisibility(View.VISIBLE);
        noView.setVisibility(View.GONE);
    }

    @Override
    public void showNoList() {
        showNoAlbumsViews(
                getResources().getString(R.string.preparing_list),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    private void showNoAlbumsViews(String mainText, int iconRes, boolean showAddView) {
        linearLayout.setVisibility(View.GONE);
        noView.setVisibility(View.VISIBLE);
        mNoMainView.setText(mainText);
    }

    @Override
    public void showDetailsUi(Integer userId) {
        //Shown in it's own Activity, since it makes more sense that way
        // and it gives us the flexibility to show some Intent stubbing.
        Intent intent = new Intent(getContext(), AlbumsActivity.class);
        intent.putExtra(AlbumsActivity.EXTRA_ITEM_ID, userId.toString());
        startActivity(intent);
    }

    @Override
    public void showLoadingError() {
        //showMessage(getString(R.string.loading_users_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }









    private static class AlbumAdapter extends BaseAdapter {

        private List<Album> mAlbums;
        private AlbumListener mAlbumListener;
        private static final String BASE_IMAGE_URL = "http://media.redmart.com/newmedia/200p";


        public AlbumAdapter(List<Album> albums, AlbumListener albumListener) {
            setList(albums);
            mAlbumListener = albumListener;
        }

        public void replaceData(List<Album> albums) {
            setList(albums);
            notifyDataSetChanged();
        }

        private void setList(List<Album> albums) {
            mAlbums = checkNotNull(albums);
        }

        @Override
        public int getCount() {
            return mAlbums.size();
        }

        @Override
        public Album getItem(int i) {
            return mAlbums.get(i);
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
                rowView = inflater.inflate(R.layout.album, viewGroup, false);
            }

            final Album album = getItem(i);

            TextView titleView = rowView.findViewById(R.id.title);
            titleView.setText(album.getTitle().trim());

            TextView descriptionView = rowView.findViewById(R.id.description);
            descriptionView.setText(album.getTitle());


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlbumListener.onClickAlbum(album);
                }
            });

            return rowView;
        }
    }

}
