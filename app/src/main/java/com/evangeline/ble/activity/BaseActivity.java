package com.evangeline.ble.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.evangeline.ble.R;
import com.evangeline.ble.app.App;


import java.lang.reflect.Field;
import java.util.List;


/***
 *
 * @Description Activity 基类
 * @author Selena Wong
 * @date 2017年4月15日 上午11:19:58
 * @version V1.0.0
 */
public abstract class BaseActivity extends Activity {

    private boolean interrupt = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
        initVariable();
        if(interrupt){
            finish();
            return;
        }
        initView( savedInstanceState );
        initData();
        setListener();
        App.activitys.add(this);
    }

    public int getStatusHeight(){
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return x;
        }catch(Exception ex){
            ex.printStackTrace();
            ex.printStackTrace();
        }
        return 0;
    }

/*    protected void setStatusBar(final ViewGroup linear_bar ){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            final int statusHeight = getStatusHeight();
            linear_bar.post( new Runnable(){
                @Override
                public void run() {
                    int titleHeight = linear_bar.getHeight();
                    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) linear_bar.getLayoutParams();
                    params.height = statusHeight + titleHeight;
                    linear_bar.setLayoutParams(params);
                }
            });
        }

    }*/


    public void setInterrupt(){
        this.interrupt = true;
    }

    public void setListener( ){


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.activitys.remove(this);
    }

    protected abstract  void initVariable();
    protected abstract  void initView( Bundle savedInstanceState);
    protected abstract  void initData();
}
