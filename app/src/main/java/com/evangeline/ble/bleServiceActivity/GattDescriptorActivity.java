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
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.evangeline.ble.R;
import com.evangeline.ble.activity.BaseActivity;
import com.evangeline.ble.adapter.GattCharacteristicDescriptorsAdapter;
import com.evangeline.ble.app.App;
import com.evangeline.ble.utils.Constants;
import com.evangeline.ble.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fragment class for GATT Descriptor
 */
public class GattDescriptorActivity extends BaseActivity {

    private List<BluetoothGattDescriptor> mBluetoothGattDescriptors;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    // Application
    private App mApplication;
    // Text Heading
    private TextView mTextHeading;
    // GATT Service name
    private String mGattServiceName = "";
    //GATT Characteristic name
    private String mGattCharacteristicName = "";
    // ListView
    private ListView mGattListView;
    // Back button
    private ImageView mBackButton;

    @Override
    protected void initData() {

    }

    @Override
    protected void initVariable() {

    }

    @Override
    public void initView( Bundle savedInstanceState) {
        setContentView(R.layout.activity_device_service);
        mApplication = (App)getApplication();
        mGattListView = (ListView)findViewById(R.id.activity_device_service_lv);
        mTextHeading = (TextView)findViewById(R.id.activity_device_service_txtservices);
        mTextHeading.setText(getString(R.string.gatt_descriptors_heading));
        mBackButton = (ImageView)findViewById(R.id.activity_device_service_imgback);
        mBluetoothGattCharacteristic = mApplication.getBluetoothgattcharacteristic();

        // Back button listener
        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               onBackPressed();

            }
        });

        // Getting the selected service from the arguments
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mGattServiceName = bundle
                    .getString(Constants.GATTDB_SELECTED_SERVICE);
            mGattCharacteristicName = bundle
                    .getString(Constants.GATTDB_SELECTED_CHARACTERISTICE);
        }
        //Preparing list data
        List<BluetoothGattDescriptor> tempList = mBluetoothGattCharacteristic.getDescriptors();
        mBluetoothGattDescriptors = new ArrayList<BluetoothGattDescriptor>();

        for (BluetoothGattDescriptor tempDesc : tempList) {
            int mainListSize = mBluetoothGattDescriptors.size();
            if (mainListSize > 0) {
                //Getting the UUID of list descriptors
                ArrayList<UUID> mainUUID = new ArrayList<UUID>();
                for (int incr = 0; incr < mainListSize; incr++) {
                    mainUUID.add(mBluetoothGattDescriptors.get(incr).getUuid());
                }
                if (!mainUUID.contains(tempDesc.getUuid())) {
                    mBluetoothGattDescriptors.add(tempDesc);
                }
            } else {
                mBluetoothGattDescriptors.add(tempDesc);
            }
        }
        GattCharacteristicDescriptorsAdapter gattCharacteristicDescriptorsAdapter =
                new GattCharacteristicDescriptorsAdapter(GattDescriptorActivity.this, mBluetoothGattDescriptors);
        if (gattCharacteristicDescriptorsAdapter != null) {
            mGattListView.setAdapter(gattCharacteristicDescriptorsAdapter);
        }
        mGattListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.i("Descriptor selected " + mBluetoothGattDescriptors.get(position).getUuid());
                mApplication.setBluetoothgattdescriptor(mBluetoothGattDescriptors.get(position));
                Intent it = new Intent(GattDescriptorActivity.this,GattDescriptorDetailsActivity.class);
                startActivity(it);
            }
        });
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
    public void onResume() {
        super.onResume();
        Log.i("TAG","enter  GattDescriptorActivity");
    }
}
