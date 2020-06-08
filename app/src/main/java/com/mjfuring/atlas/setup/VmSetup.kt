package com.mjfuring.atlas.setup

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.mjfuring.atlas.common.PREF_MAP_DOWNLOAD
import com.mjfuring.atlas.common.PREF_NAME
import com.mjfuring.base.event.EventLiveData
import com.mjfuring.base.event.success

class VmSetup(
    private val pref: SharedPreferences
): ViewModel() {

    val fragmentEvent = EventLiveData()

    fun gotoFragment(pos: Int){
        fragmentEvent.success(pos)
    }

    fun isMapDownloaded(): Boolean{
        return pref.getBoolean(PREF_MAP_DOWNLOAD, false)
    }

    fun mapDownloaded(){
        pref.edit().apply{
            putBoolean(PREF_MAP_DOWNLOAD, true)
            apply()
        }
    }


}