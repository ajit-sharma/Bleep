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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import bolts.Continuation;
import bolts.Task;

public abstract class BleScanner {
    private static final int DEFAULT_SCAN_DURATION = 2000;
    private final BluetoothAdapter adapter;
    private int scanDuration = DEFAULT_SCAN_DURATION;
    private static final Semaphore lock = new Semaphore(1);
    private final List<BleScanResult> results = new ArrayList<>();

    protected BleScanner(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    protected abstract void startScan(BluetoothAdapter adapter);
    protected abstract void stopScan(BluetoothAdapter adapter);

    protected void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        results.add(new BleScanResult(device, rssi, scanRecord));
    }

    static BleScanner create(BluetoothAdapter adapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new LollipopBleScanner(adapter);
        }

        return new LegacyBleScanner(adapter);
    }

    public BleScanner setDuration(int milliseconds) {
        scanDuration = milliseconds;
        return this;
    }

    public Task<List<BleScanResult>> scan() {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                lock.acquire();
                startScan(adapter);
                return null;
            }
        }).onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return Task.delay(scanDuration);
            }
        }).continueWith(new Continuation<Void, List<BleScanResult>>() {
            @Override
            public List<BleScanResult> then(Task<Void> task) throws Exception {
                stopScan(adapter);
                List<BleScanResult> localResults = new ArrayList<>(results);
                results.clear();
                lock.release();
                return localResults;
            }
        });
    }
}