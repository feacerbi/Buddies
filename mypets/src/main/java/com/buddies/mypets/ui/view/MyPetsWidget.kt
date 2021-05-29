package com.buddies.mypets.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import com.buddies.common.model.Buddy
import com.buddies.common.model.DefaultError
import com.buddies.common.util.inflater
import com.buddies.common.util.safeLaunch
import com.buddies.mypets.R
import com.buddies.mypets.databinding.MyPetsWidgetBinding
import com.buddies.mypets.ui.adapter.MyPetsAdapter
import com.buddies.mypets.usecase.MyPetsUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.koin.java.KoinJavaComponent.inject

class MyPetsWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : MotionLayout(context, attrs, defStyle) {

    private val binding: MyPetsWidgetBinding

    private val petUseCases: MyPetsUseCases by inject(MyPetsUseCases::class.java)

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var adapter: MyPetsAdapter? = null

    private var onExpandedListener: ((Boolean) -> Unit)? = null
    private var onNewPetListener: (() -> Unit)? = null

    private val backPressedCallback by lazy {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                transitionBack()
            }
        }
    }

    init {
        MyPetsWidgetBinding.inflate(inflater(), this, true).apply {
            binding = this
        }

        val styleAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.MyPetsWidget, defStyle, 0
        )

        styleAttrs.recycle()
    }

    fun addOnPetClickListener(onClick: (Buddy) -> Unit) {
        adapter?.onPetClick = onClick
    }

    fun addNewPetClickListener(onClick: () -> Unit) {
        onNewPetListener = onClick
    }

    fun addBackPressedHandler(owner: LifecycleOwner, dispatcher: OnBackPressedDispatcher) {
        dispatcher.addCallback(owner, backPressedCallback)
    }

    fun setExpanded(expanded: Boolean) = with (binding) {
        motion.progress = if (expanded) 1F else 0F
        if (expanded) finalStateSetup(false) else initialStateSetup()
    }

    fun isExpanded() = binding.motion.progress == 1F

    fun refresh() = reloadBuddies()

    fun setup(lifecycleOwner: LifecycleOwner) = with (binding) {

        elevation = context.resources.getDimension(R.dimen.bottom_pets_elevation)

        petsToolbar.setNavigationOnClickListener {
            transitionBack()
        }

        petsToolbar.title = context.getString(R.string.my_pets_title)
        petsToolbar.inflateMenu(R.menu.pets_toolbar_menu)
        petsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> {
                    onNewPetListener?.invoke()
                    true
                }
                else -> false
            }
        }

        motion.setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(motionLayout: MotionLayout, p1: Int) {
                val isEnd = motionLayout.progress == 1f

                if (isEnd) {
                    finalStateSetup()
                } else {
                    initialStateSetup()
                }

                onExpandedListener?.invoke(isEnd)
            }
        })

        adapter = MyPetsAdapter(lifecycleOwner)
        petsList.adapter = adapter
        petsListEmpty.alpha = 0F

        initialStateSetup()
    }

    private fun transitionBack() = with (binding) {
        changeGridSize(big = false)
        motion.transitionToStart()
    }

    private fun initialStateSetup() = with (binding) {
        motion.isInteractionEnabled = true
        motion.enableTransition(R.id.my_pets_transition, true)
        backPressedCallback.isEnabled = false
        petsToolbar.isEnabled = false
    }

    private fun finalStateSetup(animate: Boolean = true) = with (binding) {
        motion.isInteractionEnabled = false
        motion.enableTransition(R.id.my_pets_transition, false)
        backPressedCallback.isEnabled = true
        petsToolbar.isEnabled = true

        if (animate) TransitionManager.beginDelayedTransition(motion)
        changeGridSize(big = true)
    }

    private fun changeGridSize(big: Boolean) = with (binding) {
        adapter?.isBig = big
        petsList.layoutManager = GridLayoutManager(context, calculateSpanCount())
        adapter?.notifyItemRangeChanged(0, adapter?.itemCount ?: 0)
    }

    private fun reloadBuddies() = scope.safeLaunch(::showError) {
        val buddies = petUseCases.getBuddiesFromCurrentUser()

        adapter?.submitList(buddies)
        binding.petsList.layoutManager = GridLayoutManager(context, calculateSpanCount())

        updatePetsCount(adapter?.itemCount ?: 0)
    }

    private fun calculateSpanCount(): Int {
        val maxSpan = context.resources.getInteger(R.integer.mypets_widget_small_span_count)

        return when {
            adapter?.isBig == true -> {
                context.resources.getInteger(R.integer.mypets_widget_big_span_count)
            }
            adapter?.itemCount in 1 until maxSpan -> {
                adapter?.itemCount ?: 0
            }
            else -> {
                maxSpan
            }
        }
    }

    private fun updatePetsCount(
        count: Int
    ) = with (binding) {
        petsCount.text = count.toString()
        petsListEmpty.alpha = if (adapter?.itemCount == 0) 0.8F else 0F
    }

    private fun showError(error: DefaultError) {
        // TODO Show error on widget
    }

    private fun touchEventInsideTargetView(event: MotionEvent): Boolean {
        val touchRegionView = binding.petsSheet

        if (event.x > touchRegionView.left && event.x < touchRegionView.right) {
            if (event.y > touchRegionView.top && event.y < touchRegionView.bottom) {
                return true
            }
        }
        return false
    }

    private fun isAClick(event: MotionEvent) =
        event.action == ACTION_UP && event.eventTime - event.downTime < CLICK_THRESHOLD

    private fun clickAction() = binding.motion.transitionToEnd()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val isInTarget = touchEventInsideTargetView(event)

        return when {
            isInTarget && isAClick(event) -> false.also { clickAction() }
            isInTarget -> super.onInterceptTouchEvent(event)
            else -> true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scope.safeLaunch(::showError) { reloadBuddies() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancelChildren()
    }

    companion object {
        private const val CLICK_THRESHOLD = 100
    }
}
