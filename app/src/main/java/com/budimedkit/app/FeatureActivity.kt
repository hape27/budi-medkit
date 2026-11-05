package com.budimedkit.app

import android.app.Activity
import android.os.Bundle
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.*
import android.database.Cursor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class FormField(val key: String, val label: String, val numeric: Boolean = false)

class FeatureActivity : Activity() {
    private val navy = Color.rgb(17, 30, 58)
    private val blue = Color.rgb(31, 76, 130)
    private val orange = Color.rgb(245, 139, 40)
    private val ink = Color.rgb(32, 43, 58)
    private val muted = Color.rgb(116, 128, 145)
    private lateinit var db: DatabaseHelper
    private lateinit var module: String
    private lateinit var list: LinearLayout

    private val fields: List<FormField>
        get() = when (module) {
            "pelanggan" -> listOf(FormField("nama", "Nama pelanggan"), FormField("telepon", "Nomor telepon"), FormField("alamat", "Alamat"))
            "kendaraan" -> listOf(FormField("pelanggan_id", "ID pelanggan (lihat Data Pelanggan)", true), FormField("merk", "Merek kendaraan"), FormField("nomor_polisi", "Nomor polisi"), FormField("tipe", "Tipe / model"))
            "servis" -> listOf(FormField("pelanggan", "Nama pelanggan"), FormField("kendaraan", "Kendaraan / nomor polisi"), FormField("keluhan", "Keluhan / pekerjaan"), FormField("biaya", "Biaya servis", true), FormField("status", "Status (Masuk / Proses / Selesai)"))
            "sparepart" -> listOf(FormField("nama", "Nama sparepart"), FormField("stok", "Jumlah stok", true), FormField("harga", "Harga satuan", true))
            "mekanik" -> listOf(FormField("nama", "Nama mekanik"), FormField("telepon", "Nomor telepon"), FormField("keahlian", "Keahlian"))
            else -> listOf(FormField("tanggal", "Tanggal (YYYY-MM-DD)"), FormField("mekanik", "Nama mekanik"), FormField("keterangan", "Keterangan gaji"), FormField("jumlah", "Jumlah gaji", true))
        }

    private val table: String
        get() = module

    private val title: String
        get() = when (module) {
            "pelanggan" -> "Data Pelanggan"
            "kendaraan" -> "Data Kendaraan"
            "servis" -> "Servis Kendaraan"
            "sparepart" -> "Stok Sparepart"
            "mekanik" -> "Data Mekanik"
            else -> "Gaji Mekanik"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)
        module = intent.getStringExtra("module") ?: "pelanggan"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setBackgroundColor(Color.rgb(244, 247, 251))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = gradient()
        }
        header.addView(TextView(this).apply {
            text = "BUDI MEDKIT"
            textSize = 12f
            setTextColor(Color.rgb(218, 229, 244))
            setTypeface(null, Typeface.BOLD)
        })
        header.addView(TextView(this).apply {
            text = title
            textSize = 23f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(5), 0, 0)
        })
        root.addView(header, LinearLayout.LayoutParams(-1, -2))

        val add = Button(this).apply {
            text = "+ Tambah Data"
            setTextColor(Color.WHITE)
            background = rounded(orange, 14)
            setOnClickListener { showForm(null, emptyMap()) }
        }
        val addParams = LinearLayout.LayoutParams(-1, dp(52))
        addParams.topMargin = dp(14)
        root.addView(add, addParams)

        val scroll = ScrollView(this)
        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(14), 0, dp(20))
        }
        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        refreshList()
    }

    private fun refreshList() {
        list.removeAllViews()
        val columns = fields.joinToString(",") { it.key }
        val cursor = db.readableDatabase.rawQuery("SELECT id, $columns FROM $table ORDER BY id DESC", null)
        if (cursor.count == 0) {
            list.addView(TextView(this).apply {
                text = "Belum ada data. Tekan Tambah Data untuk mulai mencatat."
                textSize = 14f
                setTextColor(muted)
                gravity = Gravity.CENTER
                setPadding(dp(20), dp(35), dp(20), dp(35))
                background = rounded(Color.WHITE, 16)
            })
        } else {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val values = linkedMapOf<String, String>()
                fields.forEachIndexed { index, field -> values[field.key] = cursor.getString(index + 1) ?: "" }
                val summary = values.values.filter { it.isNotBlank() }.take(3).joinToString("\n")
                val card = TextView(this).apply {
                    text = summary.ifBlank { "Data #$id" } + "\n\nKetuk untuk edit • Tekan lama untuk hapus"
                    textSize = 14f
                    setTextColor(ink)
                    setPadding(dp(16), dp(15), dp(16), dp(15))
                    background = rounded(Color.WHITE, 16)
                    elevation = dp(1).toFloat()
                    setOnClickListener { showForm(id, values) }
                    setOnLongClickListener {
                        confirmDelete(id)
                        true
                    }
                }
                val params = LinearLayout.LayoutParams(-1, -2)
                params.bottomMargin = dp(10)
                list.addView(card, params)
            }
        }
        cursor.close()
    }

    private fun showForm(id: Long?, old: Map<String, String>) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(4))
        }
        val inputs = linkedMapOf<FormField, EditText>()
        fields.forEach { field ->
            val input = EditText(this).apply {
                hint = field.label
                textSize = 15f
                setSingleLine(false)
                if (field.numeric) inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                setText(old[field.key] ?: if (field.key == "tanggal") today() else "")
            }
            box.addView(input, LinearLayout.LayoutParams(-1, dp(52)))
            inputs[field] = input
        }
        val scroll = ScrollView(this).apply { addView(box) }
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(if (id == null) "Tambah $title" else "Edit $title")
            .setView(scroll)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val first = inputs.values.firstOrNull()?.text?.toString()?.trim().orEmpty()
                if (first.isBlank()) {
                    Toast.makeText(this, "Lengkapi data utama terlebih dahulu", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val values = ContentValues()
                fields.forEach { field ->
                    val raw = inputs[field]?.text?.toString()?.trim().orEmpty()
                    if (field.numeric) values.put(field.key, raw.toDoubleOrNull() ?: 0.0)
                    else values.put(field.key, raw)
                }
                val result = if (id == null) {
                    db.writableDatabase.insert(table, null, values)
                } else {
                    db.writableDatabase.update(table, values, "id=?", arrayOf(id.toString())).toLong()
                }
                if (result != -1L) {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    refreshList()
                } else {
                    Toast.makeText(this, "Data gagal disimpan", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun confirmDelete(id: Long) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Hapus data?")
            .setMessage("Data ini akan dihapus dari perangkat.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                db.writableDatabase.delete(table, "id=?", arrayOf(id.toString()))
                refreshList()
                Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun today() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }
    private fun gradient() = GradientDrawable(
        GradientDrawable.Orientation.TL_BR, intArrayOf(navy, blue)
    ).apply { cornerRadius = dp(20).toFloat() }
}
