package com.mjfuring.atlas.history

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjfuring.atlas.R
import com.mjfuring.atlas.VmMain
import com.mjfuring.atlas.databinding.FragmentHistoryBinding
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.LatLong
import com.mjfuring.atlas.db.model.Respondent
import com.mjfuring.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FragmentHistory : BaseFragment<FragmentHistoryBinding>() {

    private val vmMain: VmMain by sharedViewModel()
    private val listAdapter = AdapterHistory{ showDetail(it) }

    override fun layoutRes(): Int = R.layout.fragment_history

    override fun onInit() {
        viewBinding?.apply {
            rvList.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                adapter = listAdapter
            }
            observeEvents()
        }

    }

    private fun observeEvents() {
        vmMain.apply {
            observeData<List<Respondent>>(listCompletedEvent, {
                listAdapter.clearItems()
                listAdapter.addItems(it)
            })
            listCompleted()
        }
    }

    private fun showDetail(any: Any) {
        if (any is Incident){
            findNavController().navigate(
                FragmentHistoryDirections.actionNavHistoryToNavIncidentDetail(any.id, LatLong())
            )
        }
    }


}
