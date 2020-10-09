package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/party/{partyId}/adventurer")
class AdventurerControllerRest(private val aventureroService: AventureroService, val partyService:PartyService) {

    @GetMapping
    fun getAdventurers(@PathVariable partyId: Long):List<AventureroDTO> = partyService.recuperar(partyId).aventureros.map { AventureroDTO.desdeModelo(it) }

    @GetMapping("/{id}")
    fun getAdventurer(@PathVariable id: Long) = AventureroDTO.desdeModelo(aventureroService.recuperar(id))

    @PutMapping("/{id}")
    fun updateAdventurer(@PathVariable id: Long, @RequestBody adventurerData: AventureroDTO):AventureroDTO {
        val adventurer = aventureroService.recuperar(id)
        adventurerData.actualizarModelo(adventurer)
        return AventureroDTO.desdeModelo(aventureroService.actualizar(adventurer))
    }

    @PostMapping
    fun createAdventurer(@PathVariable partyId: Long, @RequestBody adventurerData: AventureroDTO):AventureroDTO{
        val aventurero = partyService.agregarAventureroAParty(partyId, adventurerData.aModelo())
        return AventureroDTO.desdeModelo(aventurero)
    }

    @DeleteMapping("/{id}")
    fun deleteAdventurer(@PathVariable id: Long):StatusResponse {
        val adventurer = aventureroService.recuperar(id)
        aventureroService.eliminar(adventurer)
        return StatusResponse(201)
    }

}

data class StatusResponse(var status:Int)