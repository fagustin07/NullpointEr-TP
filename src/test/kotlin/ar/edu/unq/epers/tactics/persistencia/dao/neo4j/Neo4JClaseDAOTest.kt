package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Clase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.util.comparator.Comparators

class Neo4JClaseDAOTest {

    private val claseDAO: Neo4JClaseDAO = Neo4JClaseDAO()

    @Test
    fun `cuando se persiste una clase y luego se la recupera se obtienen objetos similares`() {
        val clase = Clase("Aventurero")

        claseDAO.crear(clase)

        assertThat(claseDAO.recuperarTodas())
            .usingRecursiveFieldByFieldElementComparator()
            .contains(clase)
    }

    @AfterEach
    fun tearDown() {
        claseDAO.clear()
    }
}