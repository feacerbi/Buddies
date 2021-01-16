package com.buddies.newpet.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.core.content.ContextCompat
import com.buddies.newpet.R
import com.buddies.newpet.databinding.StepsWidgetBinding

class StepsWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding: StepsWidgetBinding

    init {
        StepsWidgetBinding.inflate(LayoutInflater.from(context), this, true).apply {
            binding = this
        }
    }

    fun selectStep(
        step: Int
    ) = with (binding) {
        when (step) {
            1 -> {
                secondStepTick.setImageResource(R.drawable.hollow_tick_mark)
                thirdStepTick.setImageResource(R.drawable.hollow_tick_mark)

                firstTrack.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                firstTrack.alpha = UNSELECTED_ALPHA
                secondTrack.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                secondTrack.alpha = UNSELECTED_ALPHA

                ConstraintSet().apply {
                    clone(root)
                    connect(thumb.id, START, firstStepTick.id, START)
                    connect(thumb.id, END, firstStepTick.id, END)
                    applyTo(root)
                }
            }
            2 -> {
                secondStepTick.setImageResource(R.drawable.filled_tick_mark)
                thirdStepTick.setImageResource(R.drawable.hollow_tick_mark)

                firstTrack.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondaryVariant))
                firstTrack.alpha = SELECTED_ALPHA
                secondTrack.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                secondTrack.alpha = UNSELECTED_ALPHA

                ConstraintSet().apply {
                    clone(root)
                    connect(thumb.id, START, secondStepTick.id, START)
                    connect(thumb.id, END, secondStepTick.id, END)
                    applyTo(root)
                }
            }
            3 -> {
                secondStepTick.setImageResource(R.drawable.filled_tick_mark)
                thirdStepTick.setImageResource(R.drawable.filled_tick_mark)

                firstTrack.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondaryVariant))
                firstTrack.alpha = SELECTED_ALPHA
                secondTrack.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondaryVariant))
                secondTrack.alpha = SELECTED_ALPHA

                ConstraintSet().apply {
                    clone(root)
                    connect(thumb.id, START, thirdStepTick.id, START)
                    connect(thumb.id, END, thirdStepTick.id, END)
                    applyTo(root)
                }
            }
            else -> throw IllegalArgumentException("Only 3 steps allowed")
        }
    }

    companion object {
        private const val UNSELECTED_ALPHA = 0.2F
        private const val SELECTED_ALPHA = 1F
    }
}