package com.mjfuring.atlas


import com.mapbox.mapboxsdk.Mapbox
import com.mjfuring.atlas.databinding.ActivityMainBinding
import com.mjfuring.base.BaseActivity
import com.mjfuring.base.view.DialogYesNo

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun layoutRes() = R.layout.activity_main

    override fun onInit() {
        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
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