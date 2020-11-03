package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransactionRunner
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException

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

    @Test
    fun `cuando se crea una relacion que habilita pasar de clase aventurero a fisico se recupera una mejora`() {
        claseService.crearClase("Aventurero")
        claseService.crearClase("Fisico")
        val mejoraEsperada = Mejora("Aventurero","Fisico", listOf("Fuerza"),3)
        val mejoraRecuperada = claseService.crearMejora("Aventurero","Fisico", listOf<String>("Fuerza"),3)

        assertThat(mejoraEsperada).usingRecursiveComparison().isEqualTo(mejoraRecuperada)
    }

    @Test
    fun `no se puede crear una mejora bidireccional`() {
        claseService.crearClase("Aventurero")
        claseService.crearClase("Fisico")
        claseService.crearMejora("Aventurero","Fisico", listOf<String>("Fuerza"),3)

        val exception = assertThrows<RuntimeException> {
            claseService.crearMejora("Fisico","Aventurero", listOf<String>("Fuerza"),3)
        }
        assertThat(exception.message).isEqualTo("La mejora que estas queriendo crear no es posible")

    }

    @AfterEach
    internal fun tearDown() {
        claseDAO.clear()
    }
}

