package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClaseServiceTest{

    private val claseService: ClaseServiceImpl = ClaseServiceImpl(ClaseDAO())

    @Test
    fun `cuando se crea una clase inicia con nombre`() {
        val nombre = "Aventurero"
        val nuevaClase = claseService.crearClase(nombre)

        assertThat(nuevaClase.nombre()).isEqualTo(nombre)
    }

    @Test
    fun `cuando se crea una clase luego se la puede recuperar`(){
        val claseCreada = claseService.crearClase("Aventurero")

        assertThat(claseService.recuperarTodas()).contains(claseCreada)
    }
}

