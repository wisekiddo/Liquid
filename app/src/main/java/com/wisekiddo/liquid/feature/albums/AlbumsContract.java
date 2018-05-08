package com.wisekiddo.liquid.feature.albums;

import android.support.annotation.NonNull;

import com.wisekiddo.liquid.data.model.Album;
import com.wisekiddo.liquid.root.BasePresenter;
import com.wisekiddo.liquid.root.BaseView;

import java.util.List;

/**
 * Created by ronald on 28/4/18.
 *
 * This specifies the contract between the view and the presenter.
 */

public interface AlbumsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showAlbums(List<Album> albums);

        void showDetailsUi(Integer userId);

        void showLoadingError();

        void showNoList();
    }

    interface Presenter extends BasePresenter<View> {

        void reload(boolean forceUpdate);

        void openDetails(@NonNull Album requestedUser);

        void takeView(AlbumsContract.View view);

        void dropView();
    }
}
