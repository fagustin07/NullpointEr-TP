package ar.edu.unq.epers.tactics.modelo.tienda

import ar.edu.unq.epers.tactics.exceptions.CannotBuyException
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.Item

class PartyConMonedas(val id: Long, var monedas: Int) {

    fun comprar(item: Item) {
        if (item.precio > this.monedas){
            val monedasFaltantes = item.precio - this.monedas

            throw CannotBuyException(item.nombre, monedasFaltantes)
        } else {
            monedas -= item.precio
        }
    }
}