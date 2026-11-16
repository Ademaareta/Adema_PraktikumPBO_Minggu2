import exception.PerpustakaanException
import model.Anggota
import model.Buku
import model.Dvd
import model.Majalah
import service.Perpustakaan

private val perpustakaan = Perpustakaan("Perpustakaan Kampus")

fun main() {
    println("Selamat datang di ${perpustakaan.nama}!")
    while (true) {
        tampilkanMenu()
        when (bacaInput("Pilih menu (0-7): ")) {
            "1" -> tambahItemMenu()
            "2" -> tambahAnggotaMenu()
            "3" -> pinjamMenu()
            "4" -> kembalikanMenu()
            "5" -> cariItemMenu()
            "6" -> lihatSemuaItem()
            "7" -> lihatPeminjamanAktif()
            "0" -> {
                jalankanAman { perpustakaan.simpanKeFile() }
                println("Program selesai. Terima kasih!")
                return
            }
            else -> println("! Pilihan tidak valid. Silakan pilih angka 0 sampai 7.")
        }
        println()
    }
}

private fun tampilkanMenu() {
    println(
        """
        |========================================
        |       SISTEM INFORMASI PERPUSTAKAAN
        |========================================
        |1. Tambah Item (Buku/Majalah/DVD)
        |2. Tambah Anggota
        |3. Pinjam Item
        |4. Kembalikan Item
        |5. Cari Item
        |6. Lihat Semua Item
        |7. Lihat Peminjaman Aktif
        |0. Keluar & Simpan
        |----------------------------------------
        """.trimMargin()
    )
}

private fun bacaInput(prompt: String): String {
    print(prompt)
    return readLine()?.trim().orEmpty()
}

private fun bacaAngka(prompt: String): Int {
    while (true) {
        val angka = bacaInput(prompt).toIntOrNull()
        if (angka != null && angka > 0) return angka
        println("! Harap masukkan angka bulat yang lebih dari 0.")
    }
}

private fun tambahItemMenu() {
    println("Jenis item: 1) Buku  2) Majalah  3) DVD")
    val jenis = bacaInput("Pilih jenis: ")
    if (jenis !in listOf("1", "2", "3")) {
        println("! Jenis tidak valid. Pilih 1, 2, atau 3.")
        return
    }

    val id = bacaInput("ID item       : ")
    val judul = bacaInput("Judul         : ")
    val tahun = bacaAngka("Tahun terbit  : ")
    val item = when (jenis) {
        "1" -> Buku(id, judul, tahun, bacaInput("Penulis       : "), bacaInput("ISBN          : "))
        "2" -> Majalah(id, judul, tahun, bacaAngka("Nomor edisi   : "), bacaInput("Bulan terbit  : "))
        else -> Dvd(id, judul, tahun, bacaAngka("Durasi (menit): "), bacaInput("Sutradara     : "))
    }

    jalankanAman {
        perpustakaan.tambahItem(item)
        println("✓ Item berhasil ditambahkan: ${item.deskripsi()}")
    }
}

private fun tambahAnggotaMenu() {
    val anggota = Anggota(
        id = bacaInput("ID anggota : "),
        nama = bacaInput("Nama       : "),
        email = bacaInput("Email      : ")
    )
    jalankanAman {
        perpustakaan.tambahAnggota(anggota)
        println("✓ Anggota '${anggota.nama}' berhasil didaftarkan.")
    }
}

private fun pinjamMenu() {
    val itemId = bacaInput("ID item    : ")
    val anggotaId = bacaInput("ID anggota : ")
    jalankanAman {
        val p = perpustakaan.pinjam(itemId, anggotaId)
        println("✓ Peminjaman berhasil. ID transaksi: ${p.id}")
        println("  Tanggal pinjam: ${p.tanggalPinjam}")
    }
}

private fun kembalikanMenu() {
    val id = bacaInput("ID peminjaman: ")
    jalankanAman {
        val p = perpustakaan.kembalikan(id)
        println("✓ Item ${p.itemId} berhasil dikembalikan pada ${p.tanggalKembali}.")
    }
}

private fun cariItemMenu() {
    val keyword = bacaInput("Kata kunci: ")
    val hasil = perpustakaan.cariItem(keyword)
    if (hasil.isEmpty()) println("Tidak ada item yang cocok dengan '$keyword'.")
    else hasil.forEach { println("- ${it.deskripsi()} | Status: ${if (it.tersedia) "Tersedia" else "Dipinjam"}") }
}

private fun lihatSemuaItem() {
    val semua = perpustakaan.semuaItem()
    if (semua.isEmpty()) println("Belum ada item dalam koleksi.")
    else semua.forEach { println("- ${it.deskripsi()} | Status: ${if (it.tersedia) "Tersedia" else "Dipinjam"}") }
    println(perpustakaan.tampilkanRingkasan())
}

private fun lihatPeminjamanAktif() {
    val aktif = perpustakaan.peminjamanAktif()
    if (aktif.isEmpty()) println("Tidak ada peminjaman aktif.")
    else aktif.forEach {
        println("- ID: ${it.id} | Item: ${it.itemId} | Anggota: ${it.anggotaId} | Tanggal: ${it.tanggalPinjam}")
    }
}

private inline fun jalankanAman(aksi: () -> Unit) {
    try {
        aksi()
    } catch (e: PerpustakaanException) {
        println("! ${e.message}")
    } catch (e: IllegalArgumentException) {
        println("! Input tidak valid: ${e.message}")
    }
}
