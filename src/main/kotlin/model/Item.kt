package model

/*** Class induk untuk seluruh koleksi perpustakaan.
 * Item dibuat abstrak karena objek nyata yang dikelola adalah Buku, Majalah, atau Dvd.*/
abstract class Item(
    val id: String,
    var judul: String,
    var tahunTerbit: Int
) {
    var tersedia: Boolean = true
        protected set

    /** Setiap jenis item wajib menyediakan deskripsi yang sesuai. */
    abstract fun deskripsi(): String

    /** Mengubah status item menjadi dipinjam. */
    fun tandaiDipinjam() {
        require(tersedia) { "Item '$judul' sedang tidak tersedia." }
        tersedia = false
    }

    /** Mengubah status item menjadi tersedia kembali. */
    fun tandaiDikembalikan() {
        tersedia = true
    }

    override fun toString(): String = deskripsi()
}
