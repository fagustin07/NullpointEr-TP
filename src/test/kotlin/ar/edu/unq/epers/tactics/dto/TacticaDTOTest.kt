package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.service.dto.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class TacticaDTOTest {

    @Test
    fun `Al convertir una Tactica a una TacticaDTO y de nuevo a una Tactica se obtienen objetos similares`() {
        val tacticaOriginal =
            Tactica(1,TipoDeReceptor.ENEMIGO,
            TipoDeEstadistica.VIDA,
                Criterio.MAYOR_QUE,
                50,
                Accion.ATAQUE_MAGICO)

        val tacticaDTO = TacticaDTO.desdeModelo(tacticaOriginal)
        val tacticaDesdeDTO = tacticaDTO.aModelo()

        Assertions.assertThat(tacticaDesdeDTO).usingRecursiveComparison().isEqualTo(tacticaOriginal)
    }

    @Test
    fun `Al actualizar el modelo desde una TacticaDTO se actualiza la Tactica en el dominio`(){
        val tacticaOriginal =
            Tactica(1,TipoDeReceptor.ENEMIGO,
                TipoDeEstadistica.VIDA,
                Criterio.MAYOR_QUE,
                50,
                Accion.ATAQUE_MAGICO)
        val tacticaDTO = TacticaDTO.desdeModelo(tacticaOriginal)

        tacticaDTO.accion = Accion.ATAQUE_FISICO
        tacticaDTO.actualizarModelo(tacticaOriginal)

        Assertions.assertThat(tacticaOriginal.accion).isEqualTo(Accion.ATAQUE_FISICO)
    }
}