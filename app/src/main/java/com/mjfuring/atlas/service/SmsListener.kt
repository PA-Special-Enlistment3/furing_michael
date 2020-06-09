package com.mjfuring.atlas.service

interface SmsListener {
    fun onReceivedSms(sender: String, message: String, time: Long): Boolean
}