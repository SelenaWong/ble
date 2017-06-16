package com.evangeline.ble.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evangeline.ble.R;
import com.evangeline.ble.utils.GattAttributes;
import com.evangeline.ble.utils.UUIDDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/6/15.
 */
public class GattCharacteriscticsAdapter extends BaseAdapter {


    private List<BluetoothGattCharacteristic> mGattCharacteristics;
    private Context mContext;

    public GattCharacteriscticsAdapter(Context mContext,
                                       List<BluetoothGattCharacteristic> list) {
        this.mContext = mContext;
        this.mGattCharacteristics = list;
    }

    @Override
    public int getCount() {
        if(mGattCharacteristics!=null){
            return mGattCharacteristics.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return mGattCharacteristics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder =null;
        if(view!=null){
           viewHolder =(ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_gatt_characteristics,viewGroup,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.serviceName.setSelected(true);
        BluetoothGattCharacteristic item = mGattCharacteristics.get(i);
        String name = GattAttributes.lookupUUID(item.getUuid(), item
                .getUuid().toString());
        //Report Reference lookup based on InstanceId
        if (item.getUuid().equals(UUIDDatabase.UUID_REP0RT)) {
            name = GattAttributes.lookupReferenceRDK(item.getInstanceId(), name);
        }

        viewHolder.serviceName.setText(name);
        String proprties;
        String read = null, write = null, notify = null;

        /**
         * Checking the various GattCharacteristics and listing in the ListView
         */
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_READ)) {
            read = mContext.getString(R.string.gatt_services_read);
        }
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE)
                | getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            write = mContext.getString(R.string.gatt_services_write);
        }
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            notify = mContext.getString(R.string.gatt_services_notify);
        }
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            notify = mContext.getString(R.string.gatt_services_indicate);
        }
        // Handling multiple properties listing in the ListView
        if (read != null) {
            proprties = read;
            if (write != null) {
                proprties = proprties + " & " + write;
            }
            if (notify != null) {
                proprties = proprties + " & " + notify;
            }
        } else {
            if (write != null) {
                proprties = write;
                if (notify != null) {
                    proprties = proprties + " & " + notify;
                }
            } else {
                proprties = notify;
            }
        }
        viewHolder.statusTv.setText(proprties);
        return view;
    }

    /**
     * Holder class for the ListView variable
     */
    class ViewHolder {
        @BindView(R.id.item_gatt_characteristics_serviceNameTv) TextView serviceName;
        @BindView(R.id.item_gatt_characteristics_statusTv)TextView  statusTv;
        public  ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    // Return the properties of mGattCharacteristics
    boolean getGattCharacteristicsPropertices(int characteristics,
                                              int characteristicsSearch ) {
        if ((characteristics & characteristicsSearch) == characteristicsSearch ){
            return true;
        }
        return false;
    }
}
