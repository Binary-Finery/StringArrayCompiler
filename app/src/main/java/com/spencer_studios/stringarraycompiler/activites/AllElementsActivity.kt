package com.spencer_studios.stringarraycompiler.activites

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spencer_studios.stringarraycompiler.R
import com.spencer_studios.stringarraycompiler.adapters.ElementsAdapter
import com.spencer_studios.stringarraycompiler.interfaces.AdapterCallback
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import kotlinx.android.synthetic.main.content_array_list.*
import spencerstudios.com.jetdblib.JetDB
import java.util.*

class AllElementsActivity : AppCompatActivity(),
    AdapterCallback {

    private lateinit var elementsAdapter: ElementsAdapter
    private lateinit var elements: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            if (JetDB.getBoolean(
                    this,
                    "dark_mode",
                    false
                )
            ) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_array_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        if (!JetDB.getBoolean(this, "hint_shown", false)) {
            showHintDialog()
        }

        val db = DBUtils(this)

        setToolbarValue(db.arraySize())

        elements = db.getAllElements()

        elementsAdapter =
            ElementsAdapter(
                elements,
                this
            )

        rv.apply {
            layoutManager = LinearLayoutManager(this@AllElementsActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@AllElementsActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            adapter = elementsAdapter
        }

        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition

                    Collections.swap(elements, fromPos, toPos)
                    elementsAdapter.notifyItemMoved(fromPos, toPos)
                    return true
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = .50f
                        viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#e0e0e0"))
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1f
                    elementsAdapter.notifyDataSetChanged()
                    viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                    DBUtils(this@AllElementsActivity).saveElements(elements)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    /*unused method*/
                }
            }).attachToRecyclerView(rv)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lsit_elements, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_elements -> {
                confirmDeleteDialog()
                true
            }
            android.R.id.home -> {
                navHome()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDeleteDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Delete All Elements")
            setMessage("delete all elements, are you sure? this action cannot be undone")
            setPositiveButton("yes") { d, _ ->
                DBUtils(this@AllElementsActivity)
                    .deleteAllElements()
                elements.clear()
                elementsAdapter.notifyDataSetChanged()
                setToolbarValue(
                    DBUtils(
                        this@AllElementsActivity
                    ).arraySize()
                )
                d.dismiss()
            }
            setNegativeButton("cancel") { d, _ ->
                d.dismiss()
            }
            show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navHome()
    }

    private fun navHome() {
        startActivity(Intent(this, TitleActivity::class.java))
        finish()
    }

    override fun setToolbarValue(size: Int) {
        supportActionBar?.title = "All Elements [$size]"
    }

    private fun showHintDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Hint")
            setMessage("you can re-order elements by pressing them for 1 second and dragging to the desired index")
            setPositiveButton("got it") { d, _ ->
                JetDB.putBoolean(this@AllElementsActivity, true, "hint_shown")
                d.dismiss()
            }
            show()
        }
    }
}