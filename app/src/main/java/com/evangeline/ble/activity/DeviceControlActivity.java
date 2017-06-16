package com.evangeline.ble.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evangeline.ble.OTAFirmwareUpdate.OTAFirmwareUpgradeActivity;
import com.evangeline.ble.R;
import com.evangeline.ble.adapter.BleAdapter;
import com.evangeline.ble.adapter.ServiceAdapter;
import com.evangeline.ble.app.App;
import com.evangeline.ble.bleServiceActivity.DeviceInformationServiceActivity;
import com.evangeline.ble.bleServiceActivity.GattServicesActivity;
import com.evangeline.ble.service.BluetoothLeService;
import com.evangeline.ble.utils.Constants;
import com.evangeline.ble.utils.Logger;
import com.evangeline.ble.utils.UUIDDatabase;
import com.evangeline.ble.view.XDividerItemDecoration;
import com.evangeline.ble.view.loadingdrawable.LoadingView;
import com.evangeline.ble.view.loadingdrawable.render.LoadingDrawable;
import com.evangeline.ble.view.loadingdrawable.render.circle.rotate.LevelLoadingRenderer;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceControlActivity extends CheckPermissionsActivity {

    private BluetoothLeService mBluetoothLeService;
    private List<BluetoothGattService> services = new ArrayList<>();
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mCharacters = new ArrayList<>();

    @BindView(R.id.activity_device_control_elv)
    ExpandableListView serviceElv;
    @BindView(R.id.activity_device_control_addressTv)
    TextView mAddressTv;
    @BindView(R.id.activity_device_control_nameTv)
    TextView mNameTv;
    @BindView(R.id.activity_device_control_stateTv)
    TextView mStateTv;

    @BindView(R.id.activity_device_control_noServiceTv)TextView mNoServiceTv;
    @BindView(R.id.activity_device_control_load_levelView)LoadingView mLoadView;
    private LoadingDrawable mLevelDrawable;
    private ServiceAdapter serviceAdapter;
    private boolean mConnected = false;
    private DeviceControlActivity activity = this;
    private String address;
    private String name;
    private String TAG = DeviceControlActivity.class.getSimpleName();


    private App mApplication;
    private ProgressDialog mProgressDialog;
    // GattService and Characteristics Mapping
    private  static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private  static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceFindMeData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceProximityData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private  static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceSensorHubData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattdbServiceData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceMasterData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    private final ServiceConnection serviceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) iBinder).getService();
            if (!mBluetoothLeService.initalize()) {
                finish();
            }
            mBluetoothLeService.connect(getApplicationContext(), address, name);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "mReceiver get action=" + action);
            if (Constants.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                mHandler.post(runnable);
                stopLoadView();
            } else if (Constants.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mBluetoothLeService.disconnect();
                mHandler.post(runnable);
            } else if (Constants.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "discovery service");
                displayService(mBluetoothLeService.getSupportedGattServices());
            } else if (Constants.ACTION_DATA_AVAILABLE.equals(action)) {
                //data available
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mConnected){
                Log.i(TAG,"update connect state to connected");
                mStateTv.setText(getResources().getString(R.string.device_connected_state));
            }else{
                Log.i(TAG,"update connect state to disconnected");
                mStateTv.setText(getResources().getString(R.string.device_disconnected_state));
            }
        }
    };

    public void showLoadView(){
        if(!mLoadView.isShown()){
            mLoadView.setVisibility(View.VISIBLE);
            mLevelDrawable = new LoadingDrawable(new LevelLoadingRenderer(DeviceControlActivity.this));
            mLoadView.setImageDrawable(mLevelDrawable);
            mLevelDrawable.start();
        }
    }

    public void stopLoadView(){
        if(mLoadView.isShown()){
            mLevelDrawable.stop();
            mLoadView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_device_control);
        ButterKnife.bind(this);
        Intent it = new Intent(DeviceControlActivity.this, BluetoothLeService.class);
        Log.i(TAG, "perpare bind service");
        bindService(it, serviceConnect, BIND_AUTO_CREATE);
        mApplication = (App)getApplication();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        setAdapter();
        showLoadView();
    }

    @Override
    protected void initData() {
        super.initData();
        Intent it = getIntent();
        address = it.getStringExtra("ADDRESS");
        name = it.getStringExtra("NAME");
        mAddressTv.setText(address);
        mNameTv.setText(name);
    }

    public void setAdapter() {
        serviceAdapter = new ServiceAdapter(services, mCharacters, activity, new ServiceAdapter.OnServiceItemLongClickListener() {
            @Override
            public void onItemClickListener(int position, BluetoothGattService service) {
                goServiceActivity(service);
            }
        });
        serviceElv.setAdapter(serviceAdapter);

    }

    public static boolean isInFragment = false;

    public void goServiceActivity(BluetoothGattService service) {
        UUID uuid = service.getUuid();
        App mApplication = (App) getApplication();
        mApplication.setmBluetoothGattService(service);
        // Device information service
        if (service.getUuid().equals(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE)) {
            isInFragment = true;
            Intent it = new Intent(DeviceControlActivity.this, DeviceInformationServiceActivity.class);
            Bundle bd = new Bundle();
            bd.putString("UUID", uuid.toString());
            startActivity(it);
        }
        // GattDB
        else if (service.getUuid().equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)
                || service.getUuid().equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)) {
            isInFragment = true;
            Intent it = new Intent(DeviceControlActivity.this, GattServicesActivity.class);
            Bundle bd = new Bundle();
            bd.putString("UUID", uuid.toString());
            startActivity(it);
        }
        // OTA Firmware Update Service
        else if (service.getUuid().equals(UUIDDatabase.UUID_OTA_UPDATE_SERVICE)) {
            Log.i("CarouselFragment", "OTA_UPDATE_SERVICE");
            if (Constants.OTA_ENABLED) {
                isInFragment = true;
                Intent it = new Intent(DeviceControlActivity.this, OTAFirmwareUpgradeActivity.class);
                startActivity(it);

            } else {
                showWarningMessage();
            }
        } else {
            showWarningMessage();
        }
    }


    void showWarningMessage() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                DeviceControlActivity.this);
        // set title
        alertDialogBuilder
                .setTitle(R.string.alert_message_unknown_title);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.alert_message_unkown)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_message_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                Intent it = new Intent(DeviceControlActivity.this, GattServicesActivity.class);
                                startActivity(it);
                            }
                        })
                .setNegativeButton(R.string.alert_message_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void displayService(List<BluetoothGattService> bluetoothGattServices) {
        if (bluetoothGattServices == null) {
            return;
        }
        for (BluetoothGattService service : bluetoothGattServices) {
            services.add(service);
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> characteristicList = new ArrayList<>();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                characteristicList.add(characteristic);
            }
            mCharacters.add(characteristicList);
        }
        prepareGattServices(bluetoothGattServices);
        serviceAdapter.notifyDataSetChanged();
    }

    /**
     * Getting the GATT Services
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        // Optimization code for Sensor HUb
        if (isSensorHubPresent(gattServices)) {
            prepareSensorHubData(gattServices);
        } else {
            prepareData(gattServices);
        }

    }
    /**
     * Check whether SensorHub related services are present in the discovered
     * services
     *
     * @param gattServices
     * @return {@link Boolean}
     */
    boolean isSensorHubPresent(List<BluetoothGattService> gattServices) {
        boolean present = false;
        for (BluetoothGattService gattService : gattServices) {
            UUID uuid = gattService.getUuid();
            if (uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                present = true;
            }
        }
        return present;
    }
    private void prepareSensorHubData(List<BluetoothGattService> gattServices) {

        boolean mGattSet = false;
        boolean mSensorHubSet = false;

        if (gattServices == null)
            return;
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceMasterData.clear();
        mGattServiceSensorHubData.clear();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> mCurrentServiceData = new HashMap<String, BluetoothGattService>();
            UUID uuid = gattService.getUuid();
            // Optimization code for SensorHub Profile
            if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ACCELEROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ANALOG_TEMPERATURE_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BATTERY_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                if (!mGattServiceSensorHubData.contains(mCurrentServiceData)) {
                    mGattServiceSensorHubData.add(mCurrentServiceData);
                }
                if (!mSensorHubSet
                        && uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                    mSensorHubSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }
            }
            // Optimization code for GATTDB
            else if (uuid
                    .equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattdbServiceData.add(mCurrentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }
            } else {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                mGattServiceData.add(mCurrentServiceData);
            }
        }
        mApplication.setGattServiceMasterData(mGattServiceMasterData);
        if(mGattdbServiceData.size()<=0){
            mNoServiceTv.setVisibility(View.VISIBLE);
            Logger.e("No service found");
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"没有服务被发现",Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareData(List<BluetoothGattService> gattServices) {
        boolean mFindmeSet = false;
        boolean mProximitySet = false;
        boolean mGattSet = false;
        if (gattServices == null)
            return;
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceFindMeData.clear();
        mGattServiceMasterData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> currentServiceData = new HashMap<String, BluetoothGattService>();
            UUID uuid = gattService.getUuid();
            Log.i("ServiceDiscoveryF","uuid="+uuid.toString());
            // Optimization code for FindMe Profile
            if (uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceFindMeData.contains(currentServiceData)) {
                    mGattServiceFindMeData.add(currentServiceData);
                }
                if (!mFindmeSet) {
                    mFindmeSet = true;
                    mGattServiceData.add(currentServiceData);
                }

            }
            // Optimization code for Proximity Profile
            else if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceProximityData.contains(currentServiceData)) {
                    mGattServiceProximityData.add(currentServiceData);
                }
                if (!mProximitySet) {
                    mProximitySet = true;
                    mGattServiceData.add(currentServiceData);
                }

            }// Optimization code for GATTDB
            else if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattdbServiceData.add(currentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(currentServiceData);
                }

            } //Optimization code for HID
            else if (uuid.equals(UUIDDatabase.UUID_HID_SERVICE)){
                /**
                 * Special handling for KITKAT devices
                 */
                if (Build.VERSION.SDK_INT < 21) {
                    Logger.e("Kitkat RDK device found");
                    List<BluetoothGattCharacteristic> allCharacteristics=
                            gattService.getCharacteristics();
                    List<BluetoothGattCharacteristic> RDKCharacteristics=new
                            ArrayList<BluetoothGattCharacteristic>();
                    List<BluetoothGattDescriptor> RDKDescriptors=new
                            ArrayList<BluetoothGattDescriptor>();

                    //Find all Report characteristics
                    for(BluetoothGattCharacteristic characteristic:allCharacteristics){
                        if(characteristic.getUuid().equals(UUIDDatabase.UUID_REP0RT)){
                            RDKCharacteristics.add(characteristic);
                        }
                    }

                    //Find all Report descriptors
                    for(BluetoothGattCharacteristic rdkcharacteristic:RDKCharacteristics){
                        List<BluetoothGattDescriptor> descriptors = rdkcharacteristic.
                                getDescriptors();
                        for(BluetoothGattDescriptor descriptor:descriptors){
                            RDKDescriptors.add(descriptor);
                        }
                    }
                    /**
                     * Wait for all  descriptors to receive
                     */
                    if(RDKDescriptors.size()==RDKCharacteristics.size()*2){

                        for(int pos=0,descPos=0;descPos<RDKCharacteristics.size();pos++,descPos++){
                            BluetoothGattCharacteristic rdkcharacteristic=
                                    RDKCharacteristics.get(descPos);
                            //Mapping the characteristic and descriptors
                            Logger.e("Pos-->"+pos);
                            Logger.e("Pos+1-->"+(pos+1));
                            BluetoothGattDescriptor clientdescriptor=RDKDescriptors.get(pos);
                            BluetoothGattDescriptor reportdescriptor=RDKDescriptors.get(pos+1);
                            if(!rdkcharacteristic.getDescriptors().contains(clientdescriptor))
                                rdkcharacteristic.addDescriptor(clientdescriptor);
                            if(!rdkcharacteristic.getDescriptors().contains(reportdescriptor))
                                rdkcharacteristic.addDescriptor(reportdescriptor);
                            pos++;
                        }
                    }
                    currentServiceData.put(LIST_UUID, gattService);
                    mGattServiceMasterData.add(currentServiceData);
                    mGattServiceData.add(currentServiceData);
                }else{
                    currentServiceData.put(LIST_UUID, gattService);
                    mGattServiceMasterData.add(currentServiceData);
                    mGattServiceData.add(currentServiceData);
                }

            }else {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                mGattServiceData.add(currentServiceData);
            }

        }
        mApplication.setGattServiceMasterData(mGattServiceMasterData);
        if(mGattdbServiceData.size()==0){
            mProgressDialog.dismiss();
            //showNoServiceDiscoverAlert();
            Toast.makeText(getApplicationContext(),"没有服务被发现",Toast.LENGTH_SHORT).show();
        }
    }



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isInFragment = false;
        registerReceiver(mReceiver, makeFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(getApplicationContext(), address, name);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    public IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_GATT_CONNECTED);
        filter.addAction(Constants.ACTION_GATT_DISCONNECTED);
        filter.addAction(Constants.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(Constants.ACTION_DATA_AVAILABLE);
        return filter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnect);
        mBluetoothLeService.disconnect();
        mBluetoothLeService = null;
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        super.onDestroy();

    }

}
