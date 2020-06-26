package com.lawmobile.presentation.ui.helpSection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lawmobile.presentation.R
import kotlinx.android.synthetic.main.activity_help_page.*

class HelpPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_page)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        pdfView.fromAsset("FMA_Interactive_Help.pdf").load()
    }
}