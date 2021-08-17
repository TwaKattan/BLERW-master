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
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import youten.redo.ble.util.ScannedDevice;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class DeviceAdapter extends ArrayAdapter<ScannedDevice> {
    private static final String PREFIX_RSSI = "RSSI:";
    private static final String PREFIX_distance = "Distance:";

    private List<ScannedDevice> mList;

// the devices sequence in the list
    private LayoutInflater mInflater;
    private LayoutInflater mInflater1;

    private int mResId;
// save the listed devices in this class
    public DeviceAdapter(Context context, int resId, List<ScannedDevice> objects) {
        super(context, resId, objects);
        mResId = resId;
        mList = objects;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("---------------"+mResId);
        ScannedDevice item = (ScannedDevice) getItem(position);

// if the list view not defined, create one

        if (convertView == null ) {
            convertView = mInflater.inflate(mResId, null);
        }
        // check each button ID
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+mResId);


        // if scan all devices, then do the below 2131296289
        if(mResId==2131296289) {

            if(!item.getDevice().getAddress().contains("A4:C1:38") ) {

                TextView name = (TextView) convertView.findViewById(R.id.device_name);
            name.setText(item.getDisplayName());

            TextView address = (TextView) convertView.findViewById(R.id.device_address);
            address.setText(item.getDevice().getAddress());

            TextView rssi = (TextView) convertView.findViewById(R.id.device_rssi);
            rssi.setText(PREFIX_RSSI + Integer.toString(item.getRssi()));
            TextView device_distance = (TextView) convertView.findViewById(R.id.device_distance);
            device_distance.setText(PREFIX_distance + calaculatedistance(item.getRssi())+"Meter");
       }


    }// if all devices, then do the below 2131296289

        else if (mResId==2131296290 && item.getDevice().getAddress().contains("A4:C1:38")) {




            TextView name = (TextView) convertView.findViewById(R.id.device_name1);
            name.setText(item.getDisplayName());
            TextView address = (TextView) convertView.findViewById(R.id.device_address1);
            address.setText(item.getDevice().getAddress());
            TextView device_distance = (TextView) convertView.findViewById(R.id.device_distance1);
            device_distance.setText(PREFIX_distance + calaculatedistance(item.getRssi())+ "Meter");
        }

        return convertView;
    }

    /** add or update BluetoothDevice in the list */
    public void update(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
        if ((newDevice == null) || (newDevice.getAddress() == null)) {
            return;
        }

        boolean contains = false;
        for (ScannedDevice device : mList) {
            if (newDevice.getAddress().equals(device.getDevice().getAddress())) {
                contains = true;
                device.setRssi(rssi); // update
                break;
            }
        }

        // all scan
        if (!contains && !newDevice.getAddress().contains("A4:C1:38") && mResId==2131296289 ) {
            // add new BluetoothDevice
            mList.add(new ScannedDevice(newDevice, rssi));
        }


        // xiome scan
        if (!contains && newDevice.getAddress().contains("A4:C1:38") && mResId==2131296290  ) {
            // add new BluetoothDevice
            mList.add(new ScannedDevice(newDevice, rssi));
        }

        notifyDataSetChanged();

       }
       // calculate distance

       Serializable calaculatedistance(double rssi){
           DecimalFormat df = new DecimalFormat("##.##");
           df.setRoundingMode(RoundingMode.DOWN);

           if (rssi == 0){
            return -1;
        }
        double ratio = rssi*1.0/-59;
        if (ratio < 1.0) {
            return df.format(Math.pow(ratio,10));
        }
        else {
            double accuracy = (0.89976)*Math.pow(ratio,7.7095)+0.111;

            return df.format(accuracy);
        }

       }


}
