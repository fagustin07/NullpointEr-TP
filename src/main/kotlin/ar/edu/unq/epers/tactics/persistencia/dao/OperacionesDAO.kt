package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import java.time.LocalDate

interface OperacionesDAO: DataDAO {
    fun registrarCompraDe(inventarioParty: InventarioParty, item: Item, fechaDeCompra: LocalDate)

    fun comprasRealizadasPorParty(nombreDeParty: String): List<Compra>
}