package com.wisekiddo.liquid.feature.users;

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
import com.wisekiddo.liquid.data.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import com.wisekiddo.liquid.feature.albums.AlbumsActivity;
import com.wisekiddo.liquid.root.ActivityScoped;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ronald on 28/4/18.
 */

@ActivityScoped
public class UsersFragment extends DaggerFragment implements UsersContract.View {

    @Inject
    UsersContract.Presenter presenter;
    /**
     * Listener for clicks on users in the ListView.
     */

    private UsersAdapter listAdapter;
    private View noUsersView;
    private TextView mNoUserMainView;
    private LinearLayout mUsersView;
    private TextView mFilteringLabelView;

    @Inject
    public UsersFragment() {

        // Requires empty public constructor
    }

    public interface UserListener {
        void onUserClick(User clickedUser);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new UsersAdapter(new ArrayList<User>(0), userListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running user
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //presenter.result(requestCode, resultCode);
    }

    UserListener userListener = new UserListener() {
        @Override
        public void onUserClick(User clickedUser) {
            presenter.openAlbums(clickedUser);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.users_fragment, container, false);

        // Set up users view
        ListView listView = root.findViewById(R.id.users_list);
        listView.setAdapter(listAdapter);
        //mFilteringLabelView = root.findViewById(R.id.filteringLabel);
        mUsersView = root.findViewById(R.id.usersLayout);

        // Set up  no users view
        noUsersView = root.findViewById(R.id.noUsers);
        mNoUserMainView = root.findViewById(R.id.noUsersMain);


        // Set up progress indicator
        final UsersRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
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
                presenter.loadUsers(false);
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
    public void showUsers(List<User> users) {
        listAdapter.replaceData(users);

        mUsersView.setVisibility(View.VISIBLE);
        noUsersView.setVisibility(View.GONE);
    }

    @Override
    public void showNoUsers() {
        showNoUsersViews(
                getResources().getString(R.string.preparing_list),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    private void showNoUsersViews(String mainText, int iconRes, boolean showAddView) {
        mUsersView.setVisibility(View.GONE);
        noUsersView.setVisibility(View.VISIBLE);

        mNoUserMainView.setText(mainText);
        //noinspection deprecation
    }

    @Override
    public void showLoadingUsersError() {
        //showMessage(getString(R.string.loading_users_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void showAlbums(Integer userId) {
        //Shown in it's own Activity, since it makes more sense that way
        // and it gives us the flexibility to show some Intent stubbing.
        Intent intent = new Intent(getContext(), AlbumsActivity.class);
        intent.putExtra(AlbumsActivity.EXTRA_ITEM_ID, userId.toString());
        startActivity(intent);
    }


    private static class UsersAdapter extends BaseAdapter {

        private List<User> mUsers;
        private UserListener mUserListener;
        private static final String BASE_IMAGE_URL = "http://media.redmart.com/newmedia/200p";


        public UsersAdapter(List<User> users, UserListener userListener) {
            setList(users);
            mUserListener = userListener;
        }

        public void replaceData(List<User> users) {
            setList(users);
            notifyDataSetChanged();
        }

        private void setList(List<User> users) {
            mUsers = checkNotNull(users);
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public User getItem(int i) {
            return mUsers.get(i);
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
                rowView = inflater.inflate(R.layout.user, viewGroup, false);
            }

            final User user = getItem(i);

            TextView titleView = rowView.findViewById(R.id.title);
            titleView.setText(user.getName().trim());

            TextView descriptionView = rowView.findViewById(R.id.description);
            descriptionView.setText(user.getUsername());

            TextView price = rowView.findViewById(R.id.price);
            String strPrice = "none";
            price.setText(strPrice);

            //ImageView imageView = rowView.findViewById(R.id.image);
           // Picasso.get().load(BASE_IMAGE_URL+user.getImg().getName())
            //        .memoryPolicy(MemoryPolicy.NO_STORE)
            //        .into(imageView);


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUserListener.onUserClick(user);
                }
            });

            return rowView;
        }
    }

}
