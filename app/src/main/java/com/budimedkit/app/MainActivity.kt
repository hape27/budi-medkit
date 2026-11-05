
package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val navy = Color.rgb(20, 35, 65)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 25, 20, 25)
        layout.setBackgroundColor(Color.rgb(245, 247, 250))

        scroll.addView(layout)

        val header = TextView(this)
        header.text = "BUDI JAYA MOTOR\nBUDI MEDKIT\nManajemen Bengkel"
        header.textSize = 24f
        header.setTextColor(Color.WHITE)
        header.setTypeface(null, 1)
        header.gravity = Gravity.CENTER
        header.setPadding(20, 30, 20, 30)
        header.setBackgroundColor(navy)
        layout.addView(header)

        addTitle(layout, "MENU UTAMA")

        addMenu(layout, "Transaksi Pemasukan & Pengeluaran") {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }

        addMenu(layout, "Servis Kendaraan")
        addMenu(layout, "Data Pelanggan")
        addMenu(layout, "Data Kendaraan")
        addMenu(layout, "Stok Sparepart")
        addMenu(layout, "Gaji Mekanik")
        addMenu(layout, "Laporan Keuangan")

        setContentView(scroll)
    }

    private fun addTitle(layout: LinearLayout, text: String) {
        val title = TextView(this)
        title.text = text
        title.textSize = 19f
        title.setTextColor(navy)
        title.setTypeface(null, 1)
        title.setPadding(0, 25, 0, 15)
        layout.addView(title)
    }

    private fun addMenu(
        layout: LinearLayout,
        text: String,
        action: (() -> Unit)? = null
    ) {
        val button = Button(this)
        button.text = text

        button.setOnClickListener {
            if (action != null) {
                action()
            } else {
                Toast.makeText(
                    this,
                    "$text belum tersedia",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        layout.addView(button)
    }
}
