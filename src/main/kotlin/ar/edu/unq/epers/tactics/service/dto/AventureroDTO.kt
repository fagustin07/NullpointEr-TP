package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero


data class AventureroDTO(var id:Long?, var nivel:Int, var nombre:String, var imagenURL:String, var dañoRecibido:Double, var tacticas: List<TacticaDTO>, var atributos: AtributosDTO){

    companion object {

        fun desdeModelo(aventurero: Aventurero):AventureroDTO{
            TODO()
        }
    }

    fun aModelo():Aventurero{
        TODO()
    }

    fun actualizarModelo(aventurero: Aventurero){
        TODO()
    }
}

data class AtributosDTO(var id:Long?, var fuerza:Int, var destreza:Int, var constitucion:Int, var inteligencia:Int)
data class TacticaDTO(var id:Long?, var receptor:TipoDeReceptor, var tipoDeEstadistica:TipoDeEstadistica, var criterio:Criterio, var valor:Int, var accion:Accion)

enum class TipoDeReceptor {
    ALIADO,
    ENEMIGO
}
enum class TipoDeEstadistica {
    VIDA,
    ARMADURA,
    MANA,
    VELOCIDAD,
    DAÑO_FISICO,
    DAÑO_MAGICO,
    PRECISION_FISICA
}

enum class Criterio {
    IGUAK ,
    MAYOR_QUE ,
    MENOR_QUE ;
}

enum class Accion{
    ATAQUE_FISICO,
    DEFENDER,
    CURAR,
    ATAQUE_MAGICO,
    MEDITAR;
}