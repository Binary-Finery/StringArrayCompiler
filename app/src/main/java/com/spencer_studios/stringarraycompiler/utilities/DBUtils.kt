package com.spencer_studios.stringarraycompiler.utilities

import android.content.Context
import spencerstudios.com.jetdblib.JetDB

class DBUtils(private val ctx: Context) {

    private val key = "elements"

    fun addElement(text: String) : Int {
        val elements = getAllElements()
        elements.add(text)
        saveElements(elements)
        msg(ctx, "element saved")
        return arraySize()
    }

    fun arraySize() : Int{
        return getAllElements().size
    }

    fun saveElements(elements: ArrayList<String>) {
        JetDB.putStringList(ctx, elements, key)
    }

    fun getAllElements(): ArrayList<String> {
        return JetDB.getStringList(ctx, key)
    }

    fun deleteElement(idx: Int) {
        val elements = getAllElements()
        elements.removeAt(idx)
        saveElements(elements)
    }

    fun editElement(text: String, idx: Int) {
        val elements = getAllElements()
        elements[idx] = text
        saveElements(elements)
        msg(ctx, "element updated")
    }

    fun deleteAllElements(){
        saveElements(ArrayList())
    }
}