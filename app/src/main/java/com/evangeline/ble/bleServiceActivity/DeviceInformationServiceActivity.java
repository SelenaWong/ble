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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.evangeline.ble.R;
import com.evangeline.ble.activity.BaseActivity;
import com.evangeline.ble.app.App;
import com.evangeline.ble.service.BluetoothLeService;
import com.evangeline.ble.utils.Constants;
import com.evangeline.ble.utils.GattAttributes;
import com.evangeline.ble.utils.Logger;
import com.evangeline.ble.utils.UUIDDatabase;
import com.evangeline.ble.utils.Utils;

import java.util.List;
import java.util.UUID;

/**
 * Fragment to display the Device information service
 */
public class DeviceInformationServiceActivity extends BaseActivity {

    // GATT Service and Characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mReadCharacteristic;

    // Data view variables
    private TextView mManufacturerName;
    private TextView mModelName;
    private TextView mSerialName;
    private TextView mHardwareRevisionName;
    private TextView mFirmwareRevisionName;
    private TextView mSoftwareRevisionName;
    private TextView mPnpId;
    private TextView mSysId;
    private TextView mRegulatoryCertificationDataList;
    //ProgressDialog
    private ProgressDialog mProgressDialog;


    // Flag for data set
    private static boolean mManufacturerSet = false;
    private static boolean mmModelNumberSet = false;
    private static boolean mSerialNumberSet = false;
    private static boolean mHardwareNumberSet = false;
    private static boolean mFirmwareNumberSet = false;
    private static boolean mSoftwareNumberSet = false;
    private static boolean mPnpidSet = false;
    private static boolean mRegulatoryCertificationDataListSet = false;
    private static boolean mSystemidSet = false;

 /*   *
     * BroadcastReceiver for receiving the GATT server status*/

    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (Constants.ACTION_DATA_AVAILABLE.equals(action)) {
                // Check Model number
                String data = intent.getStringExtra(Constants.EXTRA_DATA);
                Log.i("TAG","data="+data);

                if (extras.containsKey(Constants.EXTRA_MNS_VALUE)) {
                    String received_mns_data = intent
                            .getStringExtra(Constants.EXTRA_MNS_VALUE);
                    if (!received_mns_data.equalsIgnoreCase(" ")) {
                        if (!mManufacturerSet) {
                            mManufacturerSet = true;
                            displayManufactureName(received_mns_data);
                            prepareCharacteristics(UUIDDatabase.UUID_MODEL_NUMBER_STRING);
                        }
                    }
                }
                // Check Serial number
                if (extras.containsKey(Constants.EXTRA_MONS_VALUE)) {
                    String received_mons_data = intent
                            .getStringExtra(Constants.EXTRA_MONS_VALUE);
                    if (!received_mons_data.equalsIgnoreCase(" ")) {
                        if (!mmModelNumberSet) {
                            mmModelNumberSet = true;
                            displayModelNumber(received_mons_data);
                            prepareCharacteristics(UUIDDatabase.UUID_SERIAL_NUMBER_STRING);
                        }
                    }
                }
                // Check Hardware Revision
                if (extras.containsKey(Constants.EXTRA_SNS_VALUE)) {
                    String received_sns_data = intent
                            .getStringExtra(Constants.EXTRA_SNS_VALUE);
                    if (!received_sns_data.equalsIgnoreCase(" ")) {
                        if (!mSerialNumberSet) {
                            mSerialNumberSet = true;
                            displaySerialNumber(received_sns_data);
                            prepareCharacteristics(UUIDDatabase.UUID_HARDWARE_REVISION_STRING);
                        }
                    }
                }
                // check Firmware revision
                if (extras.containsKey(Constants.EXTRA_HRS_VALUE)) {
                    String received_hrs_data = intent
                            .getStringExtra(Constants.EXTRA_HRS_VALUE);
                    if (!received_hrs_data.equalsIgnoreCase(" ")) {
                        if (!mHardwareNumberSet) {
                            mHardwareNumberSet = true;
                            displayhardwareNumber(received_hrs_data);
                            prepareCharacteristics(UUIDDatabase.UUID_FIRMWARE_REVISION_STRING);
                        }
                    }
                }
                // check Software revision
                if (extras.containsKey(Constants.EXTRA_FRS_VALUE)) {
                    String received_frs_data = intent
                            .getStringExtra(Constants.EXTRA_FRS_VALUE);
                    if (!received_frs_data.equalsIgnoreCase(" ")) {
                        if (!mFirmwareNumberSet) {
                            mFirmwareNumberSet = true;
                            displayfirmwareNumber(received_frs_data);
                            prepareCharacteristics(UUIDDatabase.UUID_SOFTWARE_REVISION_STRING);
                        }
                    }
                }
                // Check PNP ID
                if (extras.containsKey(Constants.EXTRA_SRS_VALUE)) {
                    String received_srs_data = intent
                            .getStringExtra(Constants.EXTRA_SRS_VALUE);
                    if (!received_srs_data.equalsIgnoreCase(" ")) {
                        if (!mSoftwareNumberSet) {
                            mSoftwareNumberSet = true;
                            displaySoftwareNumber(received_srs_data);
                            prepareCharacteristics(UUIDDatabase.UUID_PNP_ID);
                        }
                    }
                }
                // Check IEEE
                if (extras.containsKey(Constants.EXTRA_PNP_VALUE)) {
                    String received_pnpid = intent
                            .getStringExtra(Constants.EXTRA_PNP_VALUE);
                    if (!received_pnpid.equalsIgnoreCase(" ")) {
                        if (!mPnpidSet) {
                            mPnpidSet = true;
                            displayPnpId(received_pnpid);
                            prepareCharacteristics(UUIDDatabase.UUID_IEEE);
                        }
                    }
                }
                // Check System ID
                if (extras.containsKey(Constants.EXTRA_RCDL_VALUE)) {
                    String received_rcdl_value = intent
                            .getStringExtra(Constants.EXTRA_RCDL_VALUE);
                    if (!received_rcdl_value.equalsIgnoreCase(" ")) {
                        if (!mRegulatoryCertificationDataListSet) {
                            mRegulatoryCertificationDataListSet = true;
                            displayRegulatoryData(received_rcdl_value);
                            prepareCharacteristics(UUIDDatabase.UUID_SYSTEM_ID);
                        }
                    }
                }
                // Check System Id set
                if (extras.containsKey(Constants.EXTRA_SID_VALUE)) {
                    String received_sid_value = intent
                            .getStringExtra(Constants.EXTRA_SID_VALUE);
                    if (!received_sid_value.equalsIgnoreCase(" ")) {
                        if (!mSystemidSet) {
                            displaySystemid(received_sid_value);
                            mReadCharacteristic = null;
                            mSystemidSet = true;
                        }
                    }
                }
            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.bondingProgressDialog(DeviceInformationServiceActivity.this, mProgressDialog, true);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getmDeviceName() + "|"
                            + BluetoothLeService.getmDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(DeviceInformationServiceActivity.this, mProgressDialog, false);
                    getGattData();

                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getmDeviceName() + "|"
                            + BluetoothLeService.getmDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(DeviceInformationServiceActivity.this, mProgressDialog, false);
                }
            }
        }
    };

    /**
     * Prepares Characteristics
     *
     */
    private void prepareCharacteristics(UUID characteristic) {
        List<BluetoothGattCharacteristic> mGatt = mService
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : mGatt) {
            UUID uuidchara = gattCharacteristic.getUuid();
            if (uuidchara.equals(characteristic)) {
                Logger.i("Characteristic " + uuidchara);
                prepareBroadcastDataRead(gattCharacteristic);
            }
        }
    }


    /**
     * Display RCDL Value
     *
     * @param received_rcdl_value
     */
    private void displayRegulatoryData(String received_rcdl_value) {
        mRegulatoryCertificationDataList.setText(received_rcdl_value);
    }

    /**
     * Display SystemID
     *
     * @param received_sid_value
     */

    void displaySystemid(String received_sid_value) {
        mSysId.setText(received_sid_value);

    }

    /**
     * Display PNPID
     *
     * @param received_pnpid
     */
    void displayPnpId(String received_pnpid) {
        mPnpId.setText(received_pnpid);
    }

    /**
     * Display Software revision number
     *
     * @param received_srs_data
     */
    void displaySoftwareNumber(String received_srs_data) {
        mSoftwareRevisionName.setText(received_srs_data);

    }

    /**
     * Display hardware revision number
     *
     * @param received_hrs_data
     */
    void displayhardwareNumber(String received_hrs_data) {
        mHardwareRevisionName.setText(received_hrs_data);

    }

    /**
     * Display firmware revision number
     *
     * @param received_frs_data
     */
    void displayfirmwareNumber(String received_frs_data) {
        mFirmwareRevisionName.setText(received_frs_data);

    }

    /**
     * Display serial number
     *
     * @param received_sns_data
     */
    void displaySerialNumber(String received_sns_data) {
        mSerialName.setText(received_sns_data);

    }

    /**
     * Display model number
     *
     * @param received_mons_data
     */
    void displayModelNumber(String received_mons_data) {
        mModelName.setText(received_mons_data);
    }

    /**
     * Display manufacture name
     *
     * @param received_mns_data
     */

    void displayManufactureName(String received_mns_data) {
        mManufacturerName.setText(received_mns_data);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initVariable() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_device_information);
        mManufacturerName = (TextView) findViewById(R.id.activity_device_information_div_manufacturer);
        mModelName = (TextView) findViewById(R.id.activity_device_information_div_model);
        mSerialName = (TextView)findViewById(R.id.activity_device_information_div_serial);
        mHardwareRevisionName = (TextView)findViewById(R.id.activity_device_information_div_hardware);
        mFirmwareRevisionName = (TextView)findViewById(R.id.activity_device_information_div_firmware);
        mSoftwareRevisionName = (TextView)findViewById(R.id.activity_device_information_div_software);
        mProgressDialog = new ProgressDialog(this);
        mPnpId = (TextView)findViewById(R.id.activity_device_information_div_pnp);
        mSysId = (TextView) findViewById(R.id.activity_device_information_div_system);
        mRegulatoryCertificationDataList = (TextView)findViewById(R.id.activity_device_information_div_regulatory);
        App mApplication = (App)getApplication();
        mService = mApplication.getmBluetoothGattService( );
    }

    /**
     * Prepare Broadcast receiver to broadcast read characteristics
     *
     * @param gattCharacteristic
     */

    void prepareBroadcastDataRead(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            BluetoothLeService.readCharacteristic(gattCharacteristic);
        }
    }

    @Override
    public void onResume() {
        makeDefaultBooleans();
        Log.i("TAG","enter DeviceInformationServiceActivity");
        registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        clearUI();
        getGattData();
        super.onResume();
    }

    /**
     * clear all data fields
     */
    private void clearUI() {
        mManufacturerName.setText("");
        mModelName.setText("");
        mSerialName.setText("");
        mHardwareRevisionName.setText("");
        mSoftwareRevisionName.setText("");
        mSoftwareRevisionName.setText("");
        mPnpId.setText("");
        mSysId.setText("");
    }

    /**
     * Flag up default
     */
    private void makeDefaultBooleans() {
        mManufacturerSet = false;
        mmModelNumberSet = false;
        mSerialNumberSet = false;
        mHardwareNumberSet = false;
        mFirmwareNumberSet = false;
        mSoftwareNumberSet = false;
        mPnpidSet = false;
        mSystemidSet = false;
        mRegulatoryCertificationDataListSet = false;
    }

    @Override
    public void onPause() {
        unregisterReceiver(mGattUpdateReceiver);
        super.onPause();
    }

    /**
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mService
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidchara = gattCharacteristic.getUuid().toString();
            if (uuidchara.equalsIgnoreCase(GattAttributes.MANUFACTURER_NAME_STRING)
                    || uuidchara.equalsIgnoreCase(GattAttributes.SERIAL_NUMBER_STRING)
                    || uuidchara.equalsIgnoreCase(GattAttributes.FIRMWARE_REVISION_STRING)
                    || uuidchara.equalsIgnoreCase(GattAttributes.HARDWARE_REVISION_STRING)
                    || uuidchara.equalsIgnoreCase(GattAttributes.SOFTWARE_REVISION_STRING)
                    || uuidchara.equalsIgnoreCase(GattAttributes.MANUFACTURER_NAME_STRING)
                    || uuidchara.equalsIgnoreCase(GattAttributes.PNP_ID)
                    || uuidchara.equalsIgnoreCase(GattAttributes.IEEE)
                    || uuidchara.equalsIgnoreCase(GattAttributes.SYSTEM_ID)) {
                Logger.i("Characteristic div" + uuidchara);
                prepareBroadcastDataRead(gattCharacteristic);
                break;
            }
        }
    }

/*    @Override
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

}
