package com.wing.tree.bruni.translation.archive.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wing.tree.bruni.core.extension.startActivity
import com.wing.tree.bruni.translation.archive.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.hello).setOnClickListener {
            startActivity<ProcessTextActivity>()
        }
    }
}