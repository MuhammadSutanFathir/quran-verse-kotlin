# Pengujian

Proyek ini punya dua jenis test, dan **keduanya berjalan di komputer (JVM) lewat satu perintah, tanpa emulator atau ponsel**:

| Jenis | Yang diuji | Alat |
|---|---|---|
| **Unit test** | Logika tanpa tampilan: use case, repository, data source, mapper, ViewModel, pemformat, rute, dan model | JUnit 4, kotlinx-coroutines-test |
| **Compose UI test** | Tampilan: komponen, dialog, dan layar utuh (isi layar, klik, ketik, alur antarlayar) | Compose UI Test + Robolectric |

Keduanya berada di `src/test/` tiap modul. Folder `src/androidTest/` tidak dipakai.

Kembali ke [README](../README.md).

## Menjalankan test

Semua test di semua modul:

```powershell
.\gradlew.bat testDebugUnitTest
```

Satu modul, misalnya `feature-search`:

```powershell
.\gradlew.bat :feature-search:testDebugUnitTest
```

Laporan hasil test ada di `<modul>/build/reports/tests/testDebugUnitTest/index.html`. Pada penjalanan pertama, Robolectric mengunduh Android tiruan (sekitar 100 MB), jadi butuh koneksi internet.

## Unit test

- **Fake buatan sendiri, tanpa pustaka mocking.** Repository dan DAO diganti kelas palsu kecil (`FakeBookmarkDao`, `FakeSearchRepository`, dan seterusnya). Jadi test memeriksa perilaku, bukan cara kode memanggil dependensinya.
- **Coroutine** dites dengan `runTest`. Untuk ViewModel, `MainDispatcherRule` mengganti `Dispatchers.Main` dengan dispatcher uji, sehingga `viewModelScope` berjalan seketika.
- **Yang dicakup per lapisan:**
  - *Domain:* use case meneruskan argumen dan hasil (termasuk kegagalan), entity, dan failure.
  - *Data:* data source membungkus galat menjadi exception penanda dan melempar ulang `CancellationException`; repository memetakan hasil menjadi `Either<Failure, T>`; mapper; model.
  - *Presentation:* ViewModel (state `Initial`, `Loading`, `Success`, `Error` dan peristiwa sekali jalan), pesan galat, fungsi teks, dan pembuat rute.
- `SharedPreferences` (pilihan tema) dan pembuka Gmail dites dengan Android tiruan dari Robolectric, tanpa ponsel.

## Compose UI test

- Memakai API test Compose biasa (`createComposeRule`, `onNodeWithText`, `performClick`, dan seterusnya), dijalankan oleh **Robolectric** di JVM. Setiap kelas diberi:

```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SearchScreenTest { ... }
```

- Versi Android tiruan dan ukuran layarnya diatur di `src/test/resources/robolectric.properties` (`sdk=35`, layar 411 x 891 dp).
- **Layar diuji dengan ViewModel palsu.** ViewModel dibuat langsung di test dengan repository palsu, lalu diberikan lewat parameter. Beberapa layar punya parameter opsional untuk itu (`HomeScreen`, `SurahDetailScreen`, `JuzDetailScreen`, `BookmarkableVerseList`, `BottomNavigationScreen`). **Nilai bawaannya tetap `hiltViewModel()`**, jadi perilaku aplikasi tidak berubah, dan test tidak membutuhkan Hilt.
- **Modul `app`** menjalankan **aplikasi sungguhan** (graf Hilt, Room, dan `quran.db`) sebagai uji asap: daftar surah muncul dari database, tombol Cari, Pengaturan, dan tab Markah membuka layarnya, dan membuka satu surah menyembunyikan bilah bawah. Test ini membutuhkan `quran.db` di `core/src/main/assets/database/` (lihat [Sumber Data dan Hak Cipta](../README.md#sumber-data-dan-hak-cipta)). Kelas `AppRobolectricRunner` mencegah Robolectric mengubah dua kelas Android milik `app` agar cakupannya terbaca.

## Cakupan kode

Cakupan diukur dengan **JaCoCo**, gabungan unit test dan Compose UI test. Konfigurasinya ada di [`gradle/coverage.gradle.kts`](../gradle/coverage.gradle.kts), dan setiap modul menerapkannya. Ambang yang dipakai: **cakupan baris minimal 80% per modul**. Hasil pengukuran dicatat di [CHANGELOG.md](../CHANGELOG.md).

```powershell
.\gradlew.bat :feature-search:coverageCheck
```

Perintah itu menjalankan test modul, menyusun laporan, dan **gagal bila cakupan baris di bawah 80%**. Laporan HTML ada di `<modul>/build/reports/jacoco/coverageReport/html/index.html` (berkas yang belum tercakup diberi tanda merah). Untuk laporan saja, tanpa pemeriksaan ambang, pakai `:<modul>:coverageReport`.

Kode buatan otomatis tidak dihitung: `R`, `BuildConfig`, `*_Impl` (Room), serta kelas Hilt dan Dagger.

## Menambah test

1. Letakkan berkas di `src/test/java/...` dengan paket yang sama seperti kode yang diuji.
2. Pakai fake yang sudah ada di berkas `Fakes.kt` modul itu, atau tambahkan fake baru.
3. Untuk layar baru yang memanggil `hiltViewModel()` di dalamnya, tambahkan parameter ViewModel opsional bernilai bawaan `hiltViewModel()`, lalu isi dengan ViewModel palsu di test.
4. Jalankan `coverageCheck` modulnya.
