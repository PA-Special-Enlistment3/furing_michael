package com.mjfuring.atlas.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.mjfuring.atlas.MainActivity


class SmsReceiver: BroadcastReceiver() {

    private var smsListener: SmsListener? = null

    override fun onReceive(c: Context, i: Intent) {

        var smsSender = ""
        var smsBody = ""
        var smsTime: Long = 0

        for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(i)) {
            smsSender = smsMessage.displayOriginatingAddress
            smsBody += smsMessage.messageBody
            smsTime = smsMessage.timestampMillis
        }

        smsSender = smsSender.replace("+63", "0")

        val launchActivity = smsListener?.onReceivedSms(smsSender, smsBody, smsTime)
        launchActivity?.apply {
            if(this){
                c.startActivity(
                    Intent().apply {
                        setClass(c, MainActivity::class.java)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
        }
    }

    fun setListener(smsListener: SmsListener?){
        this.smsListener = smsListener
    }

}