package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import helpers.FactoryAventureroLeaderboardService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FormacionServiceTest {
    private val factory = FactoryAventureroLeaderboardService()
    lateinit var formacionService: FormacionService
    lateinit var formacionDAO: FormacionDAO

    @BeforeEach
    fun setUp() {
        formacionDAO = MongoFormacionDAO()
        formacionService = FormacionServiceImpl(formacionDAO)
    }

    /*CREAR FORMACION*/
    @Test
    fun `se puede persistir una formacion`() {
        val requerimientos = factory.crearClases(listOf("Gran Mago" to 2, "Magico" to 5))
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacionCreada = formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val formacionEsperada = Formacion("ForBidden", requerimientos, stats)
        assertThat(formacionCreada).usingRecursiveComparison().isEqualTo(formacionEsperada)
    }

    @Test
    fun `las formaciones persistidas no pueden tener el mismo nombre`() {
        val requerimientos = factory.crearClases(listOf("Gran Mago" to 2, "Magico" to 5))
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val exception = assertThrows<DuplicateFormationException>{
            formacionService.crearFormacion("ForBidden", requerimientos, stats)
        }

        assertThat(exception.message).isEqualTo("Ya existe una formacion con el nombre dado.")
    }

    @AfterEach
    fun tearDown(){
        formacionDAO.deleteAll()
    }
}