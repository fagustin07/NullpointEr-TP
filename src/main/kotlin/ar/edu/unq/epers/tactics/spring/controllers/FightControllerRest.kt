package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import ar.edu.unq.epers.tactics.service.dto.HabilidadDTO
import ar.edu.unq.epers.tactics.service.dto.HabilidadNulaDTO
import ar.edu.unq.epers.tactics.service.dto.PeleaDTO
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@ServiceREST
@RequestMapping("/fight")
class FightControllerRest(private val peleaService: PeleaService) {

    @PostMapping
    fun createFight(@RequestBody request: CreateFightRequest) = PeleaDTO.desdeModelo(peleaService.iniciarPelea(request.partyId))

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
        val ability = request.ability ?: HabilidadNulaDTO(AventureroDTO.desdeModelo(Aventurero("")),AventureroDTO.desdeModelo(Aventurero("")))
        val aventurero = peleaService.recibirHabilidad(request.adventurerId, ability.aModelo())
        return AventureroDTO.desdeModelo(aventurero)
    }
}



data class CreateFightRequest(val partyId:Long)
data class ResolveTurnRequest(val adventurerId:Long, val enemies:List<Aventurero>)
data class ReceiveAbilityRequest(val adventurerId:Long, val ability:HabilidadDTO)
