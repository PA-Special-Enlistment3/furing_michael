package com.mjfuring.atlas.common

import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    val items = ArrayList<Any>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(baseViewHolder: BaseViewHolder, i: Int) {
        baseViewHolder.bind(items[i])
    }

    fun getItems(): List<Any> {
        return items
    }

    fun addItems(_items: List<Any>) {
        items.addAll(_items)
        notifyDataSetChanged()
    }

    fun addAny(any: Any) {
        if (any is ArrayList<*>){
            items.addAll(any)
            notifyDataSetChanged()
        }
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addItem(_items: Any) {
        items.add(_items)
        notifyItemInserted(items.size - 1)
    }

    fun getItem(pos: Int): Any {
        return items[pos]
    }

    fun removeItem(pos: Int) {
        items.removeAt(pos)
        notifyItemInserted(pos)
    }

    fun removeLast() {
        if (items.size > 0) {
            items.removeAt(items.size - 1)
            notifyItemRemoved(items.size - 1)
        }
    }




}