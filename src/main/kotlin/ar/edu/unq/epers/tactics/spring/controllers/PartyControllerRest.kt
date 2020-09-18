package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.PartyDTO
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/party")
class PartyControllerRest(private val partyService: PartyService) {

    @GetMapping
    fun getParties():List<PartyDTO> = partyService.recuperarTodas().map { PartyDTO.desdeModelo(it) }

    @GetMapping("/{id}")
    fun getParty(@PathVariable id: Long) = PartyDTO.desdeModelo(partyService.recuperar(id))

    @PutMapping("/{id}")
    fun updateParty(@PathVariable id: Long, @RequestBody partyData: PartyDTO):PartyDTO {
        val party = partyService.recuperar(id)
        partyData.actualizarModelo(party)
        return PartyDTO.desdeModelo(partyService.actualizar(party))
    }

    @PostMapping
    fun createParty(@RequestBody partyData: PartyDTO):PartyDTO{
        return PartyDTO.desdeModelo(partyService.crear(partyData.aModelo()))
    }

}
