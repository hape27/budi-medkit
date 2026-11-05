
package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(24, 24, 24, 24)
        layout.setBackgroundColor(Color.rgb(240, 248, 255))

        val title = TextView(this)
        title.text = "BUDI MEDKIT"
        title.textSize = 30f
        title.setTextColor(Color.rgb(0, 90, 140))
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Manajemen Bengkel & Servis Kendaraan"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        layout.addView(title)
        layout.addView(subtitle)

        setContentView(layout)
    }
}
