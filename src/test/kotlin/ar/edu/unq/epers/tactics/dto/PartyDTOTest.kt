package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import ar.edu.unq.epers.tactics.service.dto.PartyDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PartyDTOTest {

    @Test
    fun `Al convertir una Party a una PartyDTO y de nuevo a una Party se obtienen objetos similares`() {
        val partyOriginal = Party("Party", "URL")
        partyOriginal.agregarUnAventurero(Aventurero(
                "Pepe",
                "",
                40.0,
                50.0,
                60.0,
                70.0
        ))
        val partyDTO = PartyDTO.desdeModelo(partyOriginal)
        val partyObtenida = partyDTO.aModelo()
        assertThat(partyOriginal).usingRecursiveComparison().isEqualTo(partyObtenida)
    }

    @Test
    fun `Al enviar el mensaje actualizar a un PartyDAO se actualiza el objeto de dominio`() {
        val partyOriginal = Party("Party", "URL")
        val partyDAO = PartyDTO.desdeModelo(partyOriginal)
        val nuevaUrl = "http://..."

        val aventureroDTO = AventureroDTO.desdeModelo(Aventurero("Marcos"))
        partyDAO.aventureros = listOf(aventureroDTO)
        partyDAO.imagenURL = nuevaUrl
        partyDAO.actualizarModelo(partyOriginal)

        assertThat(partyOriginal.aventureros().size).isEqualTo(1)
        assertThat(partyOriginal.imagenURL()).isEqualTo(nuevaUrl)
    }
}