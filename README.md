# SIMPUS - Sistem Informasi Manajemen Perpustakaan Sederhana

Proyek praktikum Bab 6 Pemrograman Berorientasi Objek dengan Kotlin. Struktur dan nama file mengikuti susunan proyek pada modul:

```text
simpus/
├── build.gradle.kts
├── settings.gradle.kts
├── Dockerfile
├── docker-compose.yml
├── .gitignore
├── .dockerignore
├── README.md
└── src/
    ├── main/kotlin/
    │   ├── Main.kt
    │   ├── model/
    │   │   ├── Item.kt
    │   │   ├── Buku.kt
    │   │   ├── Majalah.kt
    │   │   ├── Dvd.kt
    │   │   ├── Anggota.kt
    │   │   └── Peminjaman.kt
    │   ├── service/
    │   │   ├── Pencarian.kt
    │   │   └── Perpustakaan.kt
    │   └── exception/
    │       └── PerpustakaanException.kt
    └── test/kotlin/service/
        └── PerpustakaanTest.kt
```

## Kebutuhan

- JDK 17 atau lebih baru.
- Gradle 8.x (modul menggunakan Kotlin JVM 2.0.21).
- IntelliJ IDEA Community Edition direkomendasikan.

## Menjalankan dari IntelliJ IDEA

1. Buka folder `simpus` melalui IntelliJ IDEA.
2. Tunggu Gradle melakukan sync dan mengunduh dependency.
3. Pastikan Project SDK menggunakan JDK 17 atau lebih baru.
4. Buka `src/main/kotlin/Main.kt`, lalu jalankan fungsi `main()`.

## Menjalankan melalui terminal

Jika Gradle sudah tersedia pada komputer:

```bash
gradle run
```

Untuk menjalankan unit test:

```bash
gradle test
```

Untuk membangun aplikasi distribusi:

```bash
gradle installDist
```

## Menu aplikasi

```text
1. Tambah Item (Buku/Majalah/DVD)
2. Tambah Anggota
3. Pinjam Item
4. Kembalikan Item
5. Cari Item
6. Lihat Semua Item
7. Lihat Peminjaman Aktif
0. Keluar & Simpan
```

Data disimpan ke `data/perpustakaan.csv` ketika pengguna memilih menu `0`. Folder `data/` dimasukkan ke `.gitignore` karena berisi data hasil uji lokal.

## Fitur yang diterapkan

- Abstract class `Item` sebagai induk koleksi.
- Inheritance dan overriding melalui `Buku`, `Majalah`, dan `Dvd`.
- Encapsulation melalui `protected set` pada status ketersediaan.
- Polymorphism melalui pemanggilan `deskripsi()` pada objek bertipe `Item`.
- Data class `Anggota` dan `Peminjaman`.
- Interface `Pencarian` yang diimplementasikan `Perpustakaan`.
- Collection: `MutableMap` untuk item/anggota dan `MutableList` untuk riwayat peminjaman.
- Sealed custom exception dan validasi input.
- Persistence sederhana ke file teks berformat pemisah `|`.
- Unit test untuk skenario normal dan kesalahan.

## Docker (bonus sesuai modul)

Build image:

```bash
docker compose build
```

Menjalankan aplikasi interaktif:

```bash
docker compose run --rm simpus-app
```

Folder `./data` di komputer dipasang ke `/app/data` di container agar file hasil penyimpanan tetap tersedia di komputer.

## Catatan implementasi

- Program ini merupakan implementasi praktikum dasar Bab 6. Fitur tambahan pada bagian latihan pengembangan modul, seperti batas maksimal peminjaman, perhitungan denda, dan pemuatan ulang data dari file, belum ditambahkan sebagai fitur wajib pada versi ini.
- File CSV menggunakan pemisah `|` sesuai contoh sederhana di modul. Karakter `|` pada input akan dinormalisasi menjadi `/` saat penyimpanan.
- Gunakan data uji yang sesuai dan cek hasil aktual ketika menyusun laporan praktikum.
