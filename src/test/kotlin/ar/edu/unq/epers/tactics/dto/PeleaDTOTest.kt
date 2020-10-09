package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.service.dto.PeleaDTO
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PeleaDTOTest {

    @Test
    fun `Al convertir una Pelea a una PeleaDTO se comparten sus datos`() {
        val partyOriginal = Party("Party", "URL")
        partyOriginal.darleElId(1)
        val pelea = Pelea(partyOriginal)
        val peleaDTO = PeleaDTO.desdeModelo(pelea)

        Assertions.assertThat(pelea.fecha()).isEqualTo(peleaDTO.date)
        Assertions.assertThat(pelea.idDeLaParty()).isEqualTo(peleaDTO.partyId)
        Assertions.assertThat(pelea.id()).isEqualTo(peleaDTO.peleaId)
    }
}