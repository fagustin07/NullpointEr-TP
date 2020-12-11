package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.modelo.tienda.Item
import ar.edu.unq.epers.tactics.service.*
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/store")
class TiendaControllerRest(private val tiendaService: TiendaService) {

    @GetMapping("/item")
    fun itemsEnVenta() = tiendaService.itemsEnVenta()

    @GetMapping("/item/mas-comprados")
    fun loMasComprado() =
        tiendaService.loMasComprado().map { mapOf("item" to it.first, "cantidad de unidades vendidas" to it.second) }

    @PostMapping("/item")
    fun registrarItem(@RequestBody itemARegistrar: Item) =
        tiendaService.registrarItem(itemARegistrar.nombre, itemARegistrar.precio)

    @GetMapping("/item/{nombreDeParty}")
    fun losItemsDe(@PathVariable nombreDeParty: String) =
        tiendaService.losItemsDe(nombreDeParty)

    @PostMapping("/compra")
    fun registrarCompra(@RequestBody ordenDeCompra: OrdenDeCompra) =
        tiendaService.registrarCompra(ordenDeCompra.nombrePartyCompradora, ordenDeCompra.nombreDeItemAComprar)

    @GetMapping("/compra/{nombreDeParty}")
    fun comprasRealizadasPor(@PathVariable nombreDeParty: String) =
        tiendaService.comprasRealizadasPor(nombreDeParty)

    @PostMapping("/trade")
    fun efectuarTrade(
        @RequestBody tradeoAEfectuar: TradeDTO
    ) =
        tiendaService.tradear(
            tradeoAEfectuar.nombrePartyVendedora,
            tradeoAEfectuar.nombrePartyCompradora,
            tradeoAEfectuar.itemsAVender,
            tradeoAEfectuar.monedasOfrecidas)
}

data class TradeDTO(val nombrePartyVendedora: String,
                    val nombrePartyCompradora: String,
                    val itemsAVender: List<Item>,
                    val monedasOfrecidas: Int)

data class OrdenDeCompra(val nombrePartyCompradora: String, val nombreDeItemAComprar: String)