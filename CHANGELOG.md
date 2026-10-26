# Changelog

Semua perubahan penting pada proyek ini dicatat di berkas ini.

Format mengikuti [Keep a Changelog](https://keepachangelog.com/id-ID/1.1.0/), dan penomoran versi mengikuti [Semantic Versioning](https://semver.org/lang/id/).

## [Unreleased]

## [1.0.0] - 2026-10-26

Rilis pertama.

### Added

- Membaca Al-Qur'an per surah dan per juz: 114 surah, 30 juz, 6.236 ayat, dengan teks Arab, transliterasi, dan terjemahan Indonesia. Data tersimpan di database lokal sehingga bisa dibaca tanpa internet.
- Catatan kaki pada terjemahan berupa angka kecil bertautan; diketuk untuk membuka dialog penjelasannya.
- Salin dan bagikan ayat, tanpa penanda dan isi catatan kaki.
- Kartu bacaan terakhir di beranda untuk kembali ke ayat yang terakhir ditandai.
- Markah ayat dengan kategori: tambah, ubah nama, dan hapus kategori, serta tambah dan hapus markah. Disertai pesan konfirmasi dan pesan galat yang jelas.
- Tombol informasi di beranda yang menjelaskan bahwa markah hanya tersimpan di perangkat dan hilang jika aplikasi dihapus atau datanya dibersihkan.
- Pencarian surah berdasarkan nama dan pencarian ayat berdasarkan kata di transliterasi dan terjemahan, dengan hasil dikelompokkan per surah, pemuatan per surah, dan teks yang disorot.
- Pengaturan tema: terang, gelap, dan mengikuti sistem, dengan tombol *Terapkan* dan *Batal*. Pilihan tersimpan di perangkat.
- Layar **Sumber data** di Pengaturan: asal data Al-Qur'an dan atribusinya. Versi aplikasi juga ditampilkan di Pengaturan.
- Tombol **Kirim masukan** di Pengaturan yang membuka Gmail (atau aplikasi surel lain bila Gmail tidak ada) dengan alamat, subjek, dan keterangan versi aplikasi serta perangkat sudah terisi.
- Tampilan: bilah navigasi bawah dan tombol aksi berefek kaca (*glassmorphism*), dialog formulir dan konfirmasi dengan kolom yang tidak bisa diubah dibedakan dari kolom yang bisa diisi, kartu kategori dengan ikon ubah dan hapus, orientasi potret, serta tampilan sampai ke tepi layar di semua versi Android (bilah atas menyesuaikan bilah status, warna ikon status mengikuti warna bilah atas, dan daftar tidak tertutup bilah navigasi sistem).
- Ikon aplikasi adaptif beserta lapisan monokrom untuk ikon bertema Android 13.
- Unit test dan Compose UI test untuk semua modul, dijalankan di komputer lewat Robolectric tanpa emulator. Total 378 test (230 unit test dan 148 Compose UI test), semuanya lolos. Cara kerjanya dijelaskan di [docs/TESTING.md](docs/TESTING.md). Agar layar bisa dites, `HomeScreen`, `SurahDetailScreen`, `JuzDetailScreen`, `BookmarkableVerseList`, dan `BottomNavigationScreen` punya parameter opsional untuk ViewModel atau grafik navigasi; nilai bawaannya sama dengan sebelumnya (`hiltViewModel()` dan grafik aplikasi), sehingga perilaku aplikasi tidak berubah.
- Pengukuran cakupan dengan JaCoCo (`coverageReport` dan `coverageCheck`, ambang 80% baris per modul). Cakupan baris per modul: `core` 97,5%, `ui-kit` 95,2%, `feature-search` 96,6%, `feature-bookmark` 91,6%, `feature-quran` 89,1%, `feature-bottomNavigation` 88,8%, `feature-theme` 86,0%, `app` 100%; total 92,2%.
- Dokumentasi dan lisensi: `README.md`, `CHANGELOG.md`, dan `LICENSE` (MIT, hanya untuk kode sumber).

<!--
Panduan menulis catatan:
- Added      : fitur baru.
- Changed    : perubahan pada fitur yang sudah ada.
- Deprecated : fitur yang akan dihapus.
- Removed    : fitur yang dihapus.
- Fixed      : perbaikan bug.
- Security   : perbaikan kerentanan.

Saat merilis, pindahkan isi "Unreleased" ke bagian baru berisi nomor versi dan
tanggal (YYYY-MM-DD), lalu biarkan "Unreleased" kosong di atasnya, misalnya:

## [Unreleased]

## [1.0.1] - 2026-11-15

### Fixed
- ...
-->
