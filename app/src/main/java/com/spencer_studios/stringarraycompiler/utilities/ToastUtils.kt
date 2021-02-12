package com.spencer_studios.stringarraycompiler.utilities

import android.content.Context
import android.view.Gravity
import android.widget.Toast

fun msg(ctx: Context, str: String) {
    Toast.makeText(ctx, str, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}