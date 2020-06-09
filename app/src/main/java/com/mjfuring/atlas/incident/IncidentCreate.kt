package com.mjfuring.atlas.incident

import com.mjfuring.atlas.R
import com.mjfuring.atlas.VmMain
import com.mjfuring.atlas.databinding.FragmentIncidentCreateBinding
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.utils.trimString
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IncidentCreate: BaseFragment<FragmentIncidentCreateBinding>() {

    private val vmMain: VmMain by sharedViewModel()

    override fun layoutRes(): Int = R.layout.fragment_incident_create

    override fun onInit() {
        viewBinding?.apply {
            btnCreate.setOnClickListener {
                create(tvTitle.trimString())
            }
            observeEvents()
        }
    }

    private fun observeEvents() {
        vmMain.apply {
            observeNoData(createEvent, {

            })
        }
    }

    private fun create(title: String){

        val incident = Incident(
            title = title
        )


        /*vmMain.createIncident(

        )*/

    }



}