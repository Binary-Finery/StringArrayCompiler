package com.spencer_studios.stringarraycompiler.activites

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.spencer_studios.stringarraycompiler.R
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import com.spencer_studios.stringarraycompiler.utilities.msg
import com.spencer_studios.stringarraycompiler.utilities.shareArray
import kotlinx.android.synthetic.main.content_compile_array.*
import spencerstudios.com.jetdblib.JetDB
import java.io.File
import java.text.DateFormat

class CompileArrayActivity : AppCompatActivity() {

    private val permissionRequestCode: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(
            if (JetDB.getBoolean(
                    this,
                    "dark_mode",
                    false
                )
            ) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_compile_array)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val elements = DBUtils(
            this
        ).getAllElements()

        if (elements.isNotEmpty()) {
            var startBracket = ""
            var endBracket = ""
            when (JetDB.getInt(this, "bracket", 0)) {
                0 -> {
                    startBracket = "[\n"
                    endBracket = "\n]"
                }
                1 -> {
                    startBracket = "{\n"
                    endBracket = "\n}"
                }
                2 -> {
                    startBracket = "(\n"
                    endBracket = "\n)"
                }
                3 -> {
                    startBracket = "<\n"
                    endBracket = "\n>"
                }
                4 -> {
                    startBracket = ""
                    endBracket = ""
                }
            }
            val builder = StringBuilder()
            builder.append(startBracket)
            elements.forEach {
                builder.append("\"${it.replace("\"", "\\\"")}\",\n")
            }
            builder.setLength(builder.length - 2)
            builder.append(endBracket)
            textViewCompiledArray.text = "$builder"
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission check", "Permission denied")
            makeRequest()
        } else {
            write()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            permissionRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                write()
            }
        }
    }

    private fun write() {
        try {
            val name = "array_${DateFormat.getDateTimeInstance().format(System.currentTimeMillis())
                .replace(" ", "_")}.txt"
            val fileName = File(getExternalFilesDir("compiled_array"), name)
            fileName.writeText(textViewCompiledArray.text.toString().trim())
            savedDialog(fileName)
        } catch (e: Exception) {
            msg(this, "unable to save")
        }
    }

    private fun savedDialog(s: File) {
        AlertDialog.Builder(this).apply {
            setTitle("File Saved")
            setMessage("file save at:\n\"${s.path}\"")
            setPositiveButton("okay") { d, _ ->
                d.dismiss()
            }
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_compile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (DBUtils(this).arraySize() > 0) {
                    setupPermissions()
                } else {
                    msg(this, "no elements to save")
                }
                true
            }
            R.id.action_share -> {
                val content = textViewCompiledArray.text.toString()
                if (content.isNotEmpty()) {
                    shareArray(this, content)
                } else {
                    msg(this, "no elements to share")
                }
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}