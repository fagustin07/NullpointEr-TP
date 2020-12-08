package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty

interface OperacionesDAO: DataDAO {
    fun registrarCompraDe(inventarioParty: InventarioParty, item: Item)

    fun comprasRealizadasPorParty(nombreDeParty: String): List<Compra>

    fun partiesQueCompraron(nombreItem: String): List<InventarioParty>
}