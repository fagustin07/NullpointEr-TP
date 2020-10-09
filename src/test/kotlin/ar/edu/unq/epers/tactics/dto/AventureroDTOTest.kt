package ar.edu.unq.epers.tactics.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.enums.Accion
import ar.edu.unq.epers.tactics.modelo.enums.Criterio
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeEstadistica
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import ar.edu.unq.epers.tactics.service.dto.TacticaDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AventureroDTOTest {

    @Test
    fun `Al convertir un Aventurero a un AventureroDTO y de nuevo a un Aventurero se obtienen objetos similares`() {
        val aventureroOriginal = Aventurero("Pepe", "", 40.0, 50.0, 60.0, 70.0)
        val curarse = Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 30.0, Accion.CURAR)
        val rematar =
            Tactica(2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 15.0, Accion.ATAQUE_MAGICO)
        aventureroOriginal.agregarTactica(curarse)
        aventureroOriginal.agregarTactica(rematar)
        val aventureroDTO = AventureroDTO.desdeModelo(aventureroOriginal)
        val aventureroDesdeDTO = aventureroDTO.aModelo()

        assertThat(aventureroDesdeDTO).usingRecursiveComparison().isEqualTo(aventureroOriginal)
    }

    @Test
    fun `Al enviar el mensaje actualizar a un AventureroDTO se actualiza el objeto de dominio`() {
        val aventurero = Aventurero("Pepe", "", 40.0, 50.0, 60.0, 70.0)
        val aventureroDTO = AventureroDTO.desdeModelo(aventurero)

        aventureroDTO.atributos.fuerza = 56.0
        aventureroDTO.atributos.inteligencia = 2.0
        val curarse = Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 30.0, Accion.CURAR)
        aventureroDTO.tacticas = mutableListOf(TacticaDTO.desdeModelo(curarse))
        aventureroDTO.actualizarModelo(aventurero)

        assertThat(aventurero.fuerza()).isEqualTo(56.0)
        assertThat(aventurero.inteligencia()).isEqualTo(2.0)
        assertThat(aventurero.tacticas().size).isEqualTo(1)
    }
}