package com.buddies.common.util

import android.content.Context
import android.net.Uri
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.R
import com.buddies.common.databinding.InputTextLayoutBinding
import com.buddies.common.databinding.SelectableListLayoutBinding
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.Result
import com.buddies.common.ui.SelectableAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import kotlin.coroutines.CoroutineContext

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(this, { action.invoke(it) })
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

fun ImageView.load(
    uri: Uri?,
    lifecycleOwner: LifecycleOwner,
    options: RequestOptions.() -> Unit = {}
) {
    val cache by inject(ImageCache::class.java)
    cache.load(lifecycleOwner, this, uri, options)
}

fun ImageView.load(
    image: String?,
    lifecycleOwner: LifecycleOwner,
    options: RequestOptions.() -> Unit = {}
) {
    val cache by inject(ImageCache::class.java)
    cache.load(lifecycleOwner, this, image, options)
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

fun generateNewId() = UUID.randomUUID().toString()

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

fun openCustomBottomSheet(
    content: View
) = BottomSheetDialog(content.context).apply {
    setContentView(content)
    dismissWithAnimation = true
    setCanceledOnTouchOutside(true)
}.apply { show() }

fun <T> Result<T>.handleResult(
) = when (this) {
    is Result.Success -> data
    is Result.Fail -> throw DefaultErrorException(error)
}

suspend fun <T, R> Result<T>.mapResult(
    transform: suspend (T?) -> R
): Result<R> =
    when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Fail -> Result.Fail(error)
    }

fun Date.toFormatted(context: Context) = this.time.toFormattedDate(context)

fun String.toFormattedDate(context: Context) = this.toLong().toFormattedDate(context)

fun Long.toFormattedDate(context: Context): String {
    val currentTime = Calendar.getInstance()
    val reqTime = Calendar.getInstance()
    reqTime.timeInMillis = this

    if(reqTime[Calendar.YEAR] != currentTime[Calendar.YEAR]) {
        return DateFormat.format("d MMM yyyy', 'kk:mm", reqTime).toString()
    }

    if(reqTime[Calendar.WEEK_OF_MONTH] != currentTime[Calendar.WEEK_OF_MONTH]) {
        return DateFormat.format("d MMM', 'kk:mm", reqTime).toString()
    }

    if(reqTime[Calendar.DAY_OF_WEEK] != currentTime[Calendar.DAY_OF_WEEK]) {
        return DateFormat.format("EEE', 'kk:mm", reqTime).toString()
    }

    if(reqTime[Calendar.HOUR_OF_DAY] != currentTime[Calendar.HOUR_OF_DAY]) {
        val hoursPassed = currentTime[Calendar.HOUR_OF_DAY] - reqTime[Calendar.HOUR_OF_DAY]
        return context.getString(R.string.hours_ago_timestamp, hoursPassed)
    }

    if(reqTime[Calendar.MINUTE] != currentTime[Calendar.MINUTE]) {
        val minutesPassed = currentTime[Calendar.MINUTE] - reqTime[Calendar.MINUTE]
        return context.getString(R.string.hours_ago_timestamp, minutesPassed)
    }

    return context.getString(R.string.just_now_timestamp)
}

fun String.customTextAppearance(
    context: Context,
    texts: Array<String>,
    style: Int,
    textColor: Int = -1
): SpannableString {
    return SpannableString(this).apply {
        texts.forEach {
            val index = indexOf(it)

            setSpan(
                TextAppearanceSpan(context, style),
                index,
                index + it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (textColor != -1) {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, textColor)),
                    index,
                    index + it.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}

fun ExtendedFloatingActionButton.expand(shouldExpand: Boolean) {
    if (shouldExpand) extend() else shrink()
}

fun <I, O> Fragment.registerForNonNullActivityResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>): ActivityResultLauncher<I> {
    return registerForActivityResult(contract) {
        if (it != null) {
            callback.onActivityResult(it)
        }
    }
}