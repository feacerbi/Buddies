package com.buddies.common.ui.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class SelectableAdapter<T : RecyclerView.ViewHolder, R> : RecyclerView.Adapter<T>() {
    abstract fun setOnSelectedListener(listener: (R) -> Unit)
}