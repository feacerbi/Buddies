package com.buddies.mypets.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionManager
import com.buddies.common.model.DefaultError
import com.buddies.common.model.Pet
import com.buddies.common.util.inflater
import com.buddies.common.util.safeLaunch
import com.buddies.mypets.R
import com.buddies.mypets.databinding.MyPetsWidgetBinding
import com.buddies.mypets.usecase.PetUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.CoroutineContext

class MyPetsWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : MotionLayout(context, attrs, defStyle) {

    private var binding: MyPetsWidgetBinding

    private val petUseCases by inject(PetUseCases::class.java)

    private lateinit var adapter: MyPetsAdapter
    private val job = SupervisorJob()

    private var onPetClicked: ((Pet) -> Unit)? = null

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

        elevation = context.resources.getDimension(R.dimen.bottom_pets_elevation)

        val styleAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.MyPetsWidget, defStyle, 0
        )

        setup(styleAttrs)
        styleAttrs.recycle()
    }

    fun addOnPetClickListener(onClick: (Pet) -> Unit) {
        onPetClicked = onClick
    }

    fun addBackPressedHandler(owner: LifecycleOwner, dispatcher: OnBackPressedDispatcher) {
        dispatcher.addCallback(owner, backPressedCallback)
    }

    private fun setup(attrs: TypedArray) = with (binding) {

        petsToolbar.setNavigationOnClickListener {
            transitionBack()
        }

        petsToolbar.title = context.getString(R.string.my_pets_title)
        petsToolbar.inflateMenu(R.menu.pets_toolbar_menu)
        petsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> {
                    // TODO Open add pet screen
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
                motionLayout.isInteractionEnabled = motionLayout.progress != 1f
                motionLayout.enableTransition(R.id.my_pets_transition, motionLayout.progress != 1f)
                backPressedCallback.isEnabled = motionLayout.progress == 1f
                petsToolbar.isEnabled = motionLayout.progress == 1f

                if (motionLayout.progress == 1f) {
                    TransitionManager.beginDelayedTransition(motion)
                    petsList.layoutManager = GridLayoutManager(context,
                        context.resources.getInteger(R.integer.mypets_widget_big_span_count))
                    adapter.isBig = true
                    adapter.notifyItemRangeChanged(0, adapter.itemCount)
                }
            }
        })
    }

    private fun addPets(
        lifecycleOwner: LifecycleOwner? = null
    ) = getScope().safeLaunch(::showError) {
        val pets = petUseCases.getPetsFromCurrentUser()

        if (isAttachedToWindow) {
            adapter = MyPetsAdapter(pets, owner = lifecycleOwner, onClick = onPetClicked)

            binding.petsList.layoutManager = GridLayoutManager(context, calculateSpanCount())
            binding.petsList.adapter = adapter

            updatePetsCount(adapter.itemCount)
        }
    }

    private fun transitionBack() {
        binding.petsList.layoutManager = GridLayoutManager(context, calculateSpanCount())
        adapter.isBig = false
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
        binding.motion.transitionToStart()
    }

    private fun calculateSpanCount(): Int {
        val maxSpan = context.resources.getInteger(R.integer.mypets_widget_small_span_count)

        return if (adapter.itemCount in 1 until maxSpan) {
            adapter.itemCount
        } else {
            maxSpan
        }
    }

    private fun updatePetsCount(
        count: Int
    ) = with (binding) {
        petsCount.text = count.toString()
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

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val isInTarget = touchEventInsideTargetView(event)

        return if (isInTarget) {
            super.onInterceptTouchEvent(event)
        } else {
            true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getScope().safeLaunch(::showError) { addPets() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancelChildren()
    }

    private fun getScope(coroutineContext: CoroutineContext = Dispatchers.Main) =
        CoroutineScope(coroutineContext + job)
}
