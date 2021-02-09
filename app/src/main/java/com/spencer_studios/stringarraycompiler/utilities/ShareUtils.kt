package com.spencer_studios.stringarraycompiler.utilities

import android.content.Context
import android.content.Intent

fun shareArray(ctx: Context, arr: String) {
    val i = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, arr)
    }
    ctx.startActivity(Intent.createChooser(i, "share to"))
}