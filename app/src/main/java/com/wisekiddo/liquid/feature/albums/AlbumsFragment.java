package com.wisekiddo.liquid.feature.albums;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wisekiddo.liquid.R;
import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.feature.photos.PhotosActivity;
import com.wisekiddo.liquid.root.ActivityScoped;
import com.wisekiddo.liquid.util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ronald on 28/4/18.
 *
 * Main UI for the user_cardview detail screen.
 */

@ActivityScoped
public class AlbumsFragment extends DaggerFragment implements AlbumsContract.View {

    @Inject
    AlbumsContract.Presenter presenter;

    private AlbumAdapter listAdapter;
    private View noView;
    private TextView mNoMainView;
    private RelativeLayout relativeLayout;
    private TextView mFilteringLabelView;

    @Inject
    public AlbumsFragment() {
        // Requires empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new AlbumAdapter(new ArrayList<Album>(0));
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
        View root = inflater.inflate(R.layout.albums_fragment, container, false);

       // initCollapsingToolbar(root);
        // Set up users view
        //ListView listView = root.findViewById(R.id.albums_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        RecyclerView recyclerView = root.findViewById(R.id.albums_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position > 0) {
                    Album album = listAdapter.albumList.get(position - 1);
                    presenter.openPhotos(album);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //For future use
            }
        }));

        final CollapsingToolbarLayout collapsingToolbar = getActivity().findViewById(R.id.collapsing_toolbar);
        // collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.appbar);
//        appBarLayout.setExpanded(true, true);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(recyclerView.getChildViewHolder(recyclerView.getChildAt(0)).getLayoutPosition()>0){

                    toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

                   // appBarLayout.setExpanded(false, true);

                }else{
                   // appBarLayout.setExpanded(true, true);

                }
            }
        });

        //mFilteringLabelView = root.findViewById(R.id.filteringLabel);
        relativeLayout = getActivity().findViewById(R.id.albumsLayout);

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
        //swipeRefreshLayout.setScrollUpChild(listView);

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
        relativeLayout.setVisibility(View.VISIBLE);
        noView.setVisibility(View.GONE);
    }

    @Override
    public void showHeader(User user){
        String strName, strAddress, strEmail, strTitle;
        strName = user.getName() + "("+user.getUsername()+")";
        strAddress = user.getAddress().getStreet() + " " +
                user.getAddress().getSuite() + " " +
                user.getAddress().getCity() + " " +
                user.getAddress().getZipcode();
        strEmail = user.getEmail();
        strTitle = "Albums";
        listAdapter.headerViewHolder.setValues(strName,strAddress,strEmail,strTitle);
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
        relativeLayout.setVisibility(View.GONE);
        noView.setVisibility(View.VISIBLE);
        mNoMainView.setText(mainText);
    }

    @Override
    public void showPhotos(Integer albumId) {
        Intent intent = new Intent(getContext(), PhotosActivity.class);
        intent.putExtra(PhotosActivity.EXTRA_ITEM_ID, albumId.toString());
        startActivity(intent);
    }

    @Override
    public void showLoadingError() {
        //showMessage(getString(R.string.loading_users_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }



    //*********************//
    //**********Adapter***//

    public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Album> albumList;
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private HeaderViewHolder headerViewHolder;

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {
                return TYPE_HEADER;

            }
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        private boolean isPositionFooter(int position) {
            return position >= albumList.size();
        }

        public void replaceData(List<Album> albums) {
            setList(albums);
            notifyDataSetChanged();
        }

        private void setList(List<Album> albums) {
            this.albumList = checkNotNull(albums);
        }
        public AlbumAdapter(List<Album> albumList) {
            this.albumList = albumList;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.album_header, viewGroup, false);
                headerViewHolder = new HeaderViewHolder(view);
                return headerViewHolder;

            } else if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.album_cardview, viewGroup, false);
                return new ItemViewHolder(view);
            }
            throw new RuntimeException("NO View Type");
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof HeaderViewHolder) {
                //
            }else if (holder instanceof ItemViewHolder) {
                Album album = albumList.get(position-1);
                ((ItemViewHolder) holder).txtTitle.setText(album.getTitle());
            }
        }

        @Override
        public int getItemCount() {
            return albumList.size()+1;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public TextView txtTitle;

            private ItemViewHolder(View view) {
                super(view);
                txtTitle = view.findViewById(R.id.title);
            }
        }

        // The ViewHolders for Header, Item and Footer
        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public View View;
            private TextView txtName;
            private TextView txtEmail;
            private TextView txtAdress;
            private TextView txtTitle;

            public HeaderViewHolder(View view) {
                super(view);
                View = view;
                // add your ui components here like this below
                txtName = View.findViewById(R.id.name);
                txtEmail = View.findViewById(R.id.email);
                txtAdress = View.findViewById(R.id.address);
                txtTitle = View.findViewById(R.id.title);
            }

            public void setValues(String name, String address, String email, String title){
                txtName.setText(name);
                txtAdress.setText(address);
                txtEmail.setText(email);
                txtTitle.setText(title);
            }
        }

    }
}
