package com.example.homeprotect_client.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.homeprotect_client.R

internal class NotificationAdapter(private var itemsList: List<String>):


    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    private var listener: OnClickListener? = null
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.notification_itemTextView)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.warning_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.itemTextView.text = item
        holder.itemView.setOnClickListener {
            listener?.onItemClick(item)
        }
    }
    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }
    interface OnClickListener {
        fun onItemClick(item: String)
    }

    // onClickListener Interface

}