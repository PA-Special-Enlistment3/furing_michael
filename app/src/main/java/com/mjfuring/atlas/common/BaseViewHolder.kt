package com.mjfuring.atlas.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(any: Any)
}