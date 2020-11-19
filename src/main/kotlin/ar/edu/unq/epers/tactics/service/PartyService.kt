package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party

interface PartyService {
    fun crear(party: Party) : Party
    fun actualizar(party: Party):Party
    fun recuperar(idDeLaParty: Long): Party
    fun recuperarTodas(): List<Party>
    fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero) : Aventurero
    fun eliminarTodo()
    fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina:Int?):PartyPaginadas
}

class PartyPaginadas (var parties:List<Party>, var total:Int)

enum class Orden {
    PODER,
    VICTORIAS,
    DERROTAS
}

enum class Direccion {
    ASCENDENTE { override fun keywordHql(): String {return "asc"}},
    DESCENDENTE {override fun keywordHql():String {return "desc"}};

    abstract fun keywordHql(): String
}
