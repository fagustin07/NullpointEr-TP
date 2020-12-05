package ar.edu.unq.epers.tactics.modelo.tienda

import ar.edu.unq.epers.tactics.exceptions.CannotBuyException

class PartyConMonedas(val nombre: String, var monedas: Int = 0) {

    fun nombre() = nombre
    fun monedas() = monedas

    fun comprar(item: Item) {
        if (item.precio() > this.monedas){
            val monedasFaltantes = item.precio() - this.monedas

            throw CannotBuyException(item.nombre(), monedasFaltantes)
        } else {
            monedas -= item.precio()
        }
    }

    internal fun adquirirRecompensaDePelea() {
        this.monedas+=500
    }
}