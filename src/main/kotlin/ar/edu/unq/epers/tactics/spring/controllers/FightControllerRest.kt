package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.*
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import ar.edu.unq.epers.tactics.service.dto.HabilidadDTO
import ar.edu.unq.epers.tactics.service.dto.PartyDTO
import ar.edu.unq.epers.tactics.service.dto.PeleaDTO
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/fight")
class FightControllerRest(private val peleaService: PeleaService) {

    @PostMapping
    fun createFight(@RequestBody request: CreateFightRequest) = PeleaDTO.desdeModelo(peleaService.iniciarPelea(request.partyId, request.partyEnemiga))

    @PostMapping("/{fightId}/finish")
    fun finish(@PathVariable fightId: Long):StatusResponse  {
        peleaService.terminarPelea(fightId)
        return StatusResponse(200)
    }

    @PostMapping("/{fightId}/resolveTurn")
    fun resolveTurn(@PathVariable fightId: Long, @RequestBody request:ResolveTurnRequest):HabilidadDTO? {
        val habilidad = peleaService.resolverTurno(fightId, request.adventurerId, request.enemies)
        return HabilidadDTO.desdeModelo(habilidad)
    }

    @PostMapping("/{fightId}/receiveAbility")
    fun receiveAbility(@PathVariable fightId: Long, @RequestBody request:ReceiveAbilityRequest):AventureroDTO {
        val aventurero = peleaService.recibirHabilidad(request.adventurerId, request.ability.aModelo())
        return AventureroDTO.desdeModelo(aventurero)
    }

    @GetMapping("/party/{partyId}")
    fun recuperarOrdenadas(@PathVariable partyId: Long, @RequestParam(required = false) pagina:Int?):PeleasPaginadasDTO {
        return PeleasPaginadasDTO.desdeModelo(peleaService.recuperarOrdenadas(partyId, pagina))
    }
}



data class CreateFightRequest(val partyId:Long, val partyEnemiga:String)
data class ResolveTurnRequest(val adventurerId:Long, val enemies:List<Aventurero>)
data class ReceiveAbilityRequest(val adventurerId:Long, val ability:HabilidadDTO)

data class PeleasPaginadasDTO(val peleas:List<PeleaDTO>, val total:Int){
    companion object{
        fun desdeModelo(peleasPaginadas: PeleasPaginadas):PeleasPaginadasDTO {
            return PeleasPaginadasDTO(peleasPaginadas.peleas.map { PeleaDTO.desdeModelo(it) }, peleasPaginadas.total)
        }
    }
}
