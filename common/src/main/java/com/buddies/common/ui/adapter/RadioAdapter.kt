package com.buddies.common.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.databinding.RadioListItemBinding
import com.buddies.common.util.inflater

class RadioAdapter<T>(
    list: List<T>,
    val displayText: (T) -> String = { it.toString() },
    selected: T? = null
) : SelectableAdapter<RadioAdapter<T>.RadioViewHolder, T>() {

    private val items: MutableList<Pair<T, Boolean>> = mutableListOf()

    private var onSelectedChanged: ((T) -> Unit)? = null

    init {
        list.forEachIndexed { index, item ->
            items.add(Pair(item, selected != null && item == selected || selected == null && index == 0))
        }
    }

    fun getSelectedItem() = items.firstOrNull { it.second }?.first

    override fun setOnSelectedListener(listener: (T) -> Unit) {
        onSelectedChanged = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder =
        RadioViewHolder(
            RadioListItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    inner class RadioViewHolder(
        val binding: RadioListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val item = items[position]

            radioButton.text = displayText(item.first)
            radioButton.isChecked = item.second

            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectPosition(position)
                    onSelectedChanged?.invoke(item.first)
                }
            }
        }

        private fun selectPosition(position: Int) {
            items.forEachIndexed { index, pair ->
                items[index] = Pair(pair.first, index == position)
            }
            notifyDataSetChanged()
        }
    }
}