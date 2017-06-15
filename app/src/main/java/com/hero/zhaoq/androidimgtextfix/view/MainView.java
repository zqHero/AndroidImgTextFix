package com.hero.zhaoq.androidimgtextfix.view;

import android.graphics.Bitmap;
import android.graphics.drawable.LevelListDrawable;

/**
 * Created by zhao
 * on 2017/6/14.
 */

public interface MainView  {


    void showLoading();


    void hideLoading();


    void updateView();


    void updateNetTxtView(Bitmap bitmap,LevelListDrawable draw);
}
