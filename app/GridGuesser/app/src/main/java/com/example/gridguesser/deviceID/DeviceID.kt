package com.example.gridguesser.deviceID

import android.content.ContentResolver
import android.provider.Settings

class DeviceID {
    companion object {
        fun getDeviceID(contentResolver: ContentResolver): String{
            return Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

}