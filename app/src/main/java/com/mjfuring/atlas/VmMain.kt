package com.mjfuring.atlas

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjfuring.atlas.db.dao.RequestDao
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.base.event.EventLiveData
import com.mjfuring.base.event.EventSuccess
import com.mjfuring.base.event.posting
import com.mjfuring.base.event.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class VmMain(
    private val requestDao: RequestDao
): ViewModel() {

    val listEvent = EventLiveData()
    val listCompletedEvent = EventLiveData()
    val createEvent = EventLiveData()


    fun listRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            listEvent.posting()
            val requests = requestDao.listInComplete()
            listEvent.postValue(EventSuccess(requests))
        }
    }

    fun listCompleted(){
        viewModelScope.launch(Dispatchers.IO) {
            listCompletedEvent.posting()
            listCompletedEvent.postValue(EventSuccess(requestDao.listCompleted()))
        }
    }

    fun createIncident(incident: Incident){
        viewModelScope.launch(Dispatchers.IO) {
            createEvent.posting()
            createEvent.success(requestDao.add(incident))
        }
    }


}