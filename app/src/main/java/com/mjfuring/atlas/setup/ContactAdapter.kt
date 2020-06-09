package com.mjfuring.atlas.setup

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.BaseAdapter
import com.mjfuring.atlas.common.BaseViewHolder
import com.mjfuring.atlas.db.model.Contact

class ContactAdapter: BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return CustomViewHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    setSelected(adapterPosition)
                }
            }
        }
    }

    inner class CustomViewHolder(parent: ViewGroup) :

        BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)) {

        private val tvName = itemView.findViewById<TextView>(R.id.tv_name)
        private val tvNumber = itemView.findViewById<TextView>(R.id.tv_number)
        private val cbSelected = itemView.findViewById<CheckBox>(R.id.cb_selected)

        override fun bind(any: Any) {
            if (any is Contact) {
                tvName.text = any.name
                tvNumber.text = any.number
            }
        }

        fun setSelected(pos: Int){
            val isCheck = cbSelected.isChecked
            cbSelected.isChecked = !isCheck
            items[pos].apply {
                if (this is Contact) {
                    selected = !isCheck
                }
            }
        }

    }

    fun getSelected(): ArrayList<Contact> {
        val contacts: ArrayList<Contact> = arrayListOf()
        items.forEach {
            if (it is Contact) {
                if (it.selected) contacts.add(it)
            }
        }
        return contacts
    }



}