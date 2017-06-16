package com.evangeline.ble.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evangeline.ble.R;
import com.evangeline.ble.utils.GattAttributes;
import com.evangeline.ble.utils.UUIDDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2017/6/12.
 */
public class ServiceAdapter extends BaseExpandableListAdapter {

    private List<BluetoothGattService> mServices = new ArrayList<>();
    private ArrayList<ArrayList<BluetoothGattCharacteristic>>mCharacteristics = new ArrayList<>();
    private Context mContext;
    private OnServiceItemLongClickListener mListener;

    public ServiceAdapter(List<BluetoothGattService> services,ArrayList<ArrayList<BluetoothGattCharacteristic>>characteristics,Context context ,
                          OnServiceItemLongClickListener listener){
        this.mServices = services;
        this.mCharacteristics = characteristics;
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public int getGroupCount() {
        if(mServices==null){
            return 0;
        }
        return mServices.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(mCharacteristics==null){
            return 0;
        }
        return mCharacteristics.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return mServices.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mCharacteristics.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        ServiceViewHolder serviceVHolder =null;
        if(view==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_expand_service,viewGroup,false);
            serviceVHolder = new ServiceViewHolder(view);
            view.setTag(serviceVHolder);
        }else{
           serviceVHolder = (ServiceViewHolder) view.getTag();
        }
       /* if(b){//isExpand
            serviceVHolder.iconImv.setImageResource(R.drawable.icon_jiao_1);
        }else{
            serviceVHolder.iconImv.setImageResource(R.drawable.icon_jiao_2);
        }*/
        BluetoothGattService item = mServices.get(i);
        String name = GattAttributes.lookupUUID(item.getUuid(), item.getUuid()
                .toString());
        if (item.getUuid().equals(UUIDDatabase.UUID_REP0RT)) {
            name = GattAttributes.lookupReferenceRDK(item.getInstanceId(), name);
        }
        int id = i+1;
        serviceVHolder.idTv.setText("Service"+Integer.toString(id));
        serviceVHolder.nameTv.setText(name);
        int childCount = mCharacteristics.get(i).size();
        serviceVHolder.childCountTv.setText("( "+Integer.toString(childCount)+" )");

        serviceVHolder.childCountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onItemClickListener(i,mServices.get(i));
                }
            }
        });
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        CharacterViewHolder characterVHolder = null;
        if(view==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_expand_character,viewGroup,false);
            characterVHolder = new CharacterViewHolder(view);
            view.setTag(characterVHolder);
        }else{
            characterVHolder = (CharacterViewHolder) view.getTag();
        }
        BluetoothGattCharacteristic item = mCharacteristics.get(i).get(i1);
        String name = GattAttributes.lookupUUID(item.getUuid(), item.getUuid()
                .toString());
        if (item.getUuid().equals(UUIDDatabase.UUID_REP0RT)) {
            name = GattAttributes.lookupReferenceRDK(item.getInstanceId(), name);
        }
        if(name.equals(item.getUuid().toString())){
            name="Unknown character";
        }
        characterVHolder.nameTv.setText(name);
        characterVHolder.uuidTv.setText(item.getUuid().toString());

        String read = null, write = null, notify = null,indicate =null;

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
           indicate = mContext.getString(R.string.gatt_services_indicate);
        }
        // Handling multiple properties listing in the ListView
        if (read != null) {
            characterVHolder.readTv.setVisibility(View.VISIBLE);
        } else {
           characterVHolder.readTv.setVisibility(View.GONE);
        }
        if (write != null) {
            characterVHolder.writeTv.setVisibility(View.VISIBLE);
        } else {
            characterVHolder.writeTv.setVisibility(View.GONE);
        }
        if (notify != null) {
            characterVHolder.notifyTv.setVisibility(View.VISIBLE);
        } else {
            characterVHolder.notifyTv.setVisibility(View.GONE);
        }
        if (indicate!= null) {
            characterVHolder.indicateTv.setVisibility(View.VISIBLE);
        } else {
            characterVHolder.indicateTv.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public class ServiceViewHolder{
        @BindView(R.id.item_service_ll)LinearLayout mLl;
        @BindView(R.id.item_service_id)TextView idTv;
        @BindView(R.id.item_service_nameTv)TextView nameTv;
        @BindView(R.id.item_service_childCountTv)TextView childCountTv;
        public ServiceViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    public class CharacterViewHolder{

        @BindView(R.id.item_character_rl)RelativeLayout characterRl;
        @BindView(R.id.item_character_nameTv)TextView nameTv;
        @BindView(R.id.item_character_uuidTv)TextView uuidTv;
        @BindView(R.id.item_character_readTv)TextView readTv;
        @BindView(R.id.item_character_writeTv)TextView writeTv;
        @BindView(R.id.item_character_notifyTv)TextView notifyTv;
        @BindView(R.id.item_character_indicateTv)TextView indicateTv;
        public CharacterViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    // Return the properties of mGattCharacteristics
    boolean getGattCharacteristicsPropertices(int characteristics,
                                              int characteristicsSearch) {

        if ((characteristics & characteristicsSearch) == characteristicsSearch) {
            return true;
        }
        return false;
    }

    public interface OnServiceItemLongClickListener{
        public void onItemClickListener( int position, BluetoothGattService service);
    }
}
