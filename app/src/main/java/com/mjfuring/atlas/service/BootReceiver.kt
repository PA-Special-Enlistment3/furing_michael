package com.mjfuring.atlas.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import timber.log.Timber


class BootReceiver: BroadcastReceiver() {

    override fun onReceive(c: Context, i: Intent) {
        if (i.action.equals(Intent.ACTION_BOOT_COMPLETED) ||
            i.action.equals(Intent.ACTION_MY_PACKAGE_REPLACED) ||
            i.action.equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
            startService(c)
        }
    }

    private fun startService(c: Context) {
        val i = Intent(c, SmsService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                c.startForegroundService(i)
            } else {
                c.startService(i)
            }
        } catch (e: Exception) {
            Timber.d("Failed to run sms service!")
        }
    }
}