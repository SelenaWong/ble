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

package com.evangeline.ble.logActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.evangeline.ble.R;
import com.evangeline.ble.activity.BaseActivity;
import com.evangeline.ble.adapter.DataLogsListAdapter;
import com.evangeline.ble.utils.Constants;
import com.evangeline.ble.utils.Logger;
import com.evangeline.ble.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Fragment to show the DataLogger
 */
public class DataLoggerActivity extends BaseActivity implements AbsListView.OnScrollListener {
    /**
     * FilePath of DataLogger
     */
    private static String mFilepath;
    int mTotalLinesToRead = 0;
    ProgressDialog mProgressDialog;
    private static String mLastFragment;
    /**
     * Log Data Temporay storage
     */
    ArrayList<String> mReadLogData;
    /**
     * List Adapter
     */
    DataLogsListAdapter mAdapter;
    /**
     * visibility flag
     */
    private boolean mVisible = false;
    /**
     * DataLogger text
     */
    private ListView mLogList;
    /**
     * Lazyloading variables
     */
    private int mStartLine = 0;
    private int mStopLine = 500;
    private boolean mLazyLoadingEnabled = false;
    /**
     * GUI elements
     */
    private TextView mFileName;
    private TextView mScrollDown;

    //Activity Request Code
    private static final int REQUEST_CODE = 123;

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.datalogger);
        mLogList = (ListView)findViewById(R.id.txtlog);
        mFileName = (TextView)findViewById(R.id.txt_file_name);
        mScrollDown = (TextView)findViewById(R.id.tv_scroll_down);
        /*
        /History option text
        */
        TextView mDataHistory = (TextView)findViewById(R.id.txthistory);
        //mScrollView = (CustumScrollView) rootView.findViewById(R.id.scroll_view_logger);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mFilepath = bundle.getString(Constants.DATA_LOGGER_FILE_NAAME);
            mVisible = bundle.getBoolean(Constants.DATA_LOGGER_FLAG);
            File fileinView = new File(mFilepath);
            mFileName.setText(fileinView.getName());
        }
        // Handling the history text visibility based on the received Arguments
        if (mVisible) {
            mDataHistory.setVisibility(View.GONE);
        } else {
            Toast.makeText(getApplicationContext(), getResources().
                            getString(R.string.data_logger_timestamp) + Utils.GetTimeandDateUpdate()
                    , Toast.LENGTH_SHORT).show();
            mDataHistory.setVisibility(View.VISIBLE);
        }
        mDataHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent dataloggerHistory = new Intent(DataLoggerActivity.this, DataLoggerHistoryList.class);
                startActivityForResult(dataloggerHistory, REQUEST_CODE);
            }
        });
        prepareData();
        mScrollDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogList.post(new Runnable() {
                    public void run() {
                        mLogList.setSelection(mLogList.getCount() - 1);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundleReceived = data.getExtras();
            mFilepath = bundleReceived.getString(Constants.DATA_LOGGER_FILE_NAAME);
            mVisible = bundleReceived.getBoolean(Constants.DATA_LOGGER_FLAG);
            File fileinView = new File(mFilepath);
            mFileName.setText(fileinView.getName());
            prepareData();
        }
    }

    @Override
    public void onResume() {
       setProgressBarIndeterminateVisibility(false);
        super.onResume();
    }

    @Override
    public void onPause() {
        if(!mReadLogData.isEmpty()) {
            mStartLine = 0;
            mStopLine = 500;
        }
        super.onPause();
    }

    public void prepareData() {
        mTotalLinesToRead = getTotalLines();
        mReadLogData = new ArrayList<String>();
        mAdapter = new DataLogsListAdapter(DataLoggerActivity.this, mReadLogData);
        mLogList.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(DataLoggerActivity.this);
        //scrollMyListViewToBottom();
        if (mTotalLinesToRead > 5000) {
            // mLogList.setOnScrollListener(this);
            mLazyLoadingEnabled = true;
            loadLogdata loadLogdata = new loadLogdata(mStartLine, mStopLine);
            Logger.e("Start Line>> " + mStartLine + "Stop Line>>" + mStopLine);
            loadLogdata.execute();
            mProgressDialog.setTitle(
                    getResources().
                            getString(R.string.app_name));
            mProgressDialog.setMessage(getResources().
                    getString(R.string.alert_message_log_read));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        } else {
            mLazyLoadingEnabled = false;
            loadLogdata loadLogdata = new loadLogdata(0, 0);
            loadLogdata.execute();
        }
    }

    /**
     * Reading the data from the file stored in the FilePath
     *
     * @return {@link String}
     * @throws FileNotFoundException
     */
    private ArrayList<String> logdata() throws FileNotFoundException {
        File file = new File(mFilepath);
        ArrayList<String> dataLines = new ArrayList<String>();
        if (!file.exists()) {
            return dataLines;
        } else {

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    dataLines.add(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataLines;
        }

    }

    /**
     * Reading the data from the file stored in the FilePath for particular set of lines
     *
     * @return {@link String}
     * @throws FileNotFoundException
     */
    private ArrayList<String> logdata(int startLine, int stopLine) throws FileNotFoundException {
        File file = new File(mFilepath);
        ArrayList<String> dataLines = new ArrayList<String>();
        if (!file.exists()) {
            return dataLines;
        } else {
            BufferedReader buffreader = new BufferedReader(new FileReader(file));
            String line;
            int lines = 0;
            try {
                while ((line = buffreader.readLine()) != null) {
                    lines++;
                    if (lines > startLine && lines <= stopLine) {
                        dataLines.add(line);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataLines;
        }
    }

    /**
     * Method to count the total lines in the selected file
     *
     * @return totalLines
     */
    public int getTotalLines() {
        int totalLines = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFilepath));
            while ((bufferedReader.readLine()) != null) {
                totalLines++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalLines;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (mLogList.getLastVisiblePosition() >= mLogList.getCount() - 1 - 0) {
                //load more list items:
                if (mLazyLoadingEnabled) {
                    mStartLine = mStopLine;
                    mStopLine = mStopLine + 500;
                    if (mStopLine < mTotalLinesToRead) {
                        loadLogdata loadLogdata = new loadLogdata(mStartLine, mStopLine);
                        loadLogdata.execute();
                    } else {
                        loadLogdata loadLogdata = new loadLogdata(mStartLine, mTotalLinesToRead);
                        loadLogdata.execute();
                        mLazyLoadingEnabled = false;
                    }
                }

            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }


  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.sharelogger:
                shareDataLoggerFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    /**
     * Sharing the data logger txt file
     */
    private void shareDataLoggerFile() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mFilepath)));
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Data Logger File");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }

    /**
     * AsyncTask class for loading logger data
     */
    private class loadLogdata extends AsyncTask<Void, Void, ArrayList<String>> {
        int startLine = 0;
        int stopLine = 0;
        ArrayList<String> newData = new ArrayList<String>();

        public loadLogdata(int startLine, int stopLine) {
            this.startLine = startLine;
            this.stopLine = stopLine;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected ArrayList<String> doInBackground(Void... params) {
            try {
                if (startLine == 0 && stopLine == 0) {
                    newData = logdata();
                } else {
                    newData = logdata(startLine, stopLine);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            return newData;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            mReadLogData.addAll(result);
            //load more list items:
            if (mLazyLoadingEnabled) {
                mStartLine = mStopLine;
                mStopLine = mStopLine + 500;
                if (mStopLine < mTotalLinesToRead) {
                    loadLogdata loadLogdata = new loadLogdata(mStartLine, mStopLine);
                    loadLogdata.execute();
                } else {
                    loadLogdata loadLogdata = new loadLogdata(mStartLine, mTotalLinesToRead);
                    loadLogdata.execute();
                    mLazyLoadingEnabled = false;
                    mProgressDialog.dismiss();
                }
            } else {
                mProgressDialog.dismiss();
            }
            Logger.i("Total size--->" + mReadLogData.size());
            mAdapter.addData(mReadLogData);
            mAdapter.notifyDataSetChanged();
        }
    }

}
