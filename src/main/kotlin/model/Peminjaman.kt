package model

import java.time.LocalDate
import java.util.UUID

data class Peminjaman(
    val id: String = UUID.randomUUID().toString().take(8),
    val itemId: String,
    val anggotaId: String,
    val tanggalPinjam: LocalDate = LocalDate.now(),
    var tanggalKembali: LocalDate? = null
) {
    /** True selama item belum dikembalikan. */
    val sedangDipinjam: Boolean
        get() = tanggalKembali == null
}
