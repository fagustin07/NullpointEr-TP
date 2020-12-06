package ar.edu.unq.epers.tactics.persistencia.dao

import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty

interface InventarioPartyDAO {
    fun guardar(inventarioParty: InventarioParty): InventarioParty

    fun actualizar(inventarioParty: InventarioParty)

    fun recuperar(nombreParty: String): InventarioParty
}