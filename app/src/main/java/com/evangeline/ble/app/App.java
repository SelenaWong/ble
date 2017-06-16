package com.evangeline.ble.app;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lenovo on 2017/6/12.
 */
public class App extends Application {

    public static  List<Activity> activitys = new ArrayList<>();
    private ArrayList<HashMap<String, BluetoothGattService>> mGattServiceMasterData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private List<BluetoothGattCharacteristic> mGattCharacteristics;
    private BluetoothGattCharacteristic mBluetoothgattcharacteristic;
    private BluetoothGattDescriptor mBluetoothGattDescriptor;

    private static BluetoothGattService mBluetoothGattService;

    public static BluetoothGattService getmBluetoothGattService() {
        return mBluetoothGattService;
    }

    public static void setmBluetoothGattService(BluetoothGattService bluetoothGattService) {
        Log.i("TAG","setBluetoothGattService"+bluetoothGattService);
        if(bluetoothGattService!=null){
            Log.i("TAG","setBluetoothGattService uuid="+bluetoothGattService.getUuid().toString()+"cSize="+bluetoothGattService.getCharacteristics().size());
        }
        mBluetoothGattService = bluetoothGattService;
    }

    /**
     * getter method for Blue tooth GATT characteristic
     *
     * @return {@link BluetoothGattCharacteristic}
     */
    public BluetoothGattCharacteristic getBluetoothgattcharacteristic() {
        return mBluetoothgattcharacteristic;
    }

    /**
     * setter method for Blue tooth GATT characteristics
     *
     * @param bluetoothgattcharacteristic
     */
    public void setBluetoothgattcharacteristic(
            BluetoothGattCharacteristic bluetoothgattcharacteristic) {
        this.mBluetoothgattcharacteristic = bluetoothgattcharacteristic;
    }

    /**
     * getter method for Blue tooth GATT characteristic
     *
     * @return {@link BluetoothGattCharacteristic}
     */
    public BluetoothGattDescriptor getBluetoothgattDescriptor() {
        return mBluetoothGattDescriptor;
    }

    /**
     * setter method for Blue tooth GATT Descriptor
     *
     * @param bluetoothGattDescriptor
     */
    public void setBluetoothgattdescriptor(
            BluetoothGattDescriptor bluetoothGattDescriptor) {
        this.mBluetoothGattDescriptor = bluetoothGattDescriptor;
    }

    /**
     * getter method for blue tooth GATT Characteristic list
     *
     * @return {@link List < BluetoothGattCharacteristic >}
     */
    public List<BluetoothGattCharacteristic> getGattCharacteristics() {
        return mGattCharacteristics;
    }

    /**
     * setter method for blue tooth GATT Characteristic list
     *
     * @param gattCharacteristics
     */
    public void setGattCharacteristics(
            List<BluetoothGattCharacteristic> gattCharacteristics) {
        this.mGattCharacteristics = gattCharacteristics;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceMasterData() {
        return mGattServiceMasterData;
    }

    public void setGattServiceMasterData(
            ArrayList<HashMap<String, BluetoothGattService>> gattServiceMasterData) {
        this.mGattServiceMasterData = gattServiceMasterData;
    }



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    public static void exit(){
        for(Activity activity : activitys){
            activity.finish();
        }
    }
}
