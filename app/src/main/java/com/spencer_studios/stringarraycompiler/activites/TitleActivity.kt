package com.spencer_studios.stringarraycompiler.activites

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.spencer_studios.stringarraycompiler.R
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import kotlinx.android.synthetic.main.content_title.*
import spencerstudios.com.jetdblib.JetDB

class TitleActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
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
        setContentView(R.layout.activity_title)
        setSupportActionBar(findViewById(R.id.toolbar))

        btnViewElements.text = "all elements [${DBUtils(
            this
        ).arraySize()}]"

        btnAddElement.setOnClickListener {
            val i = Intent(it.context, AddElementActivity::class.java)
            i.putExtra("mode", 0)
            startActivity(i)
            finish()
        }

        btnViewElements.setOnClickListener {
            startActivity(Intent(it.context, AllElementsActivity::class.java))
            finish()
        }

        btnViewCode.setOnClickListener {
            startActivity(Intent(it.context, CompileArrayActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_title, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                val darkMode = JetDB.getBoolean(this, "dark_mode", false)
                JetDB.putBoolean(this, !darkMode, "dark_mode")
                recreate()
                true
            }
            R.id.action_bracket_style -> {
                bracketSelectorDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bracketSelectorDialog() {

        val brackets = arrayOf("[ ... ]", "{ ... }", "( ... )", "< ... >", "none")
        val bracketIdx = JetDB.getInt(this, "bracket", 0)

        AlertDialog.Builder(this).apply {
            setTitle("Bracket Style For Compiled Array")
            setSingleChoiceItems(brackets, bracketIdx) { _: DialogInterface, i: Int ->
                JetDB.putInt(this@TitleActivity, i, "bracket")
            }
            setPositiveButton("okay") { d, _ ->
                d.dismiss()
            }
            show()
        }
    }
}