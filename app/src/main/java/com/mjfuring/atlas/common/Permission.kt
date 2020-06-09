package com.mjfuring.atlas.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val REQUEST_CODE_PERMISSION = 1001


fun canReceivedSms(context: Context): Boolean {
    return ContextCompat
        .checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
}

fun canReadSms(context: Context): Boolean {
    return ContextCompat
        .checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
}

fun canSendSms(context: Context): Boolean {
    return ContextCompat
        .checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
}

fun canGetLocation(context: Context): Boolean {
    return ContextCompat
        .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

fun canReadContact(context: Context): Boolean {
    return ContextCompat
        .checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
}

fun canWriteContact(context: Context): Boolean {
    return ContextCompat
        .checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.isPermitted(ask: Boolean = true): Boolean {

    val permissionList = arrayListOf<String>()
    val canReceived = canReceivedSms(requireContext())
    val canRead = canReadSms(requireContext())
    val canSend = canSendSms(requireContext())
    val canLocation = canGetLocation(requireContext())
    val canReadContact = canReadContact(requireContext())
    val canWriteContact = canWriteContact(requireContext())

    if (!canReceived) {
        permissionList.add(Manifest.permission.RECEIVE_SMS)
    }
    if (!canRead) {
        permissionList.add(Manifest.permission.READ_SMS)
    }
    if (!canSend) {
        permissionList.add(Manifest.permission.SEND_SMS)
    }
    if (!canLocation) {
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (!canReadContact) {
        permissionList.add(Manifest.permission.READ_CONTACTS)
    }

    return if (permissionList.size > 0) {
        if (ask){
            requestPermissions(permissionList.toTypedArray(),
                REQUEST_CODE_PERMISSION
            )
        }
        false
    } else {
        true
    }
}
