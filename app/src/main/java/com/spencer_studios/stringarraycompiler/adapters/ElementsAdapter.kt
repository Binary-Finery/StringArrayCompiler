package com.spencer_studios.stringarraycompiler.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spencer_studios.stringarraycompiler.interfaces.AdapterCallback
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import com.spencer_studios.stringarraycompiler.R
import com.spencer_studios.stringarraycompiler.activites.AddElementActivity

class ElementsAdapter(private val list : ArrayList<String>, private val callback : AdapterCallback)  : RecyclerView.Adapter<ElementsAdapter.Holder>() {

    class Holder(v : View) : RecyclerView.ViewHolder(v) {
        val item : TextView = v.findViewById(R.id.tvArrayItem)
        val del : ImageView = v.findViewById(R.id.icDeleteElement)
        val ed : ImageView = v.findViewById(R.id.icEditElement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, i: Int) {

        holder.item.text = list[i]

        holder.del.setOnClickListener {
            DBUtils(it.context)
                .deleteElement(i)
            list.removeAt(i)
            notifyDataSetChanged()
            callback.setToolbarValue(list.size)
        }

        holder.ed.setOnClickListener {
            val ctx = it.context
            val intent = Intent(ctx, AddElementActivity::class.java)
            intent.putExtra("mode", 1)
            intent.putExtra("element_index", i)
            ctx.startActivity(intent)
            val activity = ctx as Activity
            activity.finish()
        }
    }
}
