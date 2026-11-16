package model

class Dvd(
    id: String,
    judul: String,
    tahunTerbit: Int,
    val durasiMenit: Int,
    val sutradara: String
) : Item(id, judul, tahunTerbit) {

    init {
        require(durasiMenit > 0) { "Durasi DVD harus lebih dari 0 menit." }
    }

    override fun deskripsi(): String =
        "[DVD] $judul ($tahunTerbit) - $sutradara | $durasiMenit menit"
}
