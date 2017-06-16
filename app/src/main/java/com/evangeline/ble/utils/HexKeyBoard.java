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
package com.evangeline.ble.utils;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.evangeline.ble.R;


/**
 * HexKey board to be displayed when writing a value to characteristics and descriptors
 */
public class HexKeyBoard extends Dialog implements View.OnClickListener {

    // Write dialog buttons
    private Button mBttwo;
    private Button mBtthree;
    private Button mBtfour;
    private Button mBtfive;
    private Button mBtsix;
    private Button mBtseven;
    private Button mBteight;
    private Button mBtnine;
    private Button mBtzero;
    private Button mBta;
    private Button mBtb;
    private Button mBtc;
    private Button mBtd;
    private Button mBte;
    private Button mBtf;
    private Button mBthex;
    private Button mBtone;
    private ImageButton mBtnback;

    // Converting to hex variables
    private String hexValueString = "";
    private String hexsubstring = "0x";

    // HexValue entered
    private EditText mHexvalue;

    //Descriptor
    private BluetoothGattDescriptor mGattDescriptor;

    //Characteristic
    private BluetoothGattCharacteristic mGattCharacteristic;

    //Flag for Descriptor and characteristic
    private Boolean mIsDescriptor = false;
    private Boolean mIsCharacteristic = false;

    //Dialog listner
    private DialogListner mDialogListner;


    /**
     * Descriptor Constructor for the class
     *
     * @param activity
     * @param bluetoothGattDescriptor
     * @param isDescriptor
     */
    public HexKeyBoard(Activity activity, BluetoothGattDescriptor bluetoothGattDescriptor,
                       Boolean isDescriptor) {
        super(activity);
        this.mGattDescriptor = bluetoothGattDescriptor;
        this.mIsDescriptor = isDescriptor;
    }

    /**
     * Characteristic Constructor for the class
     *
     * @param activity
     * @param bluetoothGattCharacteristic
     * @param isCharacteristic
     */
    public HexKeyBoard(Activity activity, BluetoothGattCharacteristic bluetoothGattCharacteristic,
                       Boolean isCharacteristic) {
        super(activity);
        this.mGattCharacteristic = bluetoothGattCharacteristic;
        this.mIsCharacteristic = isCharacteristic;
    }

    public void setDialogListner(DialogListner mDialogListner) {
        this.mDialogListner = mDialogListner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hex_value);
        // Custom keyboard Buttons
        Button viewOk = (Button) findViewById(R.id.dailog_hex_value_okBtn);
        Button viewCancel = (Button) findViewById(R.id.dailog_hex_value_cancelBtn);
        mBta = (Button) findViewById(R.id.dailog_hex_value_btna);
        mBtb = (Button) findViewById(R.id.dailog_hex_value_btnb);
        mBtc = (Button) findViewById(R.id.dailog_hex_value_btnc);
        mBtd = (Button) findViewById(R.id.dailog_hex_value_btnd);
        mBte = (Button) findViewById(R.id.dailog_hex_value_btne);
        mBtf = (Button) findViewById(R.id.dailog_hex_value_btnf);
        mBtzero = (Button) findViewById(R.id.dailog_hex_value_btnzero);
        mBtnback = (ImageButton) findViewById(R.id.dailog_hex_value_btnback);
        mBtone = (Button) findViewById(R.id.dailog_hex_value_oneBtn);
        mBttwo = (Button) findViewById(R.id.dailog_hex_value_twoBtn);
        mBtthree = (Button) findViewById(R.id.dailog_hex_value_threeBtn);
        mBtfour = (Button) findViewById(R.id.dailog_hex_value_fourBtn);
        mBtfive = (Button) findViewById(R.id.dailog_hex_value_fiveBtn);
        mBtsix = (Button) findViewById(R.id.dailog_hex_value_sixBtn);
        mBtseven = (Button) findViewById(R.id.dailog_hex_value_sevenBtn);
        mBteight = (Button) findViewById(R.id.dailog_hex_value_eightBtn);
        mBtnine = (Button) findViewById(R.id.dailog_hex_value_nineBtn);
        mBthex = (Button) findViewById(R.id.dailog_hex_value_btnhex);
        mHexvalue = (EditText) findViewById(R.id.dailog_hex_value_edtText);
        mHexvalue.setText("");

        // Custom keyboard listeners
        mBta.setOnClickListener(this);
        mBtb.setOnClickListener(this);
        mBtc.setOnClickListener(this);
        mBtd.setOnClickListener(this);
        mBte.setOnClickListener(this);
        mBtf.setOnClickListener(this);
        mBtzero.setOnClickListener(this);
        mBtone.setOnClickListener(this);
        mBttwo.setOnClickListener(this);
        mBtthree.setOnClickListener(this);
        mBtfour.setOnClickListener(this);
        mBtfive.setOnClickListener(this);
        mBtsix.setOnClickListener(this);
        mBtseven.setOnClickListener(this);
        mBteight.setOnClickListener(this);
        mBtnine.setOnClickListener(this);
        mBtnback.setOnClickListener(this);
        mBthex.setOnClickListener(this);

        // EditText touch listener
        mHexvalue.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });
        viewOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mHexvalue.getText().toString().length() > 0) {
                    String hexValueString = mHexvalue.getText().toString();
                    mDialogListner.dialog0kPressed(hexValueString);
                } else {
                    mHexvalue.setText("");
                }
                cancel();
            }

        });
        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                mDialogListner.dialogCancelPressed(true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dailog_hex_value_btna:
                hexValueUpatetemp("A");
                break;
            case R.id.dailog_hex_value_btnb:
                hexValueUpatetemp("B");
                break;
            case R.id.dailog_hex_value_btnc:
                hexValueUpatetemp("C");
                break;
            case R.id.dailog_hex_value_btnd:
                hexValueUpatetemp("D");
                break;
            case R.id.dailog_hex_value_btne:
                hexValueUpatetemp("E");
                break;
            case R.id.dailog_hex_value_btnf:
                hexValueUpatetemp("F");
                break;
            case R.id.dailog_hex_value_btnzero:
                hexValueUpatetemp("0");
                break;
            case R.id.dailog_hex_value_oneBtn:
                hexValueUpatetemp("1");
                break;
            case R.id.dailog_hex_value_twoBtn:
                hexValueUpatetemp("2");
                break;
            case R.id.dailog_hex_value_threeBtn:
                hexValueUpatetemp("3");
                break;
            case R.id.dailog_hex_value_fourBtn:
                hexValueUpatetemp("4");
                break;
            case R.id.dailog_hex_value_fiveBtn:
                hexValueUpatetemp("5");
                break;
            case R.id.dailog_hex_value_sixBtn:
                hexValueUpatetemp("6");
                break;
            case R.id.dailog_hex_value_sevenBtn:
                hexValueUpatetemp("7");
                break;
            case R.id.dailog_hex_value_eightBtn:
                hexValueUpatetemp("8");
                break;
            case R.id.dailog_hex_value_nineBtn:
                hexValueUpatetemp("9");
                break;
            case R.id.dailog_hex_value_btnback:
                backbuttonpressed();
                break;
            case R.id.dailog_hex_value_btnhex:
                hexUpdate();
                break;

        }
    }

    /**
     * HexValue appending with hexSubstring
     */
    private void hexUpdate() {
        hexsubstring = "0x";
        hexValueString = hexValueString + " " + hexsubstring;
        hexsubstring = "";
        mHexvalue.setText(hexValueString.trim());
        mHexvalue.setSelection(hexValueString.trim().length());
    }

    /**
     * Update the editText field with hexValues
     *
     * @param string
     */
    private void hexValueUpatetemp(String string) {
        if (hexValueString.length() != 0) {

            String[] splited = hexValueString.split("\\s+");

            int arrayCount = splited.length;
            if (arrayCount != 0) {
                String lastValue = splited[arrayCount - 1];
                int last = lastValue.length();
                if (last == 4) {
                    hexValueString = hexValueString + " 0x" + string;
                } else if (last == 3 || last == 2) {
                    hexValueString = hexValueString + string;
                }

                mHexvalue.setText(hexValueString.trim());
                mHexvalue.setSelection(hexValueString.trim().length());
            }
        } else {
            hexValueString = "0x" + string;
            mHexvalue.setText(hexValueString.trim());
            mHexvalue.setSelection(hexValueString.trim().length());
        }
    }

    /**
     * Custom keyboard back pressed action
     */
    private void backbuttonpressed() {

        if (hexValueString.length() != 0) {

            String[] splited = hexValueString.split("\\s+");

            int last = splited.length;
            if (last != 0) {
                String substring = splited[last - 1];
                if ((substring.length() == 4) || (substring.length() == 3)) {
                    substring = substring.substring(0, substring.length() - 1);
                    splited[last - 1] = substring;
                    hexValueString = "";
                    for (int i = 0; i < splited.length; i++) {
                        hexValueString = hexValueString + " " + splited[i];
                    }
                } else if (substring.length() == 2) {
                    hexValueString = "";
                    for (int i = 0; i < splited.length - 1; i++) {
                        hexValueString = hexValueString + " " + splited[i];
                    }
                }
                mHexvalue.setText(hexValueString.trim());
                mHexvalue.setSelection(hexValueString.trim().length());
            }
        }
    }
}
