package ar.edu.unq.epers.tactics.modelo.tienda

import java.time.LocalDateTime

data class Compra(val item: Item, val fechaCompra: LocalDateTime)
