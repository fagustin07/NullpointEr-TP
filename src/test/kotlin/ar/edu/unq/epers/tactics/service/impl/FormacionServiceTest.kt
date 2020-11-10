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

    @Test
    fun `luego de persistir dos formaciones si las recupero obtengo las mismas dos que fueron persisitidas`() {
        val requerimientosFormacion1 = factory.crearClases(listOf("Guerrero" to 2, "Guerrero" to 5))
        val statsFormacion1 = factory.crearStats(listOf("Fuerza" to 10, "Destreza" to 15))

        val requerimientosFormacion2 = factory.crearClases(listOf("Gran Mago" to 2, "Magico" to 5))
        val statsFormacion2 = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacion1 = formacionService.crearFormacion("BoyScouts", requerimientosFormacion1, statsFormacion1)
        val formacion2 = formacionService.crearFormacion("ForBidden", requerimientosFormacion2, statsFormacion2)

        val formacionesPersistidas = listOf(formacion1,formacion2)
        val formacionesRecuperadas = formacionService.todasLasFormaciones()

        assertThat(formacionesRecuperadas).allMatch { formacionRecuperada -> formacionesPersistidas.any { formacionGenerada ->
            formacionGenerada.nombre == formacionRecuperada.nombre }}

    }

    @Test
    fun `al intentar recuperar todas las formaciones sin tener ninguna creada se obtiene una lista vacia`() {
        val formacionesRecuperadas = formacionService.todasLasFormaciones()
        assertThat(formacionesRecuperadas).isEmpty()
    }

    @AfterEach
    fun tearDown(){
        formacionDAO.deleteAll()
    }
}