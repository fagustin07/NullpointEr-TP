package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party


data class PartyDTO(var id:Long?, var nombre:String, var imagenURL:String, var aventureros:List<AventureroDTO>){

    companion object {

        fun desdeModelo(party: Party):PartyDTO{
            return PartyDTO(
                    party.id(),
                    party.nombre(),
                    party.imagenURL(),
                    generarAventureroDTOs(party.aventureros())
            )
        }

        private fun generarAventureroDTOs(aventureros: MutableList<Aventurero>): List<AventureroDTO> {
            return aventureros.map { aventurero -> AventureroDTO.desdeModelo(aventurero) }
        }
    }

    fun aModelo(): Party {
        val party = Party(this.nombre, this.imagenURL)
        this.aventureros.forEach { aventureroDTO ->
            val aventurero = aventureroDTO.aModelo()
            aventurero.party = party
            party.agregarUnAventurero(aventurero)
        }
        party.darleElId(this.id)
        return party

    }

    fun actualizarModelo(party: Party) = party.actualizarse(this)
}