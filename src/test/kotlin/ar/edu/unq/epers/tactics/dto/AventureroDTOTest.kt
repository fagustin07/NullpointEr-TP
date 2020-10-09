package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.enums.Accion
import ar.edu.unq.epers.tactics.modelo.enums.Criterio
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeEstadistica
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.dto.AtributosDTO
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AventureroDTOTest {

    @Test
    fun `Al convertir un Aventurero a un AventureroDTO y de nuevo a un Aventurero se obtienen objetos similares`() {
        val aventureroOriginal = aventureroConTacticas()
        val aventureroDTO = AventureroDTO.desdeModelo(aventureroOriginal)

        val aventureroDesdeDTO = aventureroDTO.aModelo()

        assertThat(aventureroDesdeDTO).usingRecursiveComparison().isEqualTo(aventureroOriginal)
    }


    @Test
    fun `Al enviar el mensaje actualizar a un AventureroDTO se actualiza el objeto de dominio`() {
        val aventurero = aventureroConTacticas()
        val aventureroDTO = AventureroDTO(
            null,
            aventurero.nivel(),
            "Nombre en DTO",
            "/otraImagen.jpg",
            aventurero.dañoRecibido() + 1,
            listOf(),
            AtributosDTO(null, aventurero.fuerza() + 1, aventurero.destreza() + 1, aventurero.constitucion() + 1, aventurero.inteligencia() + 1)
        )

        aventureroDTO.actualizarModelo(aventurero)

        assertThat(aventurero.nivel()).isEqualTo(aventureroDTO.nivel)
        assertThat(aventurero.nombre()).isEqualTo(aventureroDTO.nombre)
        assertThat(aventurero.imagenURL()).isEqualTo(aventureroDTO.imagenURL)
        assertThat(aventurero.dañoRecibido()).isEqualTo(aventureroDTO.dañoRecibido)
        assertTrue(aventurero.tacticas().isEmpty())
        assertThat(aventurero.fuerza()).isEqualTo(aventureroDTO.atributos.fuerza)
        assertThat(aventurero.destreza()).isEqualTo(aventureroDTO.atributos.destreza)
        assertThat(aventurero.constitucion()).isEqualTo(aventureroDTO.atributos.constitucion)
        assertThat(aventurero.inteligencia()).isEqualTo(aventureroDTO.atributos.inteligencia)
    }

    private fun aventureroConTacticas(): Aventurero {
        val aventurero = Aventurero("Pepe", "", 40.0, 50.0, 60.0, 70.0)

        aventurero.agregarTactica(tacticaDeCuracion())
        aventurero.agregarTactica(tacticaDeAtaque())

        return aventurero
    }

    private fun tacticaDeCuracion() =
        Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 30.0, Accion.CURAR)

    private fun tacticaDeAtaque() =
        Tactica(2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 15.0, Accion.ATAQUE_MAGICO)

}