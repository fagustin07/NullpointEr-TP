package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AventureroDTOTest {

    @Test
    fun `Al convertir un Aventurero a un AventureroDTO y de nuevo a un Aventurero se obtienen objetos similares`() {
        val aventureroOriginal = Aventurero("Pepe", 40, 50, 60, 70)
        val aventureroDTO = AventureroDTO.desdeModelo(aventureroOriginal)
        val aventureroDesdeDTO = aventureroDTO.aModelo()

        assertThat(aventureroDesdeDTO).usingRecursiveComparison().isEqualTo(aventureroOriginal)
    }

    @Test
    fun `Al enviar el mensaje actualizar a un AventureroDTO se actualiza el objeto de dominio`(){
        val aventurero = Aventurero("Pepe", 40, 50, 60, 70)
        val aventureroDTO = AventureroDTO.desdeModelo(aventurero)

        aventureroDTO.atributos.fuerza = 56
        aventureroDTO.atributos.inteligencia = 2
        aventureroDTO.actualizarModelo(aventurero)

        assertThat(aventurero.fuerza()).isEqualTo(56)
        assertThat(aventurero.inteligencia()).isEqualTo(2)
    }
}