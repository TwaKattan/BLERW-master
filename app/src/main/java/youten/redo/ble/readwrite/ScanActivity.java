/*
 * Copyright (C) 2013 youten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package youten.redo.ble.readwrite;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import youten.redo.ble.util.BleUtil;
import youten.redo.ble.util.ScannedDevice;


@RuntimePermissions
public class ScanActivity extends Activity implements BluetoothAdapter.LeScanCallback {
    private BluetoothAdapter mBTAdapter;
    private DeviceAdapter mDeviceAdapter;

    private boolean mIsScanning;
    private String currentActivity;


    // here we refer to the devices list and xiomie devices
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // set the layout 1 content
        setContentView(R.layout.layout1);
        Log.i(ScanActivity.class.getSimpleName(), "In scan activity...");
// all devices button
        final Button button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.activity_scan);
                Log.i(ScanActivity.class.getSimpleName(), "Scan button clicked");
                // all devices back button

                init();
                final Button back_button = (Button) findViewById(R.id.button9);
                back_button.setOnClickListener(new View.OnClickListener() {
                                                   public void onClick(View v) {
                                                       Log.i(ScanActivity.class.getSimpleName(), "Stop scan clicked");
                                                       stopScan();
                                                       //setContentView(R.layout.layout1); after click back button go back to main layout
                                                       startActivity(new Intent(ScanActivity.this,ScanActivity.class));

                                                   }
                                               }

                );

            }

        });
        //// xiomi devices button
        /*
        final Button xbutton = (Button) findViewById(R.id.button6);
        xbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.activity_scan1);

                init2();
                // xiomi devices  back button
                final Button back_button = (Button) findViewById(R.id.button15);
                back_button.setOnClickListener(new View.OnClickListener() {
                                                   public void onClick(View v) {
                                                       stopScan();
                                                      // setContentView(R.layout.layout1);
                                                       startActivity(new Intent(ScanActivity.this,ScanActivity.class));

                                                   }
                                               }

                );

            }

        });

*/











        //// xiomi devices button
        final Button xbutton = (Button) findViewById(R.id.button7);
        xbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.activity_scan12);
                Log.i(ScanActivity.class.getSimpleName(), "Xiomi 2 Devices selected");
                init2();
                // xiomi devices  back button
                final Button back_button = (Button) findViewById(R.id.button152);
                back_button.setOnClickListener(new View.OnClickListener() {
                                                   public void onClick(View v) {
                                                       Log.i(ScanActivity.class.getSimpleName(), "Clicked back from xiomi2 list");
                                                       stopScan();
                                                       // setContentView(R.layout.layout12);
                                                       startActivity(new Intent(ScanActivity.this,ScanActivity.class));

                                                   }
                                               }

                );

            }

        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ScanActivity.class.getSimpleName(), "Scan activity onDestroy");
        stopScan();
    }

    @Override
    // the steps after click SCAN button, devices + Rssi + list of devices array

    // onLeScan function, detect every new bluetooth device and call the method update
    public void onLeScan(final BluetoothDevice newDeivce, final int newRssi,
                         final byte[] newScanRecord) {
        Log.i(ScanActivity.class.getSimpleName(), "Found new device" + newDeivce.getName() + " " + newDeivce.getAddress()
                + " with rssi " + newRssi);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mDeviceAdapter.update(newDeivce, newRssi, newScanRecord);

            }
        });
    }
    private void init() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.i(ScanActivity.class.getSimpleName(), "Initializing BLE");
        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBTAdapter.isEnabled()) {
            Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // init listview
        // list the all devices button devices,
        ListView deviceListView = (ListView) findViewById(R.id.list);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device,
                new ArrayList<ScannedDevice>());

        deviceListView.setAdapter(mDeviceAdapter);
        // scan started
        ScanActivityPermissionsDispatcher.startScanWithPermissionCheck(this);

        // when clicking on any device in the all devices button list , show a toast massage that "Device Information Not Available"
        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                ScannedDevice item = mDeviceAdapter.getItem(position);
                if (item != null) {
                    Log.i(ScanActivity.class.getSimpleName(), "Scanned device selected " + item.getDisplayName()
                            + " " + item.getDevice().getAddress());
                    Toast.makeText(getApplicationContext(), "Device Information Not Available", Toast.LENGTH_SHORT).show();

                    // stop before change Activity
                    stopScan();
                }
            }
        });


    }

    /*

// ALL SAME STEPS FOR XIOMIA DEVICES BUTTON //
    private void init2() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBTAdapter.isEnabled()) {
            Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // init listview
        ListView deviceListView = (ListView) findViewById(R.id.list1);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device1,
                new ArrayList<ScannedDevice>());


        deviceListView.setAdapter(mDeviceAdapter);
        ScanActivityPermissionsDispatcher.startScanWithPermissionCheck(this);

        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                ScannedDevice item = mDeviceAdapter.getItem(position);
                if (item != null) {
                    Intent intent = new Intent(view.getContext(), DeviceActivity.class);
                    BluetoothDevice selectedDevice = item.getDevice();
                    intent.putExtra(DeviceActivity.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
                    startActivity(intent);

                    // stop before change Activity
                    stopScan();
                }
            }
        });

        //stopScan();
    }

*/


    // ALL SAME STEPS FOR XIOMIA DEVICES BUTTON //
    private void init2() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.i(ScanActivity.class.getSimpleName(), "Initializing BLE for XIOMI");
        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBTAdapter.isEnabled()) {
            Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // init listview
        ListView deviceListView = (ListView) findViewById(R.id.list12);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device1,
                new ArrayList<ScannedDevice>());


        deviceListView.setAdapter(mDeviceAdapter);
        ScanActivityPermissionsDispatcher.startScanWithPermissionCheck(this);

        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                ScannedDevice item = mDeviceAdapter.getItem(position);
                if (item != null) {
                    Log.i(ScanActivity.class.getSimpleName(), "Scanned device selected " + item.getDisplayName()
                            + " " + item.getDevice().getAddress());
                    Intent intent = new Intent(view.getContext(), DeviceActivity.class);
                    BluetoothDevice selectedDevice = item.getDevice();
                    intent.putExtra(DeviceActivity.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
                    startActivity(intent);

                    // stop before change Activity
                    stopScan();
                }
            }
        });

        //stopScan();
    }

    // in BLE we must activate the location , with this step we notify the user to enable the location

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void startScan() {
        if ((mBTAdapter != null) && (!mIsScanning)) {

            mBTAdapter.startLeScan(this);
            mIsScanning = true;
            setProgressBarIndeterminateVisibility(true);
            invalidateOptionsMenu();
        }
    }

    private void stopScan() {
        if (mBTAdapter != null) {
            mBTAdapter.stopLeScan(this);
        }
        mIsScanning = false;
        setProgressBarIndeterminateVisibility(false);
        invalidateOptionsMenu();
    }
}
