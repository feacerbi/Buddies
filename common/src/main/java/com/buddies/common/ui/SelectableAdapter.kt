package com.buddies.common.ui

import androidx.recyclerview.widget.RecyclerView

abstract class SelectableAdapter<T : RecyclerView.ViewHolder, R> : RecyclerView.Adapter<T>() {
    abstract fun addOnSelectedListener(listener: (R) -> Unit)
}