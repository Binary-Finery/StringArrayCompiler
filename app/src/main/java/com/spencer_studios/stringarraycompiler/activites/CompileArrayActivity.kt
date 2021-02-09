package com.spencer_studios.stringarraycompiler.activites

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.spencer_studios.stringarraycompiler.R
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import com.spencer_studios.stringarraycompiler.utilities.shareArray
import kotlinx.android.synthetic.main.content_compile_array.*
import java.io.File
import java.text.DateFormat

class CompileArrayActivity : AppCompatActivity() {

    private val permissionRequestCode: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val builder = StringBuilder()
            builder.append("[\n")
            elements.forEach {
                builder.append("\"${it.replace("\"", "\\\"")}\",\n")
            }
            builder.setLength(builder.length - 2)
            builder.append("\n]")
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
            val name = "array_${DateFormat.getDateTimeInstance().format(System.currentTimeMillis()).replace(" ","_")}.txt"
            val fileName = File(getExternalFilesDir("compiled_array"), name)
            fileName.writeText(textViewCompiledArray.text.toString().trim())
            savedDialog(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
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
                    Toast.makeText(this, "no elements to delete", Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.action_share -> {
                val content = textViewCompiledArray.text.toString()
                if (content.isNotEmpty()) {
                    shareArray(this, content)
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