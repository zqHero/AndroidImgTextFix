package com.hero.zhaoq.androidimgtextfix.presenter;


import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.LevelListDrawable;
import android.os.Handler;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.hero.zhaoq.androidimgtextfix.MainActivity;
import com.hero.zhaoq.androidimgtextfix.VirtualData;
import com.hero.zhaoq.androidimgtextfix.bean.NetDataBean;
import com.hero.zhaoq.androidimgtextfix.view.MainView;


/**
 * Created by zhao
 * on 2017/6/14.
 */

public class MainPresenter {

    private MainView view;
    private NetDataBean bean;

    private Application application;

    public MainPresenter(MainView mainActivity) {
        view = mainActivity;
        this.application = application;
    }

    /**
     * 请求数据
     */
    public void init() {
        view.showLoading();
        getNetData();
    }

    private void getNetData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取到  网络数据
                NetDataBean netBean = new NetDataBean();
                netBean.setCharSque(VirtualData.getNetStr());
                //模拟网络   访问  信息
                if (view != null) {
                    setBean(netBean);
                    view.hideLoading();
                    view.updateView();
                }
            }
        }, 3000);
    }

    public NetDataBean getBean() {
        return bean;
    }

    public void setBean(NetDataBean bean) {
        this.bean = bean;
    }

}
