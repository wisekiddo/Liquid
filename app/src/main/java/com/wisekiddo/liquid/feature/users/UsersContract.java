package com.wisekiddo.liquid.feature.users;

import android.support.annotation.NonNull;

import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.root.BasePresenter;
import com.wisekiddo.liquid.root.BaseView;

import java.util.List;


/**
 * Created by ronald on 28/4/18.
 *
 * This specifies the contract between the view and the presenter.
 */

public interface UsersContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showUsers(List<User> users);

        void showAlbums(Integer userId);

        void showLoadingUsersError();

        void showNoUsers();
    }

    interface Presenter extends BasePresenter<View> {

        void loadUsers(boolean forceUpdate);

        void openAlbums(@NonNull User requestedUser);

        void takeView(UsersContract.View view);

        void dropView();
    }
}
