package com.evangeline.ble.adapter;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evangeline.ble.R;
import com.evangeline.ble.utils.GattAttributes;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/6/15.
 */
public class GattCharacteristicDescriptorsAdapter extends BaseAdapter {

    private List<BluetoothGattDescriptor> mGattCharacteristics;

    private Context mContext;

    public GattCharacteristicDescriptorsAdapter(Context mContext,
                                                List<BluetoothGattDescriptor> list) {
        this.mContext = mContext;
        this.mGattCharacteristics = list;
    }

    @Override
    public int getCount() {
        return mGattCharacteristics.size();
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
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            LayoutInflater mInflator = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.item_gattdb_characteristics,
                    viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.serviceName.setSelected(true);
        BluetoothGattDescriptor item = mGattCharacteristics.get(i);
        String name = GattAttributes.lookupUUID(item.getUuid(), item
                .getUuid().toString());
        viewHolder.serviceName.setText(name);
        viewHolder.propertyName.setText("" + item.getUuid().toString());
        viewHolder.parameter.setText("UUID :");

        return view;
    }

    /**
     * Holder class for the ListView variable
     */
    class ViewHolder {

        @BindView(R.id.item_gattdb_characteristics_txtservicename) TextView serviceName;
        @BindView(R.id.item_gattdb_characteristics_txtstatus) TextView propertyName;
        @BindView(R.id.item_gatt_characteristics_parameterTv) TextView parameter;
        ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
