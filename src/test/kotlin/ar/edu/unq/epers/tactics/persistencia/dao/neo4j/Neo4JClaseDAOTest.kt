package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Clase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class Neo4JClaseDAOTest {

    private val NOMBRE_DE_CLASE_AVENTURERO = "Aventurero"
    private val NOMBRE_DE_CLASE_MAGO = "Mago"
    private val NOMBRE_DE_CLASE_FISICO = "Fisico"

    private val claseDAO = Neo4JClaseDAO()

    @Test
    fun `escenario planteado en issue un poco mas complejo`() {
        val NOMBRE_DE_CLASE_FISICO99 = "NOMBRE_DE_CLASE_FISICO99"

        claseDAO.crear(Clase(NOMBRE_DE_CLASE_AVENTURERO))
        claseDAO.crear(Clase(NOMBRE_DE_CLASE_MAGO))
        claseDAO.crear(Clase(NOMBRE_DE_CLASE_FISICO99))
        claseDAO.crear(Clase(NOMBRE_DE_CLASE_FISICO))
        claseDAO.crear(Clase("Gran Patriarca"))

        claseDAO.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_MAGO, listOf(), 1)
        claseDAO.crearMejora(NOMBRE_DE_CLASE_MAGO, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.FUERZA), 66)
        claseDAO.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO99, listOf(Atributo.FUERZA), 99)
        claseDAO.crearMejora(
            NOMBRE_DE_CLASE_FISICO,
            "Gran Patriarca",
            listOf(Atributo.FUERZA, Atributo.INTELIGENCIA, Atributo.DESTREZA, Atributo.CONSTITUCION),
            100
        )
        val caminoMasRentable = claseDAO.caminoMasRentable(5, setOf("Aventurero"), Atributo.FUERZA)

        assertThat(caminoMasRentable.size).isEqualTo(3)
        assertThat(caminoMasRentable[0].nombreDeLaClaseAMejorar()).isEqualTo(NOMBRE_DE_CLASE_MAGO)
        assertThat(caminoMasRentable[1].nombreDeLaClaseAMejorar()).isEqualTo(NOMBRE_DE_CLASE_FISICO)
        assertThat(caminoMasRentable[2].nombreDeLaClaseAMejorar()).isEqualTo("Gran Patriarca")

        assertThat(caminoMasRentable.sumBy { mejora ->
            if (mejora.atributos().contains(Atributo.FUERZA))
                mejora.puntosAMejorar()
            else {
                0
            }
        }
        )
            .isEqualTo(166)
    }

    @AfterEach
    internal fun tearDown() {
        claseDAO.clear()
    }
}