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

package com.evangeline.ble.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.evangeline.ble.R;
import com.evangeline.ble.utils.GattAttributes;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for GattService ListView
 */
public class GattServiceListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData;
    private Context mContext;

    public GattServiceListAdapter(Context mContext,
                                  ArrayList<HashMap<String, BluetoothGattService>> list) {
        this.mContext = mContext;
        this.mGattServiceData = list;
    }

    @Override
    public int getCount() {
        return mGattServiceData.size();
    }

    @Override
    public Object getItem(int i) {
        return mGattServiceData.get(i);
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
            view = mInflator.inflate(R.layout.item_gattdb_services,
                    viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.serviceName.setSelected(true);
        HashMap<String, BluetoothGattService> item = mGattServiceData.get(i);
        BluetoothGattService bgs = item.get("UUID");
        String name = GattAttributes.lookupUUID(
                bgs.getUuid(),
                mContext.getResources().getString(
                        R.string.profile_control_unknown_service));


        viewHolder.serviceName.setText(name);
        return view;
    }

    /**
     * Holder class for holding the ListView elements
     */
    class ViewHolder {

        @BindView(R.id.item_gattdb_serivces_nameTv)
        TextView serviceName;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }

    }

}
