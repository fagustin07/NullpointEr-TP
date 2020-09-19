package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Party


data class PartyDTO(var id:Long?, var nombre:String, var imagenURL:String, var aventureros:List<AventureroDTO>){

    companion object {

        fun desdeModelo(party: Party):PartyDTO{
            TODO()
        }
    }

    fun aModelo(): Party {
        TODO()
    }

    fun actualizarModelo(party: Party){
        TODO()
    }
}