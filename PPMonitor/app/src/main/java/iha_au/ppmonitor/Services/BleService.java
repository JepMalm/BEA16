/*
 * Copyright (C) 2013 The Android Open Source Project
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

package iha_au.ppmonitor.Services;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

import iha_au.ppmonitor.Models.ModelInterfaces.ISensor;
import iha_au.ppmonitor.Models.ModelInterfaces.ISensorData;
import iha_au.ppmonitor.Models.PressureSensor;
import iha_au.ppmonitor.Models.PressureSensorData;


/**
 * Service til at manage forbindelse og data kommunikation med en GATT servier hosted på et givent BLE apparat.
 */
public class BleService extends Service implements IDataService {
    public final static String SCAN_RESULT = "Device";
    public final static String VALUE_RESULT = "Value";
    public final static String BLE_ENABLED = "enable";
    private BluetoothGatt bGatt;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    boolean hasBle;
    private BluetoothLeScanner bluetoothLeScanner;
    private final static String SERVICE_UUID_CHECK = "0000110a-0000-1000-8000-00805f9b34fb";
    private final static String SERVICE_UUID_TEST = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private final static String DEVICE_ADRESS = "74:DA:EA:B2:1B:1A";
    Method getUuidsMethod;
    public boolean isConnected;
    BluetoothGattCharacteristic characteristic;

    ISensorData sensors;
    private final IBinder binder = new BleBinder();
    private boolean isRecording;



    /**
     * Når BleService bliver startet kaldes onCreate. Det er her alle attributter bliver instantieret.
     */
    @Override
    public void onCreate() {
        // BluetoothManager bruges til at manage og tilgå BlueToothAdapter.
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        // BlueToothAdapter repræsentere enhedens lokale bluetoothadapter. Det er denne klasse, som bruges til at kalde Bluetooth tasks.
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Gennem denne klasse kan der scannes for Low Energy Bluetooth apparater.
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        hasBle = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        //Metode til afkodning af UUIDS, da disse en collection af bytes. afkodes de til string, for nemmere sammenligning.
        try {
            getUuidsMethod = mBluetoothAdapter.getClass().getDeclaredMethod("getUuids",null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Log.v("DEBUG","bleservce is started");
        super.onCreate();
    }

    /**
     * onStartCommand kaldes efter onCreate igen af android selv.
     * I denne metode checkes der for at bluetooth er slået til på enhenden.
     * samt oprettes de sensor objekter som skal bruges til at opvare de værdier, der modtages fra HW.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createSensorModels();
        if(checkBlueToothEnabled()){
            // Hvis Bluetooth er slået til startes scanning for Low Energi apparater
            startScanForDevice();
        }
        return START_STICKY;
    }

    /**
     * opretter 5 pressureSensor objekter, et til hver sensor i HW.
     */
    private void createSensorModels() {
        sensors = new PressureSensorData();
        for (int i = 0; i < 5; i++) {
            ISensor s = new PressureSensor("Sensor"+i,new ArrayList<Integer>());
            sensors.addSensor(s);
        }
    }

    /**
     * Opretter forbindelse til HW-bluetooth modulets GATT server.
     * @param device Det fundne device fra LeScanCallBack.
     */
    private void connecToGatt(BluetoothDevice device) {
        Log.v("DEBUG","Get device name: " + device.getName());
        bGatt = device.connectGatt(getApplicationContext(),false,gattCallBack);
    }

    /**
     * BluetoothGattCallBack objektet er det som håndtere hvilken reaktion skal ske når der bliver registreret et
     *  BLE apparat.
     */
    private BluetoothGattCallback gattCallBack = new BluetoothGattCallback() {
        /**
         * Når forbindelsen til det fundne apparat er etableret.
         * hvis forbindelsen er "Connected" kaldes discoverServices().
         * samtidig kaldes broadCastConnection(...), for at fortælle BleConnectionView at der er oprettet forbindelse.
         * @param gatt det givne apparats GATT server
         * @param status ?
         * @param newState forbindelses status: forbundet eller ikke forbundet
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            // hvis state er 1 ( er forbundet).
            if(newState == BluetoothProfile.STATE_CONNECTED){
                isConnected = true;
                    try {
                        stopScanForDevice();
                        broadCastConnection(newState);
                        gatt.discoverServices();
                    } catch (Exception e) {
                        Log.v("DEBUG", e.toString());
                    }
            }
            super.onConnectionStateChange(gatt, status, newState);
        }

        /**
         * Når der er oprettet forbindelse og der fundet services på den givne GATT server, håndteres hvilken reaktion der skal ske her.
         * Der oprettes forbindelse til en specific service, som HW-modulet indeholer.
         * Herefter hentes de characteristics fra den givne service, og notification sættes til på de givne characteristics.
         * @param gatt givne apparats GATT server
         * @param status?
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == gatt.GATT_SUCCESS) {
                Log.v("DEBUG", "I Connected to GATT server with UUID:" + SERVICE_UUID_CHECK);

                BluetoothGattService service = bGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                Log.v("DEBUG", "cstics::" + characteristic.getUuid().toString());
                Log.v("DEBUG", "Icstics values" + characteristic.getValue().length);
                super.onServicesDiscovered(gatt, status);
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        }

        /**
         * Hver gang characteristics værdi(er) ændres kaldes onCharacteristicsChanged.
         * Det er her værdier fra HW modtages og sendes videre via broadCastValue(..).
         * @param gatt Det givne apparats GATT server
         * @param characteristic De Characteristics som er notifikation er slået til på.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            try {
                long time = System.nanoTime();
                String stringValue = characteristic.getStringValue(0);
                Log.v("DEBUG", stringValue + " Time: " + time);
                broadCastValue(stringValue);
            }catch (Exception e){
               // Log.v("DEBUG", e.toString());
            }
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    /**
     * Når Servicen "slukkes" kaldes onDestroy.
     * Her disconnectes der fra det tilsluttede aparat.
     */
    @Override
    public void onDestroy() {
        Log.v("DEBUG","onDestroy BleService");
        disconnectFromDevice();
        super.onDestroy();
    }

    /**
     * Startet en Intent med filtret SCAN_RESULT.
     * og broadcaster til systemet.
     * med int svarende til connection state.
     * @param connected
     */
    public void broadCastConnection(int connected){
        Intent bI = new Intent(SCAN_RESULT);
        bI.putExtra("connected",connected);
        LocalBroadcastManager.getInstance(this).sendBroadcast(bI);
    }

    /**
     * Broadcaster om bluetooth er slået til på enheden.
     * dette sker ved en intent med filtret BLE_ENABLED og en string msg.
     * @param msg
     */
    public void broadCastBleEnabled(String msg){
        Intent broadcastIntent = new Intent(BLE_ENABLED);
        broadcastIntent.putExtra("message",msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    /**
     * broadcaster modtaget string fra onCharacteristicChanged(...), som et PressureSensorData objekt.
     * @param value
     */
    public void broadCastValue(String value){
        try {
            // StringTokenizer afkoder en string til "tokenz" ( mindre del elementer). Den sepere via en dilimeter ":".
            // Hver token udgør en værdi til en sensor.
            // og de skal derfor sættes ind i deres, matchende sensor. token 1's værdi, gemmes i sensor 1.
            StringTokenizer tokens = new StringTokenizer(value, ":");
            int count = tokens.countTokens();
            for (int i = 0; i < count; i++) {
                String s = tokens.nextToken();
                int v = Integer.parseInt(s);
                sensors.getSensor(i).addValue(v);
            }
            // Intent med filter VALUE_RESULT. indeholdende PressureSensorData objekt, med de sensorer som er blevet opdateret.
            Intent bI = new Intent(VALUE_RESULT);
            bI.putExtra("sensors", (Serializable) sensors);
            LocalBroadcastManager.getInstance(this).sendBroadcast(bI);
        }catch (Exception e){
            Log.v("DEBUG","error in broadcast value " +e.toString());
        }
    }

    /**
     * kaldes af framework når en activity eller klasse binder sig til service.
     * @param intent bruges ikke.
     * @return IBinder objekt af BleBinder.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * checker om Bluetooth adapteren er sat til --> hvilket betyder at bluetooth er slået til på enheden.
     * @return true hvis adapter er enabled.
     */
    @Override
    public boolean checkBlueToothEnabled() {
        if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enable :)
            broadCastBleEnabled("Bluetooth is not enabled");
            return false;
        }
        return true;
    }

    /**
     * starter scanning efter BLE apparater inden for given radius af enheden.
     * grundet forskellige fremgangsmåder ved forskellige SDK versioner, checkes hvilken version
     * den givne enhed kører, og der handles der efter.
     */
    @Override
    public void startScanForDevice() {
        if (hasBle) {
            // hvis SDK versionen er mindre end 21, alt kører et styresystem ældre end Lollipop
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        // vil ikke blive implementeret da, det ikke er relevant for denne prototype.
                        // Dette viser kun at gruppen er opmærksom på version kontrol.
                    }
                });
                Log.v("DEBUG","starting le scan");
            }
            // hvis SDK versionen er 21 eller der over.
            else {
                try {
                    // scanning startes igennem LeScanner, som kun søger efter BLE enheder.
                    bluetoothLeScanner.startScan(sCallBack);
                    Log.v("DEBUG","starting scan");
                }catch (Exception e){
                    Log.v("DEBUG","ble service destroy");
                    this.onDestroy();
                }
            }
        }
    }

    /**
     * Stopper scanning for BLE enheder.
     * For at sikre fuld stop, stoppes både LeScanner og Adapter.
     */
    @Override
    public void stopScanForDevice() {
        try {
            bluetoothLeScanner.stopScan(sCallBack);
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(sCallBack);
            Log.v("DEBUG", "stopping LEscan");
        }catch (Exception e){
            Log.v("DEBUG", e.toString());
        }
    }

    /**
     * De characteristics fundet i onServiceDiscovered(...) sættes til at notificere BleService
     * hvergang dets værdier ændres. Disse vil blive modtaget i onCharacteristicsChanged(...).
     */
    @Override
    public void startDataRecording() {
        isRecording = true;
        try {
            // Hvis GATT serverens service indeholder characteristics.
            if (characteristic != null) {
                // sætter GATT serveren til at notificere BleService, når GATT serverens characteristic's værdi ændres.
                bGatt.setCharacteristicNotification(characteristic, true);
                Log.v("DEBUG", "setting notifikation to true");
            }else{
                Log.v("DEBUG","No characteristics!!!");
            }
        }catch(Exception e){
            Log.v("DEBUG", e.toString() + "no characteristics found");

        }
    }


    /**
     * Sætter GATT server til ikke at notificere BleService, hver Characteristics værdi ændres.
     */
    @Override
    public void stopDataRecording() {
        isRecording = false;
        Log.v("DEBUG", "isRecording = " + isRecording);
        bGatt.setCharacteristicNotification(characteristic,false);
        Log.v("DEBUG","setting notifikation to false");
    }

    /**
     * hvis GATT serveren ikke er null, altså der er oprettet forbindelse til en GATT server.
     * lukkes denne og forbindelsen afbrydes.
     * samtidig slettes alle data i det oprettede pressureSensorData objekt.
     */
    @Override
    public void disconnectFromDevice() {
        if(bGatt!=null) {
            // lukker gatt server
            bGatt.close();
            // disconnecter fra device
            bGatt.disconnect();
            // sletter data i sensor modeller.
            sensors.getPressureSensors().clear();
            Log.v("DEBUG", "closing connection and dissconnecting and clearing");
        }
    }

    /**
     * binder klasse til BleService. bruges når Activities bindes til BleService.
     * den indeholder en metode som returnere objekt af BleService.
     */
    public class BleBinder extends Binder {
        public BleService getBleService() {
            return BleService.this;
        }
    }

    /**
     * sCallBack bruges når der scannes for BLE enehder i startScan().
     * ScanCallBack er en android framework abstrakt klasse, der indeholder metoder til behandling af fundne devices.
     *
     */
    private ScanCallback sCallBack = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            Log.v("DEBUG","ERROR CODE: "+ errorCode);
            super.onScanFailed(errorCode);
        }

        /**
         * Når et apparat registreres inden for enheden rækkevidde, kaldes onScanResult.
         * @param callbackType ?
         * @param result Indeholder det funde apparat.
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                Log.v("DEBUG", "FOUND DEVICE NAMED: "+ result.getDevice().getName());
                // hvis det fundne apparats adresse matcher DEVICE_ANDRESS
                if (result.getDevice().getAddress().equals(DEVICE_ADRESS)) {
                    // hvis det fundne apparats Service's UUID matcher SERVICE_UUID_TEST
                    if (result.getScanRecord().getServiceUuids().contains(ParcelUuid.fromString(SERVICE_UUID_TEST))) {
                        Log.v("DEBUG", "device: "+ result.getDevice().getName() + "is Connected");
                        stopScanForDevice();
                        connecToGatt(result.getDevice());
                    }
                }
            } catch (Exception e) {
                e.toString();
            }
            super.onScanResult(callbackType, result);
        }
    };
}
