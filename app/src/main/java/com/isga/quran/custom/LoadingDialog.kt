package com.isga.quran.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.isga.quran.R

class LoadingDialog(context: Context): Dialog(context) {
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.loading_dialog)
        setCancelable(false)

        button = findViewById<Button>(R.id.loading_cancel)

    }

    fun setOnClickCancelButton(callback: ()-> Unit){
        button.setOnClickListener {
            callback()
            this.dismiss()
        }
    }
}