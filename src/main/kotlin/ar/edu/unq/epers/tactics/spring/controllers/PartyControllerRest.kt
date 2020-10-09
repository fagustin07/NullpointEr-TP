package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.dto.PartyDTO
import org.springframework.web.bind.annotation.*

@ServiceREST
@RequestMapping("/party")
class PartyControllerRest(private val partyService: PartyService) {

    @GetMapping
    fun getParties():List<PartyDTO> = partyService.recuperarTodas().map { PartyDTO.desdeModelo(it) }

    @GetMapping("/ordenadas")
    fun partiesOrdenadas(@RequestBody request:PartiesOrdenadasRequest):PartyPaginadasDTO {
        return PartyPaginadasDTO.desdeModelo(partyService.recuperarOrdenadas(request.orden, request.direccion, request.pagina))
    }

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


data class PartiesOrdenadasRequest(var orden: Orden, var direccion: Direccion, var pagina:Int?)

data class PartyPaginadasDTO(val parties:List<PartyDTO>, val total:Int){
    companion object{
        fun desdeModelo(partyPaginadas: PartyPaginadas):PartyPaginadasDTO{
            return PartyPaginadasDTO(partyPaginadas.parties.map { PartyDTO.desdeModelo(it) }, partyPaginadas.total)
        }
    }
}
