package com.spencer_studios.stringarraycompiler.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.spencer_studios.stringarraycompiler.*
import com.spencer_studios.stringarraycompiler.adapters.ElementsAdapter
import com.spencer_studios.stringarraycompiler.interfaces.AdapterCallback
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import kotlinx.android.synthetic.main.content_array_list.*

class AllElementsActivity : AppCompatActivity(),
    AdapterCallback {

    private lateinit var elementsAdapter: ElementsAdapter
    private lateinit var elements : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_array_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val db = DBUtils(this)
        supportActionBar?.title = "All Elements [${db.arraySize()}]"

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

    private fun confirmDeleteDialog(){
        AlertDialog.Builder(this).apply {
            setTitle("Delete All Elements")
            setMessage("delete all elements, are you sure?")
            setPositiveButton("yes"){d,_->
                DBUtils(this@AllElementsActivity)
                    .deleteAllElements()
                elements.clear()
                elementsAdapter.notifyDataSetChanged()
                supportActionBar?.title = "All Elements [${DBUtils(
                    this@AllElementsActivity
                ).arraySize()}]"
                d.dismiss()
            }
            setNegativeButton("cancel"){d,_->
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
}