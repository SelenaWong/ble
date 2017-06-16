/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package com.evangeline.ble.bleServiceActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.evangeline.ble.R;
import com.evangeline.ble.activity.BaseActivity;
import com.evangeline.ble.adapter.GattServiceListAdapter;
import com.evangeline.ble.app.App;
import com.evangeline.ble.service.BluetoothLeService;
import com.evangeline.ble.utils.Constants;
import com.evangeline.ble.utils.GattAttributes;
import com.evangeline.ble.utils.Logger;
import com.evangeline.ble.utils.UUIDDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment to show the GATT services details in GATT DB
 */
public class GattServicesActivity extends BaseActivity {

    // BluetoothGattService
    private static BluetoothGattService mService;

    // HashMap to store service
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData;
    private static ArrayList<HashMap<String, BluetoothGattService>> mModifiedServiceData;

    // GattCharacteristics list
    private static List<BluetoothGattCharacteristic> mGattCharacteristics;

    // Application
    private App mApplication;

    // ListView
    private ListView mGattListView;

    //
    private ImageView mBackButton;
    private static final int HANDLER_DELAY = 500;


    @Override
    protected void initData() {

    }

    @Override
    protected void initVariable() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_device_service);
        mApplication = (App)getApplication();
        mGattListView = (ListView)findViewById(R.id.activity_device_service_lv);
        mBackButton = (ImageView)findViewById(R.id.activity_device_service_imgback);
        mBackButton.setVisibility(View.GONE);

        // Getting the service data from the application
        mGattServiceData = mApplication.getGattServiceMasterData();

        // Preparing list data
        // GAP and GATT attributes are not displayed
        mModifiedServiceData = new ArrayList<HashMap<String, BluetoothGattService>>();
        for (int i = 0; i < mGattServiceData.size(); i++) {
            if (!(mGattServiceData.get(i).get("UUID").getUuid()
                    .equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE) || mGattServiceData
                    .get(i).get("UUID").getUuid()
                    .equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE))) {
                mModifiedServiceData.add(mGattServiceData.get(i));
            }
        }
        // Setting adapter
        GattServiceListAdapter adapter = new GattServiceListAdapter(
                GattServicesActivity.this, mModifiedServiceData);
        mGattListView.setAdapter(adapter);

        // List listener
        mGattListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                mService = mModifiedServiceData.get(pos).get("UUID");
                mGattCharacteristics = mService.getCharacteristics();
                String selectedServiceName = GattAttributes.lookupUUID(
                        mService.getUuid(),
                        getResources().getString(
                                R.string.profile_control_unknown_service));

                mApplication.setGattCharacteristics(mGattCharacteristics);

                // Passing service details to GattCharacteristicsActivity and
                // adding that fragment to the current view
                Bundle bundle = new Bundle();
                bundle.putString(Constants.GATTDB_SELECTED_SERVICE,
                        selectedServiceName);
                Intent it = new Intent(GattServicesActivity.this,GattCharacteristicsActivity.class);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        BluetoothLeService.mEnabledCharacteristics=new ArrayList<BluetoothGattCharacteristic>();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG","enter GattServicesActivity");
    }

  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem pairCache = menu.findItem(R.id.pairing);
        if (Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_PAIR_CACHE_STATUS)) {
            pairCache.setChecked(true);
        } else {
            pairCache.setChecked(false);
        }
        search.setVisible(false);
        graph.setVisible(false);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("Enabled characteristic size-->" + BluetoothLeService.mEnabledCharacteristics.size());
        if(BluetoothLeService.mEnabledCharacteristics.size()>0){
            BluetoothLeService.disableAllEnabledCharacteristics();
            Toast.makeText(getApplicationContext(), getResources().
                            getString(R.string.profile_control_stop_both_notify_indicate_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
