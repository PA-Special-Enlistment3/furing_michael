package com.mjfuring.atlas.incident

import androidx.navigation.fragment.findNavController
import com.mjfuring.atlas.R
import com.mjfuring.atlas.VmMain
import com.mjfuring.atlas.common.IncidentStatus
import com.mjfuring.atlas.databinding.FragmentIncidentCreateBinding
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.LatLong
import com.mjfuring.atlas.incident.IncidentCreateArgs.fromBundle
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.utils.trimString
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IncidentCreate: BaseFragment<FragmentIncidentCreateBinding>() {

    private lateinit var latlong: LatLong

    private val vmMain: VmMain by sharedViewModel()

    override fun layoutRes(): Int = R.layout.fragment_incident_create

    override fun onInit() {

        latlong = fromBundle(requireArguments()).latlong

        viewBinding?.apply {
            btnCreate.setOnClickListener {
                create(title.trimString())
            }
            observeEvents()
        }
    }

    private fun observeEvents() {
        vmMain.apply {
            observeNoData(createEvent, {
                findNavController().popBackStack()
            })
        }
    }

    private fun create(title: String){

        val incident = Incident(
            title = title,
            latitude = latlong.lat,
            longitude = latlong.lon,
            dateCreated = System.currentTimeMillis(),
            status = IncidentStatus.PENDING
        )

        vmMain.createIncident(requireContext(), incident)

    }



}