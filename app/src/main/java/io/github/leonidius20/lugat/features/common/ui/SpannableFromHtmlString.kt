package io.github.leonidius20.lugat.features.common.ui

import android.os.Build
import android.text.Html
import android.text.Spanned

fun htmlSpannable(htmlStr: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(htmlStr)
    }
}