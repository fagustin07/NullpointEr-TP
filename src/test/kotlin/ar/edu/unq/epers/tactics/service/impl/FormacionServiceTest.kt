package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import helpers.FactoryAventureroLeaderboardService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FormacionServiceTest {
    private val factory = FactoryAventureroLeaderboardService()
    lateinit var formacionService: FormacionService

    @BeforeEach
    fun setUp() {
        formacionService = FormacionServiceImpl(MongoFormacionDAO())
    }

    @Test
    fun `se puede crear una formacion`() {
        val requerimientos = factory.crearClases(listOf("Gran Mago" to 2, "Magico" to 5))
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacionCreada = formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val formacionEsperada = Formacion("ForBidden", requerimientos, stats)
        assertThat(formacionEsperada).usingRecursiveComparison().isEqualTo(formacionCreada)
    }
}