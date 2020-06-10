package com.mjfuring.atlas.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.mapbox.android.core.MapboxSdkInfoForUserAgentGenerator
import com.mjfuring.atlas.MainActivity
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.Commands.REQUEST
import com.mjfuring.atlas.common.Commands.RESPOND
import com.mjfuring.atlas.common.IncidentStatus.DUPLICATE
import com.mjfuring.atlas.common.IncidentStatus.PENDING
import com.mjfuring.atlas.common.RespondentStatus
import com.mjfuring.atlas.db.dao.ContactDao
import com.mjfuring.atlas.db.dao.IncidentDao
import com.mjfuring.atlas.db.dao.RespondentDao
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.JsonSms
import com.mjfuring.atlas.db.model.Respondent
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber


class SmsService : Service(), SmsListener {

    private val tag = "Atlas Service:"
    private val incidentDao: IncidentDao by inject()
    private val respondentDao: RespondentDao by inject()
    private val contactDao: ContactDao by inject()

    private var serviceListener: ServiceListener? = null
    private var smsReceiver = SmsReceiver()
    private val binder = SmsBinder()
    private var isBound = false

    override fun onCreate() {
        super.onCreate()

        registerReceiver(
            smsReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )
        smsReceiver.setListener(this)
        Timber.d("$tag Sms service created")
    }

    override fun onDestroy() {
        super.onDestroy()
        smsReceiver.setListener(null)
        unregisterReceiver(smsReceiver)
        Timber.d("$tag Sms service destroyed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("$tag Sms service Started")
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        isBound = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        return super.onUnbind(intent)
    }

    override fun onReceivedSms(sender: String, message: String, time: Long): Boolean {
        Timber.d("$tag Sms Received")
        var isValid = false
        try {
            val json = Gson().fromJson(message, JsonSms::class.java)
            json.apply {
                when (json.cmd) {
                    REQUEST -> {
                        val incident = Incident(
                            ref = ref,
                            title = nat,
                            number = sender,
                            latitude = lat,
                            longitude = lon,
                            dateCreated = time
                        )
                        isValid = parseRequest(incident)
                    }
                    RESPOND -> {
                        val respondent = Respondent(
                            ref = ref,
                            number = sender,
                            latitude = lat,
                            longitude = lon,
                            dateReceived = time
                        )
                        respondent.number.replace("+63", "0")
                        isValid = parseResponse(respondent)
                    }
                    else -> {
                        Timber.d("$tag Invalid request")
                    }

                }
            }

        } catch (e: Exception) {
            Timber.e(e)
        }

        return if (isValid) {
            if (isBound) {
                serviceListener?.onNewRequest()
                false
            } else {
                true
            }
        } else {
            false
        }

    }

    fun setListener(serviceListener: ServiceListener) {
        this.serviceListener = serviceListener
    }

    inner class SmsBinder : Binder() {
        fun getService(): SmsService {
            return this@SmsService
        }
    }


    private fun parseRequest(incident: Incident): Boolean {
        return runBlocking {
            withContext(Dispatchers.Default) {
                val existing = incidentDao.getByRef(incident.ref)
                if (existing == null){
                    incident.status = PENDING
                    incidentDao.add(incident)
                    true
                } else {
                    incident.status = DUPLICATE
                    incidentDao.add(incident)
                    false
                }

            }
        }
    }

    private fun parseResponse(respondent: Respondent): Boolean {
        return runBlocking {
            withContext(Dispatchers.Default) {

                val contact = contactDao.getByNumber(respondent.number)
                if (contact != null){
                    respondent.name = contact.name
                } else {
                    respondent.name = "Unknown"
                }

                respondent.status = RespondentStatus.RESPONDING
                incidentDao.setResponded(respondent.ref)
                respondentDao.add(respondent)

                showNotification(respondent)


               /* val id = respondentDao.getByNumber(respondent.ref, respondent.number)
                if (id > 0){
                    respondent.id = id
                    respondentDao.updateStatus(id, RespondentStatus.RESPONDING)
                    showNotification(respondent)
                }*/
                false
            }
        }
    }


    private fun showNotification(respondent: Respondent){

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
            0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.ic_arrow_back_wide)
            .setContentTitle(respondent.name)
            .setContentText("${respondent.number} is responding to incident")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(respondent.id.toInt(), builder.build())
        }

    }
}




