package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private val red = Color.rgb(202, 20, 30)
    private val darkRed = Color.rgb(151, 10, 20)
    private val navy = Color.rgb(18, 55, 112)
    private val ink = Color.rgb(35, 39, 47)
    private val muted = Color.rgb(105, 111, 122)
    private val pageColor = Color.rgb(246, 247, 250)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(pageColor)
        }
        val scroll = ScrollView(this).apply { isFillViewport = true }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(18))
        }

        // Brand area: original Budi Jaya Motor logo
        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(14), dp(12), dp(12))
            background = rounded(Color.WHITE, 20)
            elevation = dp(2).toFloat()
        }
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.budi_jaya_motor_logo)
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "Logo Budi Jaya Motor"
        }
        brand.addView(logo, LinearLayout.LayoutParams(-1, dp(142)))
        brand.addView(TextView(this).apply {
            text = "Terpercaya – Berkualitas – Bergaransi"
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(ink)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(5), 0, 0)
        })
        page.addView(brand, matchWrap())

        // Red application title strip
        val titleBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(12), dp(10), dp(12))
            background = roundedGradient(red, darkRed, 22)
            elevation = dp(3).toFloat()
        }
        titleBox.addView(TextView(this).apply {
            text = "BUDI MEDKIT"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.04f
        })
        titleBox.addView(TextView(this).apply {
            text = "Aplikasi Manajemen Bengkel"
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, dp(2), 0, 0)
        })
        val titleParams = matchWrap()
        titleParams.topMargin = dp(14)
        page.addView(titleBox, titleParams)

        val menuTitle = TextView(this).apply {
            text = "MENU UTAMA"
            textSize = 15f
            setTextColor(ink)
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(4), dp(18), 0, dp(10))
        }
        page.addView(menuTitle)

        val items = listOf(
            MenuItem("🚗", "Data\nKendaraan", red) { openModule("kendaraan") },
            MenuItem("🔧", "Servis &\nPerawatan", navy) { openModule("servis") },
            MenuItem("📋", "Riwayat\nServis", Color.rgb(22, 105, 178)) { openModule("servis") },
            MenuItem("🛒", "Suku\nCadang", Color.rgb(225, 145, 20)) { openModule("sparepart") },
            MenuItem("👥", "Data\nPelanggan", Color.rgb(22, 133, 83)) { openModule("pelanggan") },
            MenuItem("💳", "Transaksi", red) { startActivity(Intent(this, TransaksiActivity::class.java)) },
            MenuItem("🧑‍🔧", "Data\nMekanik", navy) { openModule("mekanik") },
            MenuItem("💰", "Gaji\nMekanik", Color.rgb(22, 133, 83)) { openModule("gaji") },
            MenuItem("📊", "Laporan\nKeuangan", Color.rgb(103, 57, 166)) { openModule("laporan") },
        )

        for (rowIndex in 0 until 3) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            for (colIndex in 0 until 3) {
                val item = items[rowIndex * 3 + colIndex]
                row.addView(createMenuTile(item), LinearLayout.LayoutParams(0, dp(142), 1f).apply {
                    marginStart = dp(4)
                    marginEnd = dp(4)
                })
            }
            val rowParams = LinearLayout.LayoutParams(-1, -2)
            rowParams.bottomMargin = dp(9)
            page.addView(row, rowParams)
        }

        val footer = TextView(this).apply {
            text = "Bersama BJM, Perjalanan Anda Lebih Nyaman"
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD_ITALIC)
            setPadding(dp(12), dp(16), dp(12), dp(16))
            background = roundedGradient(red, darkRed, 20)
        }
        val footerParams = matchWrap()
        footerParams.topMargin = dp(5)
        page.addView(footer, footerParams)

        scroll.addView(page)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private data class MenuItem(
        val icon: String,
        val label: String,
        val color: Int,
        val action: () -> Unit
    )

    private fun createMenuTile(item: MenuItem): View {
        val tile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(5), dp(10), dp(5), dp(8))
            background = rounded(Color.WHITE, 18)
            elevation = dp(2).toFloat()
            isClickable = true
            isFocusable = true
            setOnClickListener { item.action() }
        }
        val icon = TextView(this).apply {
            text = item.icon
            textSize = 35f
            gravity = Gravity.CENTER
            setTextColor(item.color)
        }
        tile.addView(icon, LinearLayout.LayoutParams(-1, dp(62)))
        tile.addView(TextView(this).apply {
            text = item.label
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(ink)
            setTypeface(null, Typeface.BOLD)
            maxLines = 2
        }, LinearLayout.LayoutParams(-1, -2))
        return tile
    }

    private fun openModule(module: String) {
        startActivity(Intent(this, FeatureActivity::class.java).putExtra("module", module))
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun roundedGradient(start: Int, end: Int, radius: Int) =
        GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(start, end)).apply {
            cornerRadius = dp(radius).toFloat()
        }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
