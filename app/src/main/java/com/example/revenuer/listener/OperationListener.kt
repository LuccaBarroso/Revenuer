package com.example.revenuer.listener

import android.view.View

interface OperationListener {
    abstract fun onListItemClick(View: View, position: Int)
    abstract fun onListItemLongClick(view: View, adapterPosition: Int)
}