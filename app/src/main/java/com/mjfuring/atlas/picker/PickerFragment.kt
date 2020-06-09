package com.mjfuring.atlas.picker

import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.isPermitted
import com.mjfuring.atlas.databinding.FragmentPermissionBinding
import com.mjfuring.atlas.setup.VmSetup
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.view.DialogYesNo
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PickerFragment: BaseFragment<FragmentPermissionBinding>() {

    private val vmSetup: VmSetup by sharedViewModel()

    override fun layoutRes(): Int = R.layout.fragment_permission

    override fun onInit() {

    }



}