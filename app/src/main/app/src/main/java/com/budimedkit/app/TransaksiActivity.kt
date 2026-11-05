
package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaksiActivity : Activity() {

    private lateinit var database: DatabaseHelper
    private lateinit var jenis: Spinner
    private lateinit var keterangan: EditText
    private lateinit var jumlah: EditText
    private lateinit var hasil: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = DatabaseHelper(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 30, 24, 24)
        layout.setBackgroundColor(Color.WHITE)

        val judul = TextView(this)
        judul.text = "TRANSAKSI BENGKEL"
        judul.textSize = 24f
        judul.setTextColor(Color.rgb(20, 35, 65))
        judul.setGravity(Gravity.CENTER)
        layout.addView(judul)

        jenis = Spinner(this)
        val pilihan = arrayOf("PEMASUKAN", "PENGELUARAN")
        jenis.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            pilihan
        )
        layout.addView(jenis)

        keterangan = EditText(this)
        keterangan.hint = "Keterangan transaksi"
        layout.addView(keterangan)

        jumlah = EditText(this)
        jumlah.hint = "Jumlah uang (contoh: 50000)"
        jumlah.inputType = 2
        layout.addView(jumlah)

        val tombol = Button(this)
        tombol.text = "SIMPAN TRANSAKSI"
        layout.addView(tombol)

        hasil = TextView(this)
        hasil.textSize = 16f
        hasil.setPadding(0, 25, 0, 0)
        layout.addView(hasil)

        tombol.setOnClickListener {
            simpanTransaksi()
        }

        setContentView(layout)
    }

    private fun simpanTransaksi() {
        val nominal = jumlah.text.toString().toDoubleOrNull()

        if (keterangan.text.isBlank() || nominal == null || nominal <= 0) {
            Toast.makeText(
                this,
                "Lengkapi keterangan dan jumlah uang",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val tanggal = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

        val berhasil = database.tambahTransaksi(
            tanggal,
            jenis.selectedItem.toString(),
            keterangan.text.toString(),
            nominal
        )

        if (berhasil) {
            Toast.makeText(
                this,
                "Transaksi berhasil disimpan",
                Toast.LENGTH_SHORT
            ).show()

            keterangan.text.clear()
            jumlah.text.clear()
            tampilkanSaldo()
        } else {
            Toast.makeText(
                this,
                "Gagal menyimpan transaksi",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun tampilkanSaldo() {
        val masuk = database.totalTransaksi("PEMASUKAN")
        val keluar = database.totalTransaksi("PENGELUARAN")

        hasil.text = """
            TOTAL PEMASUKAN: Rp ${masuk.toLong()}
            TOTAL PENGELUARAN: Rp ${keluar.toLong()}
            SALDO: Rp ${(masuk - keluar).toLong()}
        """.trimIndent()
    }
}
