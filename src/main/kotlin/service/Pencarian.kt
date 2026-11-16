package service

import model.Item

/** Kontrak kemampuan mencari item berdasarkan kata kunci. */
interface Pencarian {
    fun cariItem(keyword: String): List<Item>
}
