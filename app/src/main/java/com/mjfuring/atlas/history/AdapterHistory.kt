package com.mjfuring.atlas.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.BaseAdapter
import com.mjfuring.atlas.common.BaseViewHolder
import com.mjfuring.atlas.common.IncidentStatus.COMPLETED
import com.mjfuring.atlas.common.toStringDate
import com.mjfuring.atlas.db.model.Incident


class AdapterHistory(
    private val onClickCallback: ((item: Any) -> Unit)? = null
): BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return CustomViewHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickCallback?.invoke(getItem(adapterPosition))
                }
            }
        }
    }

    inner class CustomViewHolder(parent: ViewGroup) :

        BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)) {

        private val tvName = itemView.findViewById<TextView>(R.id.tv_name)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tv_status)

        override fun bind(any: Any) {
            if (any is Incident) {
                tvName.text = any.title
                tvStatus.text = "Completed: ${any.dateCompleted.toStringDate()}"
            }
        }


    }

}