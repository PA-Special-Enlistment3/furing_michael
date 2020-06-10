package com.mjfuring.atlas.incident

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjfuring.atlas.R
import com.mjfuring.atlas.VmMain
import com.mjfuring.atlas.common.IncidentStatus.COMPLETED
import com.mjfuring.atlas.common.IncidentStatus.PENDING
import com.mjfuring.atlas.common.toRequestStatus
import com.mjfuring.atlas.databinding.FragmentIncidentDetailBinding
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.LatLong
import com.mjfuring.atlas.db.model.Respondent
import com.mjfuring.atlas.incident.IncidentDetailArgs.fromBundle
import com.mjfuring.base.BaseFragment
import com.mjfuring.base.view.DialogYesNo
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IncidentDetail: BaseFragment<FragmentIncidentDetailBinding>() {

    private var incidentId: Long = 0
    private lateinit var latlong: LatLong
    private var incident = Incident()
    private val listAdapter = RespondentAdapter()

    private val vmMain: VmMain by sharedViewModel()

    override fun layoutRes(): Int = R.layout.fragment_incident_detail

    override fun onInit() {

        incidentId = fromBundle(requireArguments()).id
        latlong = fromBundle(requireArguments()).latlong

        viewBinding?.apply {
            btnRespond.setOnClickListener {
                vmMain.respondIncident(requireContext(), incident, latlong)
            }
            btnMore.setOnClickListener {
                showPopUp(it)
            }
            rvList.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = listAdapter
            }
            observeEvents()
        }
    }

    private fun observeEvents() {
        vmMain.apply {
            observeData<List<Respondent>>(listRespondentEvent, {
                listAdapter.clearItems()
                if (incident.status >= COMPLETED){
                    it.forEach { respondent ->
                        respondent.status = 10
                    }
                }
                listAdapter.addItems(it)
            })
            observeData<Incident>(getEvent, {
                incident = it
                showIncident(it)
            })
            observeData<Incident>(respondEvent, {
                showIncident(it)
                showInfoDialog(R.string.msg_success_respond)
            })
            observeData<Incident>(updateEvent, {
                showIncident(it)
                showInfoDialog(R.string.msg_success_update)
            })
            getIncident(incidentId)
        }
    }

    private fun showIncident(incident: Incident){
        bindings().apply {
            tvNature.text = incident.title
            tvStatus.text = incident.status.toRequestStatus()
            if (incident.ref > 0){
                tvFrom.text = incident.number
                btnRespond.isVisible = incident.status == PENDING
            } else {
                vmMain.listRespondent(incident.id)
                lblFrom.isVisible = false
                tvFrom.isVisible = false
                btnRespond.isVisible = false
            }
        }

    }

    private fun showPopUp(view: View){
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.menu_incident)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_completed->{
                        DialogYesNo(requireContext(), {
                            vmMain.completeIncident(incidentId)
                        }).show(getString(R.string.msg_incident_completed))
                        true
                    }
                    R.id.action_invalid->{
                        DialogYesNo(requireContext(), {
                            vmMain.invalidIncident(incidentId)
                        }).show(getString(R.string.msg_incident_invalid))
                        true
                    }
                    else-> false
                }
            }
            show()
        }
    }


}