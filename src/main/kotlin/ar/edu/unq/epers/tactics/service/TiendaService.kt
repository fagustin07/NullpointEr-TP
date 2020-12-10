package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.tienda.Compra
import ar.edu.unq.epers.tactics.modelo.tienda.Item

interface TiendaService {

    fun registrarItem(nombre: String, precio: Int): Item

    fun registrarCompra(nombreParty: String, nombreDeItemAComprar: String)

    fun comprasRealizadasPor(nombreDeParty: String): List<Compra>

    fun loMasComprado(): List<Pair<Item, Int>>

    fun losItemsDe(nombreParty: String):List<Item>

    fun compradoresDe(nombreItem: String): List<Party>

    fun tradear(nombrePartyVendedora: String, nombrePartyCompradora: String, itemsAVender: List<Item>, monedas: Int)
}