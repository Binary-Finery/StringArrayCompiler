package com.spencer_studios.stringarraycompiler.activites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spencer_studios.stringarraycompiler.utilities.DBUtils
import com.spencer_studios.stringarraycompiler.R
import kotlinx.android.synthetic.main.content_main.*

class AddElementActivity : AppCompatActivity(){

    var mode : Int = 0
    var index = 0
    private val addElement = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        mode = intent.getIntExtra("mode", addElement)

        if(mode != addElement) {
            index = intent.getIntExtra("element_index", 0)
            val element = DBUtils(
                this
            ).getAllElements()[index]
            etInput.setText(element)
            supportActionBar?.title = "Edit Element [$index]"
            btnSave.text = "update"
        }else supportActionBar?.title = "Add Element"


        btnSave.setOnClickListener {
            val utils =
                DBUtils(it.context)
            val text = etInput.text.toString().trim()
            if(mode == addElement) {
                utils.addElement(text)
            }else{
                utils.editElement(text, index)
            }
        }

        btnClear.setOnClickListener {
           etInput.setText("")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(mode!=addElement){
            startActivity(Intent(this, AllElementsActivity::class.java))
        }else{
            startActivity(Intent(this, TitleActivity::class.java))
        }
        finish()
    }

}