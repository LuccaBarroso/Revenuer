package com.example.revenuer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.entity.Operation
import com.example.revenuer.listener.OperationListener

class HistoryAdapter(val list: List<Operation>): RecyclerView.Adapter<HistoryAdapter.OperationViewHolder>() {
    private var listener: OperationListener? = null

    fun setOnOperationListener(listener: OperationListener){
        this.listener = listener;
    }

    class OperationViewHolder(view: View, private val listener: OperationListener?):  RecyclerView.ViewHolder(view){
        val nameView: TextView
        val valueView: TextView
        val dateView: TextView
        val imageView: ImageView
        init {
            view.setOnClickListener{
                listener?.onListItemClick(view, adapterPosition)
            }
            nameView = view.findViewById(com.example.revenuer.R.id.item_cardview_name)
            valueView = view.findViewById(com.example.revenuer.R.id.item_cardview_value)
            dateView = view.findViewById(com.example.revenuer.R.id.item_cardview_date)
            imageView = view.findViewById(com.example.revenuer.R.id.operation_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(com.example.revenuer.R.layout.operation_item, parent,false)
        return OperationViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        val operation = list[position]
        holder.nameView.text = operation.name
        holder.valueView.text = operation.value
        holder.dateView.text = operation.date
        if (operation.type) {
            //positivo
            holder.imageView.setImageResource(com.example.revenuer.R.drawable.arrowup)
        } else {
            //negativo
            holder.imageView.setImageResource(com.example.revenuer.R.drawable.arrowdown)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}