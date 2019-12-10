package com.healthcarelab.wearable.mibandapp2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    Boolean isListeningHeartRate = false;

    private InputMethodManager imm;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    ListView list;
    Button btnStartConnecting, chat, setting;
    ToggleButton btnGetHeartRate;
    EditText txtPhysicalAddress, nickname;
    TextView txtState, txtByte;
    private String mDeviceName;
    private String mDeviceAddress;
    ImageView heartb;
    Timer timer;
    String miband_id;
    String miband_name;

    private final String setMiname = "http://localhost/setMiname.php?";
    private final String setMiHR = "http://localhost/setMiHRdata.php?";

    phpInsert phpInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeObjects();
        initilaizeComponents();
        initializeEvents();
        getBoundedDevice();
    }

    void getBoundedDevice() {
        mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        txtPhysicalAddress.setText(mDeviceAddress);

        Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bd : boundedDevice) {
            if (bd.getName().contains("MI Band 2")) {
                txtPhysicalAddress.setText(bd.getAddress());
            }
        }
    }

    void initializeObjects() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    void initilaizeComponents() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        heartb = (ImageView) findViewById(R.id.gif_image1);
        btnStartConnecting = (Button) findViewById(R.id.btnStartConnecting);
        setting = (Button) findViewById(R.id.setting);
        btnGetHeartRate = (ToggleButton) findViewById(R.id.btnGetHeartRate);
        chat = (Button)findViewById(R.id.chat);
        txtPhysicalAddress = (EditText) findViewById(R.id.txtPhysicalAddress);
        txtState = (TextView) findViewById(R.id.txtState);
        txtByte = (TextView) findViewById(R.id.txtByte);
        nickname = (EditText) findViewById(R.id.nickname);
    }

    void initializeEvents() {
        btnStartConnecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnecting();
            }
        });

        btnGetHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(miband_name == null){
                    Toast.makeText(MainActivity.this, "이름을 입력하세요.", Toast.LENGTH_LONG).show();
                    btnGetHeartRate.setChecked(false);
                } else {
                    if (btnGetHeartRate.isChecked()) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                startScanHeartRate();
                            }
                        }, 0, 1000);   //1분
                        btnGetHeartRate.setText("running");
                    } else {
                        txtByte.setText(" ");
                        btnGetHeartRate.setText("stop");
                    }
                }


            }
        });

        // 채팅으로 넘어가기
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ArrayAdapter 객체를 생성한다.
                Intent intent =  new Intent(getApplicationContext(), PopUpChat.class);
                intent.putExtra("message",String.valueOf(miband_name));
                intent.putExtra("address",String.valueOf(miband_id));

                startActivity(intent);
            }
        });


        // 미밴드 아이디, 사용자 이름 설정하기
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(miband_id == null){
                    Toast.makeText(MainActivity.this, "다시 누르세요..", Toast.LENGTH_LONG).show();
                    startConnecting();
                } else {
                    miband_name = nickname.getText().toString();
//                    phpInsert = new phpInsert();
//                    phpInsert.execute(setMiname + "'&id='" + miband_id + "'&name='" + miband_name + "'");
                }
                imm.hideSoftInputFromWindow(nickname.getWindowToken(), 0);

            }
        });
    }

    void startConnecting() {
        String address = txtPhysicalAddress.getText().toString();
        imm.hideSoftInputFromWindow(txtPhysicalAddress.getWindowToken(), 0);
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        // 미밴드 아이디
        miband_id = address;
        Log.v(TAG, "Connecting to " + address);
        Log.v(TAG, "Device name " + bluetoothDevice.getName());
        bluetoothGatt = bluetoothDevice.connectGatt(this, true, bluetoothGattCallback);

    }

    void stateConnected() {
        // 블루투스 신호 연결 되었을 때
        bluetoothGatt.discoverServices();
        txtState.setText("Connected");
    }

    void stateDisconnected() {
        bluetoothGatt.disconnect();
        txtState.setText("Disconnected");
    }

    void startScanHeartRate() {
        if (bluetoothGatt != null) {
            BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                    .getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
            bchar.setValue(new byte[]{21, 1, 1}); // {21, 2, 1} : 1번측정
            bluetoothGatt.writeCharacteristic(bchar);

        } else {
            startConnecting();
            Toast.makeText(this, "Re try.", Toast.LENGTH_SHORT).show();
        }
    }

    void listenHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v(TAG, "onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.v(TAG, "onServicesDiscovered");
            listenHeartRate();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.v(TAG, "onCharacteristicWrite");
        }


        // 심박수 측정
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
            Log.i(TAG, "onCharacteristicRead/setValue: " + Arrays.toString(data));
            txtByte.setText("현재 심박수 : " + (int) data[1]);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();

            BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.Basic.service).getCharacteristic(CustomBluetoothProfile.Basic.realtimeStepCharacteristic);
//            phpInsert = new phpInsert();
            int hr_data = (int) data[1];
//            phpInsert.execute(setMiHR + "id='" + miband_id + "'&hr=" + hr_data);
            txtByte.setText("현재 심박수 : "+ hr_data);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.v(TAG, "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.v(TAG, "onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.v(TAG, "onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.v(TAG, "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.v(TAG, "onMtuChanged");
        }
    };


}
