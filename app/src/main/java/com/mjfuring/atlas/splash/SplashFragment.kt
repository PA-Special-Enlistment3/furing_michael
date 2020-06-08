package com.mjfuring.atlas.splash

import androidx.navigation.fragment.findNavController
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.isPermitted
import com.mjfuring.atlas.databinding.FragmentSplashBinding
import com.mjfuring.atlas.setup.VmSetup
import com.mjfuring.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment: BaseFragment<FragmentSplashBinding>() {

    private val vmSetup: VmSetup by sharedViewModel()

    override fun layoutRes(): Int = R.layout.fragment_splash

    override fun onInit() {
        if(isPermitted(false) && vmSetup.isMapDownloaded()){
            goToHome()
        } else {
            goToSetup()
        }
    }

    private fun goToHome(){
        findNavController().navigate(
           SplashFragmentDirections.actionNavSplashToNavHome()
        )
    }

    private fun goToSetup(){
        findNavController().navigate(
            SplashFragmentDirections.actionNavSplashToNavSetup()
        )
    }

}