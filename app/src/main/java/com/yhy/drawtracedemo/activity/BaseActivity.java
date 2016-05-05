package com.yhy.drawtracedemo.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Administrator on 2016/5/4.
 */
public class BaseActivity extends FragmentActivity {

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initConfig();
        initData();
        initEvent();
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initConfig();
        initData();
        initEvent();
    }

    protected void initView(){

    }

    //初始化适配相关
    protected void initConfig(){

    }
    protected void initData(){

    }

    protected void initEvent(){

    }
}
