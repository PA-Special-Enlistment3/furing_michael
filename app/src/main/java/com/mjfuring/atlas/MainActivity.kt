package com.mjfuring.atlas


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.mapbox.mapboxsdk.Mapbox
import com.mjfuring.atlas.databinding.ActivityMainBinding
import com.mjfuring.atlas.service.ServiceListener
import com.mjfuring.atlas.service.SmsService
import com.mjfuring.base.BaseActivity
import com.mjfuring.base.view.DialogYesNo
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(), ServiceListener {

    private val vmMain: VmMain by viewModel()
    private var myService: SmsService? = null
    private var isBound = false

    override fun layoutRes() = R.layout.activity_main

    override fun onInit() {
        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        startService(Intent(this, SmsService::class.java))
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SmsService::class.java).also { intent ->
            bindService(intent, smsConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(smsConnection)
        isBound = false
    }

    override fun onNewRequest() {
        vmMain.listRequest()
    }

    private val smsConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SmsService.SmsBinder
            myService = binder.getService()
            myService?.setListener(this@MainActivity)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_main)
        val count = navHostFragment?.childFragmentManager?.backStackEntryCount
        if (count == 0){
            DialogYesNo(this, { finish() }).show(getString(R.string.msg_close_app))
            return
        }
        super.onBackPressed()
    }

}

//{cmd:1,nat:"test",ref=4,lat:12.9794,lon:124.0271}