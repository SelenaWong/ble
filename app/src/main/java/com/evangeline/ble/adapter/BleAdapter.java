package com.evangeline.ble.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evangeline.ble.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/6/12.
 */
public class BleAdapter extends RecyclerView.Adapter<BleAdapter.BleViewHolder> {

    private List<BluetoothDevice> mDevices = new ArrayList<>();
    private Map<String ,Integer> mRSSIMap =new HashMap<>();
    private OnItemClickListener mListener;

    public BleAdapter(List<BluetoothDevice> devices,Map<String ,Integer> rris,OnItemClickListener listener){
        mDevices = devices;
        mRSSIMap =rris;
        mListener = listener;
    }


    @Override
    public BleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       BleViewHolder bleViewHolder =  new BleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble,parent,false));
        return bleViewHolder;
    }

    @Override
    public void onBindViewHolder(BleViewHolder holder, final int position) {
        BluetoothDevice device = mDevices.get(position);
        if(TextUtils.isEmpty(device.getName())){
            holder.nameTv.setText("Unknow device");
        }else{
            holder.nameTv.setText(device.getName());
        }
        holder.addressTv.setText(device.getAddress());
        holder.rssidTv.setText("RSSI: "+Integer.toString(mRSSIMap.get(device.getAddress()))+" dBm");
        holder.pariedStateTv.setText("未配对");
        holder.bleRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onBleClickListener(position,mDevices.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mDevices!=null){
            return mDevices.size();
        }
        return 0;
    }

    public static class BleViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_ble_rl)RelativeLayout bleRl;
        @BindView(R.id.item_ble_nameTv)TextView nameTv;
        @BindView(R.id.item_ble_pariedStateTv)TextView pariedStateTv;
        @BindView(R.id.item_ble_rssidTv)TextView rssidTv;
        @BindView(R.id.item_ble_addressTv)TextView addressTv;
        public BleViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public interface  OnItemClickListener{
        public void onBleClickListener( int position,BluetoothDevice device );
    }
}
