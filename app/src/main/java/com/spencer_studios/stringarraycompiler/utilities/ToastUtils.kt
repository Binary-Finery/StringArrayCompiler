package com.spencer_studios.stringarraycompiler.utilities

import android.content.Context
import android.widget.Toast

fun msg(c: Context, s: String) {
    Toast.makeText(c, s, Toast.LENGTH_LONG).show()
}