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

    private val navy = Color.rgb(17, 30, 58)
    private val blue = Color.rgb(31, 76, 130)
    private val orange = Color.rgb(245, 139, 40)
    private val bg = Color.rgb(244, 247, 251)
    private val ink = Color.rgb(32, 43, 58)
    private val muted = Color.rgb(116, 128, 145)
    private var incomeValue: TextView? = null
    private var expenseValue: TextView? = null
    private var balanceView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseHelper(this)
        val masuk = db.totalTransaksi("PEMASUKAN")
        val keluar = db.totalTransaksi("PENGELUARAN")
        val saldo = masuk - keluar

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            setBackgroundColor(bg)
        }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }
        scroll.addView(page)

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(24))
            background = roundedGradient(navy, blue, 24)
        }
        header.addView(TextView(this).apply {
            text = "BUDI JAYA MOTOR"
            textSize = 12f
            letterSpacing = 0.12f
            setTextColor(Color.rgb(210, 224, 244))
            setTypeface(null, Typeface.BOLD)
        })
        header.addView(TextView(this).apply {
            text = "BUDI MEDKIT"
            textSize = 27f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(7), 0, 0)
        })
        header.addView(TextView(this).apply {
            text = "Sistem Manajemen Bengkel"
            textSize = 14f
            setTextColor(Color.rgb(225, 234, 246))
            setPadding(0, dp(3), 0, 0)
        })
        page.addView(header, matchWrap())

        addSectionTitle(page, "Ringkasan Keuangan", "Ikhtisar transaksi bengkel")
        val finance = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(15), dp(17), dp(15))
            background = rounded(Color.WHITE, 20)
            elevation = dp(2).toFloat()
        }
        addMoneyRow(finance, "Total pemasukan", masuk, Color.rgb(27, 145, 105))
        addThinDivider(finance)
        addMoneyRow(finance, "Total pengeluaran", keluar, orange)
        addThinDivider(finance)
        balanceView = TextView(this).apply {
            text = "SALDO SAAT INI\n${rupiah(saldo)}"
            textSize = 18f
            setTextColor(navy)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(13), 0, dp(2))
        }
        finance.addView(balanceView)
        page.addView(finance, matchWrap())

        addSectionTitle(page, "Menu Utama", "Pilih layanan yang ingin dibuka")
        addMenuCard(page, "💳", "Transaksi", "Pemasukan dan pengeluaran", true) {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }
        addMenuCard(page, "🔧", "Servis Kendaraan", "Pencatatan pekerjaan servis", true) {
            openModule("servis")
        }
        addMenuCard(page, "👥", "Data Pelanggan", "Kelola informasi pelanggan", true) {
            openModule("pelanggan")
        }
        addMenuCard(page, "🏍", "Data Kendaraan", "Data motor dan kendaraan", true) {
            openModule("kendaraan")
        }
        addMenuCard(page, "🧰", "Stok Sparepart", "Persediaan suku cadang", true) {
            openModule("sparepart")
        }
        addMenuCard(page, "🧑‍🔧", "Data Mekanik", "Kelola data mekanik", true) {
            openModule("mekanik")
        }
        addMenuCard(page, "👨‍🔧", "Gaji Mekanik", "Catatan upah mekanik", true) {
            openModule("gaji")
        }
        addMenuCard(page, "📊", "Laporan Keuangan", "Rekap transaksi bengkel", true) {
            openModule("laporan")
        }

        page.addView(TextView(this).apply {
            text = "BUDI MEDKIT  •  BUDI JAYA MOTOR"
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(muted)
            setPadding(0, dp(22), 0, 0)
        })
        setContentView(scroll)
    }

    override fun onResume() {
        super.onResume()
        if (incomeValue != null) {
            val db = DatabaseHelper(this)
            val masuk = db.totalTransaksi("PEMASUKAN")
            val keluar = db.totalTransaksi("PENGELUARAN")
            incomeValue?.text = rupiah(masuk)
            expenseValue?.text = rupiah(keluar)
            balanceView?.text = "SALDO SAAT INI\n${rupiah(masuk - keluar)}"
        }
    }

    private fun openModule(module: String) {
        startActivity(Intent(this, FeatureActivity::class.java).putExtra("module", module))
    }

    private fun addSectionTitle(parent: LinearLayout, title: String, subtitle: String) {
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(2), dp(22), dp(2), dp(11))
        }
        wrap.addView(TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(ink)
            setTypeface(null, Typeface.BOLD)
        })
        wrap.addView(TextView(this).apply {
            text = subtitle
            textSize = 12f
            setTextColor(muted)
            setPadding(0, dp(3), 0, 0)
        })
        parent.addView(wrap)
    }

    private fun addMoneyRow(parent: LinearLayout, label: String, amount: Double, color: Int) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(7), 0, dp(7))
        }
        row.addView(TextView(this).apply {
            text = label
            textSize = 13f
            setTextColor(muted)
        }, LinearLayout.LayoutParams(0, -2, 1f))
        val amountView = TextView(this).apply {
            text = rupiah(amount)
            textSize = 15f
            setTextColor(color)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.END
        }
        if (label == "Total pemasukan") incomeValue = amountView
        if (label == "Total pengeluaran") expenseValue = amountView
        row.addView(amountView)
        parent.addView(row)
    }

    private fun addThinDivider(parent: LinearLayout) {
        parent.addView(View(this).apply {
            setBackgroundColor(Color.rgb(235, 239, 244))
        }, LinearLayout.LayoutParams(-1, dp(1)))
    }

    private fun addMenuCard(
        parent: LinearLayout,
        symbol: String,
        title: String,
        detail: String,
        active: Boolean,
        action: (() -> Unit)? = null
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(14), dp(13), dp(14))
            background = rounded(Color.WHITE, 17)
            elevation = dp(1).toFloat()
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (action != null) action()
                else Toast.makeText(this@MainActivity, "$title belum tersedia", Toast.LENGTH_SHORT).show()
            }
        }
        card.addView(TextView(this).apply {
            text = symbol
            textSize = 23f
            gravity = Gravity.CENTER
            background = rounded(if (active) Color.rgb(255, 241, 224) else Color.rgb(239, 244, 250), 13)
        }, LinearLayout.LayoutParams(dp(49), dp(49)))

        val texts = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), 0, dp(5), 0)
        }
        texts.addView(TextView(this).apply {
            text = title
            textSize = 15f
            setTextColor(ink)
            setTypeface(null, Typeface.BOLD)
        })
        texts.addView(TextView(this).apply {
            text = detail
            textSize = 11f
            setTextColor(muted)
            setPadding(0, dp(3), 0, 0)
        })
        card.addView(texts, LinearLayout.LayoutParams(0, -2, 1f))
        card.addView(TextView(this).apply {
            text = "›"
            textSize = 27f
            setTextColor(if (active) orange else muted)
        })
        val params = matchWrap()
        params.bottomMargin = dp(10)
        parent.addView(card, params)
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun roundedGradient(start: Int, end: Int, radius: Int): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(start, end)).apply {
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun rupiah(value: Double): String =
        "Rp " + String.format("%,.0f", value).replace(',', '.')
}