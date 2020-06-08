package com.mjfuring.atlas.setup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mjfuring.atlas.R
import com.mjfuring.atlas.databinding.FragmentSetupBinding
import com.mjfuring.atlas.databinding.FragmentSplashBinding
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.BaseFragmentPager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SetupFragment: BaseFragment<FragmentSetupBinding>() {

    private val vmSetup: VmSetup by sharedViewModel()
    private lateinit var setupViewPager: SetupViewPager

    override fun layoutRes(): Int = R.layout.fragment_setup

    override fun onInit() {
        setupViewPager = SetupViewPager(childFragmentManager)
        viewBinding?.vpNoSwipe?.apply {
            adapter = setupViewPager
            observeEvents()
        }
    }

    private fun observeEvents() {
        vmSetup.apply {
            observeData<Int>(fragmentEvent, {
                viewBinding?.vpNoSwipe?.currentItem = it
            })
        }
    }

    inner class SetupViewPager(fm: FragmentManager): BaseFragmentPager(fm){
        override fun getItem(pos: Int): Fragment {
            return when(pos){
                1 -> DownloadMapFragment()
                else -> PermissionFragment()
            }
        }
        override fun getCount(): Int {
            return 2
        }
    }


}