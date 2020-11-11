package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import helpers.Factory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FormacionServiceTest {
    private val factory = Factory()
    lateinit var formacionService: FormacionService
    lateinit var formacionDAO: FormacionDAO

    @BeforeEach
    fun setUp() {
        formacionDAO = MongoFormacionDAO()
        formacionService = FormacionServiceImpl(formacionDAO)
    }

    /*CREAR FORMACION*/
    @Test
    fun `se puede crear una formacion`() {
        val requerimientos = factory.crearClases(listOf("Gran Mago" to 2, "Magico" to 5))
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacionCreada = formacionService.crearFormacion("ForBidden", requerimientos, stats)
        //TODO: [REEMPLAZAR]
        // val formacionEsperada = formacionService.recuperarTodas()[0]
        val formacionEsperada = Formacion("ForBidden", requerimientos, stats)
        assertThat(formacionCreada).usingRecursiveComparison().ignoringFields("id").isEqualTo(formacionEsperada)
    }

    @Test
    fun `al querer crear una formacion con un nombre ya existente se levanta una excepcion`() {
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