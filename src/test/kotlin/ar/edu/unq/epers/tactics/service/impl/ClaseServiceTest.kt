package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ClaseServiceTest {

    val claseDAO = Neo4JClaseDAO()
    private val claseService: ClaseServiceImpl = ClaseServiceImpl(claseDAO)

    @Test
    fun `cuando se crea una clase inicia con nombre`() {
        val nombre = "Aventurero"
        val nuevaClase = claseService.crearClase(nombre)

        assertThat(nuevaClase.nombre()).isEqualTo(nombre)
    }

    @Test
    fun `se puede agregar un requerimiento de clase a otra clase`() {
        val clasePredecesora = claseService.crearClase("Paladin")
        val claseSucesora = claseService.crearClase("Clerigo")

        claseService.requerir(clasePredecesora, claseSucesora)

        assertThat(claseDAO.requeridasDe(clasePredecesora))
            .usingRecursiveFieldByFieldElementComparator()
            .contains(claseSucesora)
    }

    @Test
    fun `dos clases no pueden requerirse entre si`() {
        val clasePredecesora = claseService.crearClase("Paladin")
        val claseSucesora = claseService.crearClase("Clerigo")
        claseService.requerir(clasePredecesora, claseSucesora)

        assertThatThrownBy { claseService.requerir(claseSucesora, clasePredecesora) }
            .hasMessageContaining("No se puede establecer una relacion bidireccional")
            .hasMessageContaining(clasePredecesora.nombre())
            .hasMessageContaining(claseSucesora.nombre())

        asertarQueNoSeCreoLaRelacionRequerirEntre(clasePredecesora, claseSucesora)
    }

    @Test
    fun `el grafo de clases no puede ser ciclico`(){
        val clasePredecesora = claseService.crearClase("Paladin")
        val claseDelMedio = claseService.crearClase("Guerrero de la Luz")
        val claseSucesora = claseService.crearClase("Clerigo")
        claseService.requerir(clasePredecesora, claseDelMedio)
        claseService.requerir(claseDelMedio, claseSucesora)

        assertThatThrownBy { claseService.requerir(claseSucesora, clasePredecesora) }
            .hasMessageContaining("No se puede establecer una relacion bidireccional")
            .hasMessageContaining(clasePredecesora.nombre())
            .hasMessageContaining(claseSucesora.nombre())
    }

    private fun asertarQueNoSeCreoLaRelacionRequerirEntre(claseRequerida: Clase, claseHabilitada: Clase) {
        assertThat(claseDAO.requiereEnAlgunNivelDe(claseRequerida, claseHabilitada))
    }

    @AfterEach
    internal fun tearDown() {
        claseDAO.clear()
    }
}

