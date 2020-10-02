package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.service.dto.HabilidadDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HabilidadDTOTest {

    @Test
    fun `Al convertir un Ataque a un AtaqueDTO y de nuevo a un Ataque se obtienen objetos similares`() {
        val aventurero = Aventurero("Raul")
        val ataqueOriginal = Ataque(10, 8, aventurero, DadoDe20())

        val ataqueDTO = HabilidadDTO.desdeModelo(ataqueOriginal)
        val ataqueObtenido = ataqueDTO.aModelo()

        assertThat(ataqueObtenido).usingRecursiveComparison().isEqualTo(ataqueOriginal)
    }

    @Test
    fun `Al convertir una Defensa a una DefensaDTO y de nuevo a una Defensa se obtienen objetos similares`() {
        val aventureroEmisor = Aventurero("Raul")
        val aventureroReceptor = Aventurero("Jorge")
        val defensaOriginal = Defensa(aventureroEmisor, aventureroReceptor)

        val defensaDTO = HabilidadDTO.desdeModelo(defensaOriginal)
        val defensaObtenida = defensaDTO.aModelo()

        assertThat(defensaObtenida).usingRecursiveComparison().isEqualTo(defensaOriginal)
    }

    @Test
    fun `Al convertir un Curacion a un CurarDTO y de nuevo a un Curacion se obtienen objetos similares`() {
        val aventurero = Aventurero("Raul")
        val curarOriginal = Curacion(aventurero.poderMagico(), aventurero)

        val curarDTO = HabilidadDTO.desdeModelo(curarOriginal)
        val curarObtenido = curarDTO.aModelo()

        assertThat(curarObtenido).usingRecursiveComparison().isEqualTo(curarOriginal)
    }

    @Test
    fun `Al convertir un AtaqueMagico a un AtaqueMagicoDTO y de nuevo a un AtaqueMagico se obtienen objetos similares`() {
        val aventurero = Aventurero("Raul")
        val aventureroAtacado = Aventurero("Sancho Panza")
        val ataqueMagicoOriginal = AtaqueMagico(aventurero.poderMagico(), aventurero.nivel(), aventureroAtacado, DadoDe20())

        val ataqueMagicoDTO = HabilidadDTO.desdeModelo(ataqueMagicoOriginal)
        val ataqueMagicoObtenido = ataqueMagicoDTO.aModelo()

        assertThat(ataqueMagicoObtenido).usingRecursiveComparison().isEqualTo(ataqueMagicoOriginal)
    }

    @Test
    fun `Al convertir un Meditacion a un MeditarDTO y de nuevo a un Meditacion se obtienen objetos similares`() {
        val aventurero = Aventurero("Raul")
        val meditacion = Meditacion(aventurero, aventurero)

        val meditarDTO = HabilidadDTO.desdeModelo(meditacion)
        val meditacionObtenida = meditarDTO.aModelo()

        assertThat(meditacionObtenida).usingRecursiveComparison().isEqualTo(meditacion)
    }
}