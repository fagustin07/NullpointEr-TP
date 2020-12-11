package ar.edu.unq.epers.tactics.modelo.tienda

class Item(val nombre: String, val precio: Int) {

    fun nombre() = nombre
    fun precio() = precio

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (nombre != other.nombre) return false
        if (precio != other.precio) return false

        return true
    }


}