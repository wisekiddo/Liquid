package com.wisekiddo.liquid.feature.users;

import android.support.annotation.NonNull;

import com.wisekiddo.liquid.data.model.User;
import com.wisekiddo.liquid.data.source.Repository;
import com.wisekiddo.liquid.root.ActivityScoped;
import com.wisekiddo.liquid.util.EspressoIdlingResource;
import com.wisekiddo.liquid.util.schedulers.BaseSchedulerProvider;
import com.wisekiddo.liquid.util.schedulers.SchedulerProvider;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * *Created by ronald on 28/4/18.
 */

@ActivityScoped
final class UsersPresenter implements UsersContract.Presenter {

    @NonNull
    private final Repository repository;
    @NonNull
    private UsersContract.View usersView;

    @NonNull
    private final BaseSchedulerProvider schedulerProvider = SchedulerProvider.getInstance();

    private boolean firstLoad = true;

    @NonNull
    private CompositeDisposable compositeDisposable;

    @Inject
    UsersPresenter(Repository usersRepository) {
        repository = usersRepository;
        compositeDisposable = new CompositeDisposable();
    }

    //@Override
    public void subscribe() {
       // loadTasks(false);
    }

   // @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void loadUsers(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadUsers(forceUpdate || firstLoad, true);
        firstLoad = false;
    }

    private void loadUsers(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            if (usersView != null) {
                usersView.setLoadingIndicator(true);
            }
        }
        if (forceUpdate) {
            repository.refreshUsers();
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        compositeDisposable.clear();
        Disposable disposable = repository
                .getUsers()
                .flatMap(Flowable::fromIterable)
                .toList()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
                .subscribe(
                        // onNext
                        users -> {
                            processUsers(users);
                            usersView.setLoadingIndicator(false);
                        },
                        // onError
                        throwable -> usersView.showLoadingUsersError());

        compositeDisposable.add(disposable);

    }

    private void processUsers(List<User> users) {
        if (users.isEmpty()) {
            processEmptyUsers();
        } else {
            usersView.showUsers(users);
        }
    }


    private void processEmptyUsers() {
        usersView.showNoUsers();
    }

    @Override
    public void openAlbums(@NonNull User requestedUser) {
        checkNotNull(requestedUser, "requestedUser cannot be null!");
        if (usersView != null) {
           usersView.showAlbums(requestedUser.getId());
        }
    }


    @Override
    public void takeView(UsersContract.View view) {
        this.usersView = view;
        loadUsers(false);
    }

    @Override
    public void dropView() {
        usersView = null;
    }

    public Integer getId() {
        return 1;
    }
}
