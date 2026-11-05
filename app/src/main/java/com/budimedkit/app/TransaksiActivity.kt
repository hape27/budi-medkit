package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaksiActivity : Activity() {
    private val navy = Color.rgb(17, 30, 58)
    private val blue = Color.rgb(31, 76, 130)
    private val orange = Color.rgb(245, 139, 40)
    private val muted = Color.rgb(116, 128, 145)
    private lateinit var database: DatabaseHelper
    private lateinit var jenis: Spinner
    private lateinit var keterangan: EditText
    private lateinit var jumlah: EditText
    private lateinit var hasil: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = DatabaseHelper(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(244, 247, 251))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(18), dp(14))
            background = gradient()
        }
        val back = TextView(this).apply {
            text = "‹"
            textSize = 34f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setOnClickListener { finish() }
        }
        header.addView(back, LinearLayout.LayoutParams(dp(42), dp(48)))
        val heading = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        heading.addView(TextView(this).apply {
            text = "BUDI MEDKIT"
            textSize = 11f
            setTextColor(Color.rgb(216, 229, 245))
            setTypeface(null, Typeface.BOLD)
        })
        heading.addView(TextView(this).apply {
            text = "Transaksi Keuangan"
            textSize = 21f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        })
        header.addView(heading)
        root.addView(header)

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(24))
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(20))
            background = rounded(Color.WHITE, 20)
            elevation = dp(2).toFloat()
        }
        form.addView(TextView(this).apply {
            text = "Catat transaksi baru"
            textSize = 18f
            setTextColor(navy)
            setTypeface(null, Typeface.BOLD)
        })
        form.addView(label("Jenis transaksi"))
        jenis = Spinner(this).apply {
            adapter = ArrayAdapter(this@TransaksiActivity, android.R.layout.simple_spinner_dropdown_item, arrayOf("PEMASUKAN", "PENGELUARAN"))
        }
        form.addView(jenis, fieldParams())
        form.addView(label("Keterangan"))
        keterangan = EditText(this).apply {
            hint = "Contoh: Servis ganti oli"
            textSize = 14f
            setSingleLine(true)
            setPadding(dp(12), 0, dp(12), 0)
            background = rounded(Color.rgb(246, 248, 251), 12)
        }
        form.addView(keterangan, fieldParams())
        form.addView(label("Jumlah uang (Rp)"))
        jumlah = EditText(this).apply {
            hint = "Contoh: 50000"
            textSize = 14f
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
            setPadding(dp(12), 0, dp(12), 0)
            background = rounded(Color.rgb(246, 248, 251), 12)
        }
        form.addView(jumlah, fieldParams())

        val save = TextView(this).apply {
            text = "SIMPAN TRANSAKSI"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = rounded(orange, 13)
            isClickable = true
            isFocusable = true
            setOnClickListener { simpanTransaksi() }
        }
        form.addView(save, LinearLayout.LayoutParams(-1, dp(52)).apply { topMargin = dp(18) })
        content.addView(form, LinearLayout.LayoutParams(-1, -2))

        val summary = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = rounded(Color.WHITE, 20)
            elevation = dp(1).toFloat()
        }
        summary.addView(TextView(this).apply {
            text = "Ringkasan keuangan"
            textSize = 17f
            setTextColor(navy)
            setTypeface(null, Typeface.BOLD)
        })
        hasil = TextView(this).apply {
            textSize = 14f
            setTextColor(muted)
            setLineSpacing(dp(7).toFloat(), 1f)
            setPadding(0, dp(12), 0, 0)
        }
        summary.addView(hasil)
        content.addView(summary, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(14) })
        setContentView(root)
        tampilkanSaldo()
    }

    private fun simpanTransaksi() {
        val nominal = jumlah.text.toString().trim().toDoubleOrNull()
        val catatan = keterangan.text.toString().trim()
        if (catatan.isBlank() || nominal == null || nominal <= 0) {
            Toast.makeText(this, "Lengkapi keterangan dan jumlah uang yang benar", Toast.LENGTH_SHORT).show()
            return
        }
        val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val berhasil = database.tambahTransaksi(tanggal, jenis.selectedItem.toString(), catatan, nominal)
        if (berhasil) {
            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
            keterangan.text.clear()
            jumlah.text.clear()
            tampilkanSaldo()
        } else {
            Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tampilkanSaldo() {
        val masuk = database.totalTransaksi("PEMASUKAN")
        val keluar = database.totalTransaksi("PENGELUARAN")
        hasil.text = "TOTAL PEMASUKAN     " + money(masuk) +
            "\n\nTOTAL PENGELUARAN  " + money(keluar) +
            "\n\nSALDO SAAT INI          " + money(masuk - keluar)
    }

    private fun label(value: String) = TextView(this).apply {
        text = value
        textSize = 13f
        setTextColor(navy)
        setTypeface(null, Typeface.BOLD)
        val p = LinearLayout.LayoutParams(-1, -2)
        p.topMargin = dp(15)
        layoutParams = p
    }

    private fun fieldParams() = LinearLayout.LayoutParams(-1, dp(50)).apply { topMargin = dp(7) }
    private fun money(value: Double) = "Rp " + String.format("%,.0f", value).replace(',', '.')
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }
    private fun gradient() = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(navy, blue))
}