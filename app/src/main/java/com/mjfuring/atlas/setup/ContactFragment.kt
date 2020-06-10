package com.mjfuring.atlas.setup

import androidx.recyclerview.widget.LinearLayoutManager
import com.mjfuring.atlas.R
import com.mjfuring.atlas.databinding.FragmentSetupContactsBinding
import com.mjfuring.atlas.db.model.Contact
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.view.DialogYesNo
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ContactFragment: BaseFragment<FragmentSetupContactsBinding>() {

    private val vmSetup: VmSetup by sharedViewModel()
    private val listAdapter = ContactAdapter()

    override fun layoutRes(): Int = R.layout.fragment_setup_contacts

    override fun onInit() {
        bindings().apply {
            back.setOnClickListener {
                vmSetup.gotoFragment(0)
            }
            btnSave.setOnClickListener {
                val contacts = listAdapter.getSelected()
                if (contacts.size > 0){
                    vmSetup.importContact(contacts)
                } else {
                    showWarnDialog(R.string.msg_select_contact)
                }
            }
            rvContacts.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = listAdapter
            }
            observeEvents()
        }
    }

    private fun observeEvents() {
        vmSetup.apply {
            observeData<ArrayList<Contact>>(contactEvent, {
                listAdapter.apply {
                    clearItems()
                    addItems(it)
                }
            })
            observeNoData(importEvent, {
                vmSetup.gotoFragment(2)
            })
        }
    }



}