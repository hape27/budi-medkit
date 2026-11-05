package com.budimedkit.app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "BudiMedkit.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS pelanggan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                telepon TEXT,
                alamat TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS kendaraan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pelanggan_id INTEGER,
                merk TEXT,
                nomor_polisi TEXT,
                tipe TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS transaksi (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal TEXT,
                jenis TEXT,
                keterangan TEXT,
                jumlah REAL
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS servis (
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
            CREATE TABLE IF NOT EXISTS sparepart (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT,
                stok INTEGER,
                harga REAL
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS mekanik (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                telepon TEXT,
                keahlian TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS gaji (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal TEXT,
                mekanik TEXT,
                keterangan TEXT,
                jumlah REAL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS mekanik (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama TEXT NOT NULL,
                    telepon TEXT,
                    keahlian TEXT
                )
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS gaji (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tanggal TEXT,
                    mekanik TEXT,
                    keterangan TEXT,
                    jumlah REAL
                )
            """)
        }
    }

    fun tambahTransaksi(tanggal: String, jenis: String, keterangan: String, jumlah: Double): Boolean {
        val values = ContentValues().apply {
            put("tanggal", tanggal)
            put("jenis", jenis)
            put("keterangan", keterangan)
            put("jumlah", jumlah)
        }
        return writableDatabase.insert("transaksi", null, values) != -1L
    }

    fun totalTransaksi(jenis: String): Double {
        val cursor = readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(jumlah), 0) FROM transaksi WHERE jenis = ?",
            arrayOf(jenis)
        )
        val total = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close()
        return total
    }

    fun jumlahBaris(tabel: String): Int {
        val aman = setOf("pelanggan", "kendaraan", "transaksi", "servis", "sparepart", "mekanik", "gaji")
        if (tabel !in aman) return 0
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $tabel", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }
}
