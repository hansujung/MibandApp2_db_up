package com.healthcarelab.wearable.mibandapp2;

import java.util.UUID;

public class CustomBluetoothProfile {

    public static class Basic {
        public static UUID service = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
        public static UUID batteryCharacteristic = UUID.fromString("00000006-0000-3512-2118-0009af100700");
        public static UUID realtimeStepCharacteristic = UUID.fromString("00000007-0000-3512-2118-0009af100700");
        public static UUID StepDescriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }

    public static class AlertNotification {
        public static UUID service = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
        public static UUID alertCharacteristic = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    }

    public static class HeartRate {
        public static UUID service = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        public static UUID measurementCharacteristic = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
        public static UUID descriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        public static UUID controlCharacteristic = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
        // 0000180d-0000-1000-8000-00805f9b34fb
        // 00002a37-0000-1000-8000-00805f9b34fb
        // 00002a39-0000-1000-8000-00805f9b34fb
    }

}
