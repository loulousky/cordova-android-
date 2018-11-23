package com.liuhai.jiugeh5;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.liuhai.jiugeh5.R;

/**
 * Created by admin on 2017/4/6.
 *
 * 基类A
 *
 */

public abstract  class BaseicActivity extends AppCompatActivity{
    public Gson gson = new Gson();;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }
    public void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }
    public  void  showProgress(String msg){
        progressDialog = ProgressDialog
                .show(this, null, msg, false);
        progressDialog.setCanceledOnTouchOutside(true);
    }

    public  void cancleProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }


    /**
     * 加载事件监听
     */
    public abstract void loadlisten();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
