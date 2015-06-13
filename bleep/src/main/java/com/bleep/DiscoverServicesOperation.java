/*
 * Copyright 2015 Tamir Shomer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bleep;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import java.util.List;

class DiscoverServicesOperation extends BleOperation<List<BluetoothGattService>> {
    private final BluetoothGatt gatt;

    protected DiscoverServicesOperation(BluetoothGatt gatt, BleCallbacks callbacks) {
        super(callbacks);
        this.gatt = gatt;
    }

    @Override
    void preformOperation() {
        gatt.discoverServices();
    }

    @Override
    String getOperationName() {
        return "Discover Services";
    }

    @Override
    String getDeviceAddress() {
        return gatt.getDevice().getAddress();
    }

    @Override
    public boolean onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (this.gatt.getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setResponse(gatt.getServices());
            } else {
                setException(new BleException(status,
                    String.format("Discover Services failed with status %s", status)));
            }

            return true;
        }

        return super.onServicesDiscovered(gatt, status);
    }
}
