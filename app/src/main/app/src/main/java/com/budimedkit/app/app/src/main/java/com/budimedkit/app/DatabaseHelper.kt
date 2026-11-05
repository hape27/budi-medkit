
package com.budimedkit.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "BudiMedkit.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE pelanggan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                telepon TEXT,
                alamat TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE kendaraan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pelanggan_id INTEGER,
                merk TEXT,
                nomor_polisi TEXT,
                tipe TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE transaksi (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal TEXT,
                jenis TEXT,
                keterangan TEXT,
                jumlah REAL
            )
        """)

        db.execSQL("""
            CREATE TABLE servis (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal TEXT,
                pelanggan TEXT,
                kendaraan TEXT,
                keluhan TEXT,
                biaya REAL,
                status TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE sparepart (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT,
                stok INTEGER,
                harga REAL
            )
        """)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Database dipertahankan untuk pembaruan berikutnya.
    }

    fun tambahTransaksi(
        tanggal: String,
        jenis: String,
        keterangan: String,
        jumlah: Double
    ): Boolean {

        val db = writableDatabase
        val values = ContentValues().apply {
            put("tanggal", tanggal)
            put("jenis", jenis)
            put("keterangan", keterangan)
            put("jumlah", jumlah)
        }

        return db.insert("transaksi", null, values) != -1L
    }

    fun totalTransaksi(jenis: String): Double {

        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(jumlah) FROM transaksi WHERE jenis = ?",
            arrayOf(jenis)
        )

        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }
    }
    
