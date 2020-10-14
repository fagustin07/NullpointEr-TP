package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Pelea
import java.time.LocalDate
import java.time.LocalDateTime


data class PeleaDTO(var partyId:Long?, var date: LocalDateTime, var peleaId:Long?, var partyEnemiga:String){

    companion object {

        fun desdeModelo(pelea: Pelea):PeleaDTO{
            return PeleaDTO(pelea.idDeLaParty(), pelea.fecha(), pelea.id(), pelea.partyEnemiga())
        }
    }

}