package ar.edu.unq.epers.tactics.modelo

class Party(val nombre: String) {
    var id: Long? = null
    var numeroDeAventureros = 0

    fun agregarA(unAventurero: Aventurero) {
        if (!esLaParty(unAventurero.party)) throw RuntimeException("El aventurero no pertenece a la party seleccionada.")
        numeroDeAventureros++
    }


    private fun esLaParty(partyBuscada: Party) = nombre == partyBuscada.nombre
}