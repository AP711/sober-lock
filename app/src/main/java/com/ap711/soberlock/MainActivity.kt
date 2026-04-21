package com.ap711.soberlock

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "Sober Lock"
        tv.setTextColor(Color.WHITE)
        tv.textSize = 32f
        tv.gravity = Gravity.CENTER
        val layout = LinearLayout(this)
        layout.gravity = Gravity.CENTER
        layout.setBackgroundColor(Color.parseColor("#1A237E"))
        layout.addView(tv)
        setContentView(layout)
    }
}
