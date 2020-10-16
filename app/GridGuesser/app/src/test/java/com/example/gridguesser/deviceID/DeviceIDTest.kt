package com.example.gridguesser.deviceID

import android.content.ContentResolver
import android.provider.Settings
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class DeviceIDTest {
    private lateinit var contentResolver : ContentResolver

    @Before
    fun setUp() {
        //contentResolver = mock(ContentResolver)

    }

    @Test
    fun testDeviceID() {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
}

