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
import model.Majalah
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import java.nio.file.Files

class PerpustakaanTest {
    private fun buatPerpustakaan(): Perpustakaan {
        val perpustakaan = Perpustakaan("Perpustakaan Uji")
        perpustakaan.tambahItem(Buku("B001", "Belajar Kotlin", 2024, "Budi", "ISBN-001"))
        perpustakaan.tambahItem(Majalah("M001", "Teknologi Hari Ini", 2025, 12, "November"))
        perpustakaan.tambahItem(Dvd("D001", "Dokumenter Alam", 2023, 90, "Sari"))
        perpustakaan.tambahAnggota(Anggota("A001", "Adema", "adema@example.com"))
        return perpustakaan
    }

    @Test
    fun `dapat menambahkan tiga jenis item`() {
        val perp = buatPerpustakaan()
        assertEquals(3, perp.totalItem)
        assertIs<Buku>(perp.cariItemById("B001"))
        assertIs<Majalah>(perp.cariItemById("M001"))
        assertIs<Dvd>(perp.cariItemById("D001"))
    }

    @Test
    fun `menolak ID item duplikat`() {
        val perp = buatPerpustakaan()
        assertFailsWith<DuplikasiIdException> {
            perp.tambahItem(Buku("B001", "Judul Lain", 2024, "Penulis", "ISBN-002"))
        }
    }

    @Test
    fun `menolak anggota dengan email tidak valid`() {
        val perp = Perpustakaan()
        assertFailsWith<InputTidakValidException> {
            perp.tambahAnggota(Anggota("A002", "Rina", "email-tidak-valid"))
        }
    }

    @Test
    fun `peminjaman berhasil dan item berubah menjadi tidak tersedia`() {
        val perp = buatPerpustakaan()
        val transaksi = perp.pinjam("B001", "A001")
        assertFalse(perp.cariItemById("B001")!!.tersedia)
        assertTrue(transaksi.sedangDipinjam)
        assertEquals(1, perp.peminjamanAktif().size)
    }

    @Test
    fun `menolak peminjaman item yang sudah dipinjam`() {
        val perp = buatPerpustakaan()
        perp.pinjam("B001", "A001")
        assertFailsWith<ItemTidakTersediaException> { perp.pinjam("B001", "A001") }
    }

    @Test
    fun `menolak peminjaman jika item tidak ditemukan`() {
        val perp = buatPerpustakaan()
        assertFailsWith<ItemTidakDitemukanException> { perp.pinjam("X999", "A001") }
    }

    @Test
    fun `menolak peminjaman oleh anggota yang tidak terdaftar`() {
        val perp = buatPerpustakaan()
        assertFailsWith<AnggotaTidakDitemukanException> { perp.pinjam("B001", "A999") }
    }

    @Test
    fun `pengembalian membuat item tersedia kembali`() {
        val perp = buatPerpustakaan()
        val transaksi = perp.pinjam("B001", "A001")
        val hasil = perp.kembalikan(transaksi.id)
        assertFalse(hasil.sedangDipinjam)
        assertTrue(perp.cariItemById("B001")!!.tersedia)
        assertEquals(0, perp.peminjamanAktif().size)
    }

    @Test
    fun `menolak pengembalian transaksi yang tidak ditemukan`() {
        val perp = buatPerpustakaan()
        assertFailsWith<PeminjamanTidakDitemukanException> { perp.kembalikan("TIDAKADA") }
    }

    @Test
    fun `pencarian tidak membedakan huruf besar dan kecil`() {
        val perp = buatPerpustakaan()
        val hasil = perp.cariItem("kOtLiN")
        assertEquals(1, hasil.size)
        assertEquals("B001", hasil.first().id)
    }

    @Test
    fun `simpan file membuat data item anggota dan peminjaman`() {
        val perp = buatPerpustakaan()
        perp.pinjam("B001", "A001")
        val folder = Files.createTempDirectory("simpus-test")
        val file = folder.resolve("perpustakaan.csv")
        perp.simpanKeFile(file.toString())
        val isi = Files.readString(file)
        assertTrue(isi.contains("# ITEMS"))
        assertTrue(isi.contains("# ANGGOTA"))
        assertTrue(isi.contains("# PEMINJAMAN"))
        assertTrue(isi.contains("B001"))
        assertTrue(isi.contains("A001"))
    }
}
