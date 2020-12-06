package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.tienda.Item

interface ItemDAO {
    fun guardar(item: Item): Item

    fun recuperar(nombre: String): Item

    fun loMasComprado(): List<Pair<Item, Int>>
}