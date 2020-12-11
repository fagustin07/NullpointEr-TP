package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.tienda.Item

interface ItemDAO: DataDAO {
    fun guardar(item: Item): Item

    fun recuperar(nombre: String): Item

    fun loMasCompradoEnLaUltimaSemana(): List<Pair<Item, Int>>

    fun itemsEnVenta():List<Item>

    fun perteneceA(nombreItem: String, nombreParty: String): Boolean
}