package com.mjfuring.atlas.setup

import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.isPermitted
import com.mjfuring.atlas.databinding.FragmentPermissionBinding
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.view.DialogYesNo
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PermissionFragment: BaseFragment<FragmentPermissionBinding>() {

    private val vmSetup: VmSetup by sharedViewModel()

    override fun layoutRes(): Int = R.layout.fragment_permission

    override fun onInit() {
        bindings().apply {
            back.setOnClickListener {
                DialogYesNo(requireContext(), { requireActivity().finish() }).show(getString(R.string.msg_close_app))
            }
            btnRequest.setOnClickListener {
                if(isPermitted()) {
                    vmSetup.getContacts(requireContext())
                    vmSetup.gotoFragment(1)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(isPermitted(false)){
            vmSetup.gotoFragment(1)
        } else {
            showErrorDialog(R.string.msg_permission_not_granted)
        }
    }


}