package ar.edu.unq.epers.tactics.modelo

class Party(val nombre: String) {
    var id: Long? = null
    var numeroDeAventureros = 0
    var aventureros:List<Aventurero> = listOf()
}