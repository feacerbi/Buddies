package com.buddies.common.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.R
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode
import com.buddies.common.model.ErrorCode.RESULT_NULL
import com.buddies.common.model.Result
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


fun <T> LifecycleOwner.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(this, { action.invoke(it) })
}

fun View.invisible(apply: Boolean = true) { visibility = if (apply) View.INVISIBLE else View.VISIBLE }
fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

fun RecyclerView.animate(run: Boolean) {
    if (run) {
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_bottom_up)
        scheduleLayoutAnimation()
    }
}

var FloatingActionButton.isDisplayed
    get() = isShown
    set(value) { if (value) show() else hide() }

fun Context.getStringOrNull(@StringRes resId: Int?) = resId?.let { getString(resId) }

fun Context.newImageFile(): File =
    File.createTempFile(
        "${System.currentTimeMillis()}",
        ".jpg",
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    ).apply {
        deleteOnExit()
    }

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun File.toUri(context: Context): Uri =
    FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), this)

fun Float?.orZero(): Float = this ?: 0f

fun ImageView.load(
    uri: Uri?,
    lifecycleOwner: LifecycleOwner,
    options: RequestOptions.() -> Unit = {}
) {
    val handler: ImageHandler by inject(ImageHandler::class.java)
    handler.load(lifecycleOwner, this, uri, options)
}

fun ImageView.load(
    image: String?,
    lifecycleOwner: LifecycleOwner,
    options: RequestOptions.() -> Unit = {}
) {
    val handler: ImageHandler by inject(ImageHandler::class.java)
    handler.load(lifecycleOwner, this, image, options)
}

fun ImageView.load(
    bitmap: Bitmap?,
    lifecycleOwner: LifecycleOwner,
    options: RequestOptions.() -> Unit = {}
) {
    val handler: ImageHandler by inject(ImageHandler::class.java)
    handler.load(lifecycleOwner, this, bitmap, options)
}

@ExperimentalContracts
fun String?.isNotNullNorBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullNorBlank != null)
    }
    return this != null && isNotBlank()
}

fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun CoroutineScope.safeLaunch(
    error: (DefaultError) -> Unit = {},
    context: CoroutineContext = coroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = launch(context) {
    try {
        block.invoke(this)
    } catch (exception: Exception) {
        if (exception !is CancellationException) {
            error.invoke(exception.toDefaultError())
        }
    }
}

fun generateNewId() = UUID.randomUUID().toString()

fun Fragment.getQuantityString(
    @PluralsRes id: Int,
    quantity: Int,
    vararg formatArgs: Any = emptyArray()
) = requireContext().resources.getQuantityString(id, quantity, *formatArgs)

fun Fragment.setOnBackPressed(action: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action.invoke()
        }
    })
}

fun <T> Result<T>.handleResult(
) = when (this) {
    is Result.Success -> data
    is Result.Fail -> throw DefaultErrorException(error)
}

fun <T> Result<T>.handleNonNullResult(
) = when (this) {
    is Result.Success -> data ?: throw DefaultErrorException(DefaultError(RESULT_NULL))
    is Result.Fail -> throw DefaultErrorException(error)
}

suspend fun <T, R> Result<T>.mapResult(
    transform: suspend (T?) -> R
): Result<R> =
    when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Fail -> Result.Fail(error)
    }

fun Result<Boolean>.handleAccessResult() {
    when (this) {
        is Result.Success -> if (data == false) throw DefaultErrorException(DefaultError(
            ErrorCode.ACCESS_DENIED
        ))
        is Result.Fail -> throw DefaultErrorException(error)
    }
}

fun Calendar.toFormatted(context: Context) = this.timeInMillis.toFormattedDate(context)

fun String.toFormattedDate(context: Context) = this.toLong().toFormattedDate(context)

fun Int.dpToPx(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return (this * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

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

            if (index > -1) {
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
}

fun ExtendedFloatingActionButton.expand(shouldExpand: Boolean) {
    if (shouldExpand) extend() else shrink()
}

fun <I, O> Fragment.registerForNonNullActivityResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    return registerForActivityResult(contract) {
        if (it != null) {
            callback.onActivityResult(it)
        }
    }
}

fun <I> Fragment.registerForTrueActivityResult(
    contract: ActivityResultContract<I, Boolean>,
    callback: ActivityResultCallback<Boolean>
): ActivityResultLauncher<I> {
    return registerForActivityResult(contract) {
        if (it) {
            callback.onActivityResult(true)
        }
    }
}

fun Fragment.getStringOrNull(@StringRes id: Int?): String? =
    if (id == null) {
        null
    } else {
        getString(id)
    }