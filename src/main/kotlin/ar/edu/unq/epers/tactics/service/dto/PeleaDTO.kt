package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Pelea
import java.time.LocalDateTime


data class PeleaDTO(var partyId:Long?, var date: LocalDateTime, var peleaId:Long?){

    companion object {

        fun desdeModelo(pelea: Pelea):PeleaDTO{
            TODO("implementar el pasaje a DTO de pelea")
        }
    }

}