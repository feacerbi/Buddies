package com.buddies.common.util

import android.content.res.Resources
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coil.request.CachePolicy
import coil.request.LoadRequestBuilder
import coil.transform.CircleCropTransformation
import com.buddies.common.R
import com.buddies.common.databinding.InputTextLayoutBinding
import com.buddies.common.databinding.SelectableListLayoutBinding
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.UNKNOWN
import com.buddies.common.model.OwnershipCategory.*
import com.buddies.common.ui.SelectableAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(this, Observer { action.invoke(it) })
}

var View.show: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
fun View.hide() { visibility = View.GONE }
fun View.invisible(apply: Boolean = true) { visibility = if (apply) View.INVISIBLE else View.VISIBLE }
fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

fun RecyclerView.animate(run: Boolean) {
    if (run) {
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_bottom_up)
        scheduleLayoutAnimation()
    }
}

fun Float?.orZero(): Float = this ?: 0f

fun LoadRequestBuilder.createLoadRequest(
    lifecycleOwner: LifecycleOwner? = null,
    circleTransform: Boolean = false,
    @DrawableRes error: Int = -1
) {
    memoryCachePolicy(CachePolicy.ENABLED)
    networkCachePolicy(CachePolicy.ENABLED)
    crossfade(true)
    lifecycle(lifecycleOwner)
    if (error != -1) error(error)
    if (circleTransform) transformations(CircleCropTransformation())
}

fun CoroutineScope.safeLaunch(
    error: (DefaultError) -> Unit = {},
    context: CoroutineContext = coroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = launch(context) {
    try {
        block.invoke(this)
    } catch (exception: Exception) {
        error.invoke(exception.toDefaultError())
    }
}

fun Exception.toDefaultError() =
    when (this) {
        is DefaultErrorException -> error
        else -> DefaultError(UNKNOWN)
    }

fun DefaultError.toException() =
    DefaultErrorException(this)

fun generateNewId() = UUID.randomUUID().toString()

fun Float.toPx(res: Resources) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    res.displayMetrics
)

fun Float.toDp(res: Resources) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    this,
    res.displayMetrics
)

fun Fragment.openBottomEditDialog(
    hint: String = "",
    text: String = "",
    positiveAction: (String) -> Unit,
    dismissAction: (() -> Unit)? = null
) {
    val inputView = InputTextLayoutBinding.inflate(layoutInflater)
    val bottomSheet = BottomSheetDialog(inputView.root.context).apply {
        setContentView(inputView.root)
        dismissWithAnimation = true
        setCanceledOnTouchOutside(true)
    }

    with (inputView) {
        inputLayout.hint = hint
        inputEditText.setText(text)
        inputEditText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        cancelButton.setOnClickListener {
            dismissAction?.invoke()
            bottomSheet.cancel()
        }
        changeButton.setOnClickListener {
            positiveAction.invoke(inputEditText.text.toString())
            bottomSheet.cancel()
        }
    }

    bottomSheet.show()
}

fun <T : RecyclerView.ViewHolder, R> Fragment.openBottomSelectableDialog(
    title: String,
    adapter: SelectableAdapter<T, R>,
    onSelectedChanged: (() -> Unit)? = null,
    onSelectedButton: String = getString(com.buddies.common.R.string.change_button),
    dismissAction: (() -> Unit)? = null
) {
    val selectableView = SelectableListLayoutBinding.inflate(layoutInflater)
    val bottomSheet = BottomSheetDialog(selectableView.root.context).apply {
        setContentView(selectableView.root)
        dismissWithAnimation = true
        setCanceledOnTouchOutside(true)
    }

    with (selectableView) {
        adapter.addOnSelectedListener {
            changeButton.isEnabled = true
        }

        listTitle.text = title
        list.adapter = adapter

        cancelButton.setOnClickListener {
            dismissAction?.invoke()
            bottomSheet.cancel()
        }
        changeButton.text = onSelectedButton
        changeButton.setOnClickListener {
            onSelectedChanged?.invoke()
            bottomSheet.cancel()
        }
    }

    bottomSheet.show()
}

fun String.toOwnershipCategory() = when (this) {
    OWNER.name -> OWNER
    FAMILY.name -> FAMILY
    FRIEND.name -> FRIEND
    CARE_TAKER.name -> CARE_TAKER
    else -> VISITOR
}