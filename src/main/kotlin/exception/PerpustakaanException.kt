package exception

/** Induk exception khusus yang berkaitan dengan proses perpustakaan. */
sealed class PerpustakaanException(message: String) : Exception(message)

class ItemTidakDitemukanException(id: String) :
    PerpustakaanException("Item dengan ID '$id' tidak ditemukan.")

class AnggotaTidakDitemukanException(id: String) :
    PerpustakaanException("Anggota dengan ID '$id' tidak ditemukan.")

class ItemTidakTersediaException(judul: String) :
    PerpustakaanException("Item '$judul' sedang tidak tersedia.")

class DuplikasiIdException(id: String) :
    PerpustakaanException("ID '$id' sudah terdaftar.")

class PeminjamanTidakDitemukanException(id: String) :
    PerpustakaanException("Peminjaman aktif dengan ID '$id' tidak ditemukan.")

class InputTidakValidException(pesan: String) :
    PerpustakaanException(pesan)
