package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ClaseServiceTest{

    val claseDAO = Neo4JClaseDAO()
    private val claseService: ClaseServiceImpl = ClaseServiceImpl(claseDAO)

    @Test
    fun `cuando se crea una clase inicia con nombre`() {
        val nombre = "Aventurero"
        val nuevaClase = claseService.crearClase(nombre)

        assertThat(nuevaClase.nombre()).isEqualTo(nombre)
    }

    @Test
    fun `cuando se crea una clase luego se la puede recuperar`(){
        val claseCreada = claseService.crearClase("Aventurero")

        assertThat(claseService.recuperarTodas()).usingRecursiveFieldByFieldElementComparator().contains(claseCreada)
    }

    @AfterEach
    internal fun tearDown() {
        claseDAO.clear()
    }
}

