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
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class Bleep {
    static boolean LOG = true;
    private static Bleep self;
    private final Context context;
    private final BleCallbacks callbacks = new BleCallbacks();

    private Bleep(Context context) {
        this.context = context.getApplicationContext();
    }

    static Bleep getSelf() {
        if (self == null) {
            throw new IllegalStateException("Bleep library has not been initialized");
        }
        return self;
    }

    public Bleep initialize(Context context) {
        self = new Bleep(context);
        return self;
    }

    public void enableLogging(boolean enable) {
        LOG = enable;
    }

    BluetoothAdapter getBluetoothAdapter() {
        BluetoothManager btm =
            (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        return btm.getAdapter();
    }

    BleCallbacks getCallbacks() {
        return callbacks;
    }

    Context getContext() {
        return context;
    }
}
