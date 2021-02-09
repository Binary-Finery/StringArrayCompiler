package com.spencer_studios.stringarraycompiler.activites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import com.spencer_studios.stringarraycompiler.R
import kotlinx.android.synthetic.main.content_title.*

class TitleActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
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
}