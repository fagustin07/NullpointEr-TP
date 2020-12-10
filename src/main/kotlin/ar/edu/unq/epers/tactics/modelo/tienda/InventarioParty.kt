package ar.edu.unq.epers.tactics.modelo.tienda

class InventarioParty(val nombre: String, var monedas: Int = 0) {

    fun comprar(item: Item) {
        if (item.precio() > this.monedas){
            val monedasFaltantes = item.precio() - this.monedas

            throw RuntimeException("No puedes comprar '${item.nombre}', te faltan ${monedasFaltantes} monedas.")
        } else {
            monedas -= item.precio()
        }
    }

    internal fun adquirirRecompensaDePelea() {
        this.monedas+=500
    }

    fun debitarMonto(monedas: Int) {
        if (monedas > this.monedas){
            val monedasFaltantes = monedas - this.monedas

            throw RuntimeException("No puedes debitar '${monedas}', te faltan ${monedasFaltantes} monedas.")
        } else {
            this.monedas -= monedas
        }
    }
}