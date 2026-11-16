package service

import exception.AnggotaTidakDitemukanException
import exception.DuplikasiIdException
import exception.InputTidakValidException
import exception.ItemTidakDitemukanException
import exception.ItemTidakTersediaException
import exception.PeminjamanTidakDitemukanException
import model.Anggota
import model.Buku
import model.Dvd
import model.Item
import model.Majalah
import model.Peminjaman
import java.io.File
import java.time.LocalDate

/**
 * Mengelola koleksi, anggota, dan transaksi peminjaman perpustakaan.
 * Map digunakan untuk pencarian cepat berdasarkan ID, sedangkan List menyimpan riwayat.
 */
class Perpustakaan(val nama: String = "Perpustakaan Kampus") : Pencarian {
    private val items = mutableMapOf<String, Item>()
    private val anggota = mutableMapOf<String, Anggota>()
    private val peminjamans = mutableListOf<Peminjaman>()

    val totalItem: Int
        get() = items.size

    val totalAnggota: Int
        get() = anggota.size

    val totalPeminjaman: Int
        get() = peminjamans.size

    /** Menambahkan satu item setelah memeriksa ID dan data wajib. */
    fun tambahItem(item: Item) {
        if (item.id.isBlank()) throw InputTidakValidException("ID item tidak boleh kosong.")
        if (item.judul.isBlank()) throw InputTidakValidException("Judul item tidak boleh kosong.")
        if (item.tahunTerbit <= 0) throw InputTidakValidException("Tahun terbit harus lebih dari 0.")
        if (items.containsKey(item.id)) throw DuplikasiIdException(item.id)
        items[item.id] = item
    }

    fun tambahItems(daftar: List<Item>) {
        daftar.forEach(::tambahItem)
    }

    override fun cariItem(keyword: String): List<Item> {
        if (keyword.isBlank()) return emptyList()
        return items.values.filter {
            it.judul.contains(keyword, ignoreCase = true) ||
                it.deskripsi().contains(keyword, ignoreCase = true)
        }
    }

    fun semuaItem(): List<Item> = items.values.toList()

    fun itemTersedia(): List<Item> = items.values.filter { it.tersedia }

    fun cariItemById(id: String): Item? = items[id]

    /** Menambahkan anggota dengan validasi sederhana. */
    fun tambahAnggota(a: Anggota) {
        if (a.id.isBlank() || a.nama.isBlank()) {
            throw InputTidakValidException("ID dan nama anggota wajib diisi.")
        }
        if (!a.email.contains("@")) {
            throw InputTidakValidException("Format email tidak valid: '${a.email}'.")
        }
        if (anggota.containsKey(a.id)) throw DuplikasiIdException(a.id)
        anggota[a.id] = a
    }

    fun semuaAnggota(): List<Anggota> = anggota.values.toList()

    fun cariAnggota(id: String): Anggota? = anggota[id]

    /**
     * Meminjam item jika item tersedia dan anggota telah terdaftar.
     * Pemeriksaan dilakukan lebih dahulu agar status tidak berubah jika proses gagal.
     */
    fun pinjam(itemId: String, anggotaId: String): Peminjaman {
        val item = items[itemId] ?: throw ItemTidakDitemukanException(itemId)
        if (!anggota.containsKey(anggotaId)) {
            throw AnggotaTidakDitemukanException(anggotaId)
        }
        if (!item.tersedia) throw ItemTidakTersediaException(item.judul)

        item.tandaiDipinjam()
        val peminjaman = Peminjaman(itemId = itemId, anggotaId = anggotaId)
        peminjamans.add(peminjaman)
        return peminjaman
    }

    /** Mengembalikan item berdasarkan ID transaksi yang masih aktif. */
    fun kembalikan(peminjamanId: String): Peminjaman {
        val peminjaman = peminjamans.firstOrNull {
            it.id == peminjamanId && it.sedangDipinjam
        } ?: throw PeminjamanTidakDitemukanException(peminjamanId)

        peminjaman.tanggalKembali = LocalDate.now()
        items[peminjaman.itemId]?.tandaiDikembalikan()
        return peminjaman
    }

    fun peminjamanAktif(): List<Peminjaman> =
        peminjamans.filter { it.sedangDipinjam }

    fun semuaPeminjaman(): List<Peminjaman> = peminjamans.toList()

    fun peminjamanAnggota(anggotaId: String): List<Peminjaman> =
        peminjamans.filter { it.anggotaId == anggotaId }

    /** Menyimpan data item, anggota, dan peminjaman ke file teks berformat pipe-delimited. */
    fun simpanKeFile(path: String = "data/perpustakaan.csv") {
        val file = File(path)
        file.parentFile?.mkdirs()
        file.writeText(buildString {
            appendLine("# ITEMS")
            items.values.forEach { item ->
                val detail = when (item) {
                    is Buku -> "${item.penulis}|${item.isbn}"
                    is Majalah -> "${item.edisi}|${item.bulan}"
                    is Dvd -> "${item.durasiMenit}|${item.sutradara}"
                    else -> ""
                }
                appendLine(
                    listOf("ITEM", item::class.simpleName, item.id, item.judul,
                        item.tahunTerbit, item.tersedia, detail).joinToString("|") { aman(it.toString()) }
                )
            }
            appendLine("# ANGGOTA")
            anggota.values.forEach { a ->
                appendLine(listOf("ANGGOTA", a.id, a.nama, a.email).joinToString("|") { aman(it) })
            }
            appendLine("# PEMINJAMAN")
            peminjamans.forEach { p ->
                appendLine(
                    listOf("PINJAM", p.id, p.itemId, p.anggotaId,
                        p.tanggalPinjam, p.tanggalKembali ?: "-").joinToString("|") { aman(it.toString()) }
                )
            }
        })
        println("✓ Data tersimpan di ${file.absolutePath}")
    }

    private fun aman(teks: String): String = teks.replace("|", "/").replace("\n", " ")

    fun tampilkanRingkasan(): String = buildString {
        appendLine("=== LAPORAN $nama ===")
        appendLine("Total item          : $totalItem")
        appendLine("Item tersedia       : ${itemTersedia().size}")
        appendLine("Item sedang dipinjam: ${totalItem - itemTersedia().size}")
        appendLine("Total anggota       : $totalAnggota")
        appendLine("Total transaksi     : $totalPeminjaman")
        append("Peminjaman aktif    : ${peminjamanAktif().size}")
    }
}
