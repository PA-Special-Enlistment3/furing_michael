package com.mjfuring.atlas.incident

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.BaseAdapter
import com.mjfuring.atlas.common.BaseViewHolder
import com.mjfuring.atlas.common.toRequestStatus
import com.mjfuring.atlas.common.toRespondentStatus
import com.mjfuring.atlas.db.model.Contact
import com.mjfuring.atlas.db.model.Respondent

class RespondentAdapter: BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return CustomViewHolder(parent)
    }

    inner class CustomViewHolder(parent: ViewGroup) :

        BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_respondent, parent, false)) {

        private val tvName = itemView.findViewById<TextView>(R.id.tv_name)
        private val tvNumber = itemView.findViewById<TextView>(R.id.tv_number)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tv_status)

        override fun bind(any: Any) {
            if (any is Respondent) {
                tvName.text = any.name
                tvNumber.text = any.number
                tvStatus.text = any.status.toRespondentStatus()
            }
        }

    }


}