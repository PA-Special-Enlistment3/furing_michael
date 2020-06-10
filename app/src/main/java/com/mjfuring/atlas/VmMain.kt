package com.mjfuring.atlas

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjfuring.atlas.common.Commands
import com.mjfuring.atlas.common.ID
import com.mjfuring.atlas.common.INCIDENT_SMS
import com.mjfuring.atlas.common.RESPOND_SMS
import com.mjfuring.atlas.common.RespondentStatus.FAILED
import com.mjfuring.atlas.common.RespondentStatus.SENT
import com.mjfuring.atlas.db.dao.ContactDao
import com.mjfuring.atlas.db.dao.IncidentDao
import com.mjfuring.atlas.db.dao.RespondentDao
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.LatLong
import com.mjfuring.atlas.db.model.Respondent
import com.mjfuring.base.event.EventLiveData
import com.mjfuring.base.event.EventSuccess
import com.mjfuring.base.event.posting
import com.mjfuring.base.event.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class VmMain(
    private val incidentDao: IncidentDao,
    private val contactDao: ContactDao,
    private val respondentDao: RespondentDao
): ViewModel() {

    val listEvent = EventLiveData()
    val listCompletedEvent = EventLiveData()
    val createEvent = EventLiveData()
    val respondEvent = EventLiveData()
    val getEvent = EventLiveData()
    val updateEvent = EventLiveData()
    val listRespondentEvent = EventLiveData()


    fun listRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            listEvent.posting()
            val requests = incidentDao.listInComplete()
            listEvent.postValue(EventSuccess(requests))
        }
    }

    fun listCompleted(){
        viewModelScope.launch(Dispatchers.IO) {
            listCompletedEvent.posting()
            listCompletedEvent.postValue(EventSuccess(incidentDao.listCompleted()))
        }
    }

    fun listRespondent(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            listRespondentEvent.posting()
            listRespondentEvent.success(respondentDao.listByRequest(id))
        }
    }

    fun getIncident(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            getEvent.posting()
            val res = incidentDao.getById(id)
            getEvent.postValue(EventSuccess(res))
        }
    }

    fun createIncident(context: Context, incident: Incident){
        viewModelScope.launch(Dispatchers.IO) {
            createEvent.posting()

            val id = incidentDao.add(incident)
            val contacts = contactDao.all()

            contacts.forEach {

                /*val respondentId = respondentDao.add(Respondent(
                    ref = id,
                    name = it.name,
                    number = it.number,
                    status = FAILED
                ))
*/
                try {
                    val sentIntent = PendingIntent.getBroadcast(context, 0,
                        Intent(INCIDENT_SMS), 0)
                    SmsManager.getDefault().sendTextMessage(it.number, null,
                      "{cmd:${Commands.REQUEST},nat:\"${incident.title}\",ref:${id},lat:${incident.latitude},lon:${incident.longitude}}"
                        , sentIntent, null)
                    //respondentDao.updateStatus(respondentId, SENT)
                } catch (e: Exception) {
                    Timber.e(e)
                }

            }

            createEvent.success(id)
        }
    }


    fun respondIncident(context: Context, incident: Incident, latLong: LatLong){
        viewModelScope.launch(Dispatchers.IO) {
            respondEvent.posting()

            incidentDao.setResponded(incident.id)

            try {
                val sentIntent = PendingIntent.getBroadcast(context, 0, Intent(RESPOND_SMS), 0)
                SmsManager.getDefault().sendTextMessage(incident.number, null,
                    "{cmd:${Commands.RESPOND},ref:${incident.ref},lat:${latLong.lat},lon:${latLong.lon}}"
                    , sentIntent, null)
            } catch (e: Exception) {
                Timber.e(e)
            }

            respondEvent.success(incidentDao.getById(incident.id))
        }

    }

    fun invalidIncident(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            updateEvent.posting()
            updateEvent.success(incidentDao.setInvalid(id))
        }
    }

    fun completeIncident(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            updateEvent.posting()
            updateEvent.success(incidentDao.setCompleted(id))
        }
    }
}