package com.evangeline.ble.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evangeline.ble.R;
import com.evangeline.ble.adapter.BleAdapter;
import com.evangeline.ble.logActivity.DataLoggerActivity;
import com.evangeline.ble.service.BluetoothLeService;
import com.evangeline.ble.utils.Constants;
import com.evangeline.ble.utils.Logger;
import com.evangeline.ble.utils.Utils;
import com.evangeline.ble.view.XDividerItemDecoration;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.haha.perflib.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends CheckPermissionsActivity {

    @BindView(R.id.activity_main_logTv)TextView mLogTv;
    @BindView(R.id.activity_main_bleXlv)XRecyclerView mBleXlv;

    private List<BluetoothDevice> mDevices=new ArrayList<>();
    private Map<String,Integer> mRSSIMap = new HashMap<String,Integer>();

    private BluetoothAdapter mBluetoothAdapter;
    private BleAdapter mAdapter;
    public static boolean  mApplicationInBackground = false;

    private final static  int REQUEST_ENABLE_BT =1;
    private final static int SCAN_PEROID = 1000;
    private final int UPDATA_DEVICE = 1001;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter =bluetoothManager.getAdapter();
        if(mBluetoothAdapter==null){
            Toast.makeText(getApplicationContext(),"蓝牙设备无法打开",Toast.LENGTH_SHORT).show();
            finish();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBleXlv.setLayoutManager(layoutManager);
        mBleXlv.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mBleXlv.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mBleXlv.addItemDecoration( new XDividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        mBleXlv.setArrowImageView(R.drawable.iconfont_downgrey);
        mBleXlv.setLoadingMoreEnabled(false);
        setAdapter();
        setListener();
    }

    public void setListener(){
        mBleXlv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                scanLeDevice(true);
            }
            @Override
            public void onLoadMore() {
            }
        });
    }

    public void setAdapter(){
        mAdapter = new BleAdapter(mDevices,mRSSIMap,new BleAdapter.OnItemClickListener() {
            @Override
            public void onBleClickListener(int position,BluetoothDevice device) {
                Intent it = new Intent(MainActivity.this,DeviceControlActivity.class);
                it.putExtra("ADDRESS",device.getAddress());
                String name = TextUtils.isEmpty(device.getName())? "Unknow device" : device.getName();
                it.putExtra("NAME",name);
                startActivity(it);
            }
        });
        mBleXlv.setAdapter(mAdapter);
    }

    public void  scanLeDevice(boolean enable){
       if(enable){
           mHandler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   isScanning = false;
                   mBluetoothAdapter.stopLeScan(mLeScanCallback);
                   mBleXlv.refreshComplete();
                   Message msg = new Message();
                   msg.what = UPDATA_DEVICE;
                   mHandler.sendMessage(msg);
               }
           },SCAN_PEROID);
           isScanning = true;
           mBluetoothAdapter.startLeScan(mLeScanCallback);
       }else{
           isScanning = false;
           mBluetoothAdapter.stopLeScan(mLeScanCallback);
       }
    }
    private boolean isScanning = false;
    private BluetoothAdapter.LeScanCallback  mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
          addDevice(bluetoothDevice,rssi);
        }
    };

    public void addDevice(BluetoothDevice device,int rssi){
        try {
            if (mRSSIMap.containsKey(device.getAddress())) {
                mRSSIMap.put(device.getAddress(), rssi);
            } else {
                mDevices.add(device);
                mRSSIMap.put(device.getAddress(), rssi);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



   private Handler mHandler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case UPDATA_DEVICE:
                   mAdapter.notifyDataSetChanged();
                   break;
           }
       }
   };


    @OnClick(R.id.activity_main_logTv)
    public void goLog(){
        Intent it = new Intent(MainActivity.this,DataLoggerActivity.class);
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "CySmart" + File.separator
                + Utils.GetDate() + ".txt");
        String path = file.getAbsolutePath();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_LOGGER_FILE_NAAME, path);
        bundle.putBoolean(Constants.DATA_LOGGER_FLAG, false);
        it.putExtras(bundle);
        startActivity(it);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        Logger.createDataLoggerFile(MainActivity.this);
        mApplicationInBackground = false;
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        // Initializes list view adapter.
       mBleXlv.refresh();
    }


    //For UnPairing
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mApplicationInBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
