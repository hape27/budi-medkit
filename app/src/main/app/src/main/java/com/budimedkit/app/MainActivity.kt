
package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val navy = Color.rgb(20, 35, 65)
    private val green = Color.rgb(20, 150, 100)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 25, 20, 25)
        layout.setBackgroundColor(Color.rgb(245, 247, 250))

        scroll.addView(layout)

        val header = TextView(this)
        header.text = "BUDI JAYA MOTOR\nBUDI MEDKIT"
        header.textSize = 25f
        header.setTextColor(Color.WHITE)
        header.setTypeface(null, 1)
        header.setPadding(20, 25, 20, 25)
        header.setBackgroundColor(navy)
        layout.addView(header)

        addTitle(layout, "Ringkasan Keuangan")

        addCard(layout, "Saldo Kas", "Rp 0", green)
        addCard(layout, "Total Pemasukan", "Rp 0", navy)
        addCard(layout, "Total Pengeluaran", "Rp 0", Color.RED)
        addCard(layout, "Laba Bersih", "Rp 0", green)

        addTitle(layout, "Menu Utama")

        val menus = arrayOf(
            "Transaksi Pemasukan",
            "Transaksi Pengeluaran",
            "Servis Kendaraan",
            "Data Pelanggan",
            "Data Kendaraan",
            "Stok Sparepart",
            "Gaji Mekanik",
            "Laporan Keuangan"
        )

        for (menu in menus) {
            val item = TextView(this)
            item.text = "▸  $menu"
            item.textSize = 17f
            item.setTextColor(navy)
            item.setPadding(18, 20, 18, 20)
            item.setBackgroundColor(Color.WHITE)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 5, 0, 5)
            layout.addView(item, params)
        }

        setContentView(scroll)
    }

    private fun addTitle(layout: LinearLayout, text: String) {
        val title = TextView(this)
        title.text = text
        title.textSize = 19f
        title.setTextColor(navy)
        title.setTypeface(null, 1)
        title.setPadding(0, 25, 0, 12)
        layout.addView(title)
    }

    private fun addCard(
        layout: LinearLayout,
        title: String,
        value: String,
        color: Int
    ) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(20, 18, 20, 18)
        card.setBackgroundColor(Color.WHITE)

        val label = TextView(this)
        label.text = title
        label.textSize = 15f
        label.setTextColor(Color.DKGRAY)

        val amount = TextView(this)
        amount.text = value
        amount.textSize = 24f
        amount.setTextColor(color)
        amount.setTypeface(null, 1)

        card.addView(label)
        card.addView(amount)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 5, 0, 5)
        layout.addView(card, params)
    }
}
