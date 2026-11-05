# BUDI MEDKIT

Aplikasi pembukuan lokal untuk Budi Jaya Motor. Versi web dan APK menggunakan antarmuka serta alur pencatatan yang sama.

## Fitur yang tersedia pada rancangan saat ini
- Dashboard pemasukan, pengeluaran, dan saldo bersih.
- Pencatatan transaksi dengan tanggal dan waktu lokal perangkat.
- Riwayat transaksi dengan filter, ubah, dan hapus transaksi manual.
- Kategori pemasukan/pengeluaran: tambah, ubah, dan hapus jika belum dipakai.
- Analisis omzet per bulan, waktu pemasukan berdasarkan jam, hari, dan kategori.
- Laporan bulanan dan cetak/simpan PDF.
- Nota manual dengan item, kuantitas, harga, diskon, pelanggan, nomor nota, dan riwayat cetak ulang.
- Pilihan nota: cetak saja atau catat sebagai pemasukan. Pemasukan nota ditautkan ke nomor nota agar cetak ulang tidak menambah transaksi lagi.
- Backup dan pemulihan data JSON.
- PIN pemilik 4–6 angka untuk mengunci tampilan aplikasi.
- PWA/service worker untuk cache offline setelah aplikasi dibuka dari hosting HTTPS.

## Penyimpanan dan privasi
Data disimpan lokal pada browser atau penyimpanan WebView di perangkat yang digunakan. Tidak ada server atau sinkronisasi otomatis. Data web dan APK terpisah; gunakan ekspor/impor backup untuk memindahkan data. Jangan menghapus data aplikasi/browser sebelum membuat backup.

PIN adalah pengunci layar lokal, bukan enkripsi database atau perlindungan terhadap orang yang memiliki akses teknis ke data perangkat. Gunakan kunci layar Android dan simpan file backup di tempat aman.

## Menjalankan versi web
Buka `index.html` melalui web server lokal atau hosting HTTPS. Service worker dan pemasangan PWA tidak aktif jika file dibuka langsung dengan skema `file://`.

## APK Android
Antarmuka web dibundel di `app/src/main/assets/index.html` dan dijalankan melalui Android WebView. Android menyediakan dialog cetak nota dan pemilih file untuk pemulihan backup. APK dibangun melalui GitHub Actions workflow `.github/workflows/build-apk.yml`.

## Status pengujian
Sintaks JavaScript telah diperiksa. Build APK dan pengujian langsung di perangkat Android, termasuk cetak nota, pemulihan file, offline, dan ketahanan data, tetap harus lulus sebelum aplikasi dinyatakan final/siap dipakai untuk pembukuan penting.
