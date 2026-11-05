# BUDI MEDKIT Web Preview

Aplikasi pembukuan lokal untuk Budi Jaya Motor.

## Fitur prototipe
- Dashboard pemasukan, pengeluaran, dan saldo bersih.
- Pencatatan transaksi dengan tanggal dan waktu.
- Kategori pemasukan/pengeluaran yang dapat ditambah.
- Analisis omzet per bulan, waktu pemasukan berdasarkan jam, hari, dan kategori.
- Laporan bulanan dan cetak/simpan PDF.
- Nota manual dengan pilihan cetak saja atau catat sebagai pemasukan.
- Ekspor dan pemulihan backup JSON.
- PWA/service worker untuk cache offline setelah aplikasi dibuka dari hosting HTTPS.

## Penyimpanan dan privasi
Data prototipe disimpan pada localStorage browser di perangkat yang digunakan. Data tidak dikirim ke server. Data web dan APK tidak otomatis tersinkron; gunakan ekspor/impor backup untuk memindahkan data. Jangan menghapus data browser sebelum membuat backup.

## Menjalankan
Buka `index.html` melalui web server lokal atau hosting HTTPS. Service worker dan pemasangan PWA tidak aktif jika file dibuka langsung dengan skema `file://`.

## Status
Ini adalah prototipe awal untuk pengujian antarmuka dan alur. Belum dinyatakan siap produksi. Sebelum dipakai untuk pembukuan nyata, perlu pengujian menyeluruh, validasi backup/restore, pengamanan akses pemilik, dan pembungkusan ke APK Android.
