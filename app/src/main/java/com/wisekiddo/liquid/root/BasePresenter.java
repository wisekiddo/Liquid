package com.wisekiddo.liquid.root;

/**
 * Created by ronald on 28/4/18.
 */

public interface BasePresenter<T> {
    void generateView(T view);
    void dropView();
}
