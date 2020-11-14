package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.FormacionService
import ar.edu.unq.epers.tactics.service.PartyService
import helpers.Factory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FormacionServiceTest {
    lateinit var recursiveComparisonConfiguration: RecursiveComparisonConfiguration
    lateinit var claseDAO: Neo4JClaseDAO
    lateinit var aventureroService: AventureroService
    private val factory = Factory()
    lateinit var formacionService: FormacionService
    lateinit var formacionDAO: FormacionDAO
    lateinit var partyService: PartyService
    lateinit var claseService: ClaseService

    @BeforeEach
    fun setUp() {
        val partyDAO = HibernatePartyDAO()
        val aventureroDAO = HibernateAventureroDAO()
        aventureroService = AventureroServiceImpl(aventureroDAO, partyDAO)
        formacionDAO = MongoFormacionDAO()
        formacionService = FormacionServiceImpl(formacionDAO, HibernatePartyDAO())
        partyService = PartyServiceImpl(partyDAO)
        formacionService = FormacionServiceImpl(formacionDAO, partyService)
        claseDAO = Neo4JClaseDAO()
        claseService = ClaseServiceImpl(claseDAO, aventureroDAO)
        recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder().withIgnoredFields("id").build()
    }

    /*CREAR FORMACION*/
    @Test
    fun `se puede persistir una formacion`() {
        val requerimientos = mapOf<String, Int>(Pair("Gran Mago", 2), Pair("Magico", 5) )
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacionCreada = formacionService.crearFormacion("ForBidden", requerimientos, stats)
        //TODO: [REEMPLAZAR]
        // val formacionEsperada = formacionService.recuperarTodas()[0]
        val formacionEsperada = Formacion("ForBidden", requerimientos, stats)
        assertThat(formacionCreada).usingRecursiveComparison().ignoringFields("id").isEqualTo(formacionEsperada)
    }

    @Test
    fun `al querer crear una formacion con un nombre ya existente se levanta una excepcion`() {
        val requerimientos = mapOf<String, Int>(Pair("Gran Mago", 2), Pair("Magico", 5) )
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val exception = assertThrows<DuplicateFormationException> {
            formacionService.crearFormacion("ForBidden", requerimientos, stats)
        }

        assertThat(exception.message).isEqualTo("Ya existe una formacion con el nombre dado.")
    }

    /* ATRIBUTOS DE FORMACION QUE CORRESPONDEN A UNA PARTY */
    @Test
    fun `cuando una party no tiene aventureros no le corresponden atributos de formacion`() {
        val partyId = factory.nuevaPartyPersistida()

        formacionService.crearFormacion("Nombre de formacion", listOf(claseAventurero()), listOf(atributoDeFormacionFuerza()))

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        assertThat(atributosQueCorresponden).isEmpty()
    }

    @Test
    fun `cuando una party con aventureros cumple con los todos requisitos de una formacion le corresponden atributos de formacion`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.crearAventureroProficienteEnAventurero(partyId)

        val requerimientos = listOf(claseAventurero())
        val stats = listOf(atributoDeFormacionFuerza(), atributoDeFormacionInteligencia())
        formacionService.crearFormacion("Nombre de formacion", requerimientos, stats)

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        assertThat(atributosQueCorresponden).usingRecursiveComparison().isEqualTo(stats)
    }

    @Test
    fun `cuando una party con aventureros no cumple con los todos requisitos de una formacion no le corresponden atributos de esa formacion`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.crearAventureroProficienteEnAventurero(partyId)

        val requerimientos = listOf(claseAventurero(), claseMago())
        val stats = listOf(atributoDeFormacionFuerza())
        formacionService.crearFormacion("Nombre de formacion", requerimientos, stats)

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        assertThat(atributosQueCorresponden).isEmpty()
    }

    @Test
    fun `cuando una party obtiene el mismo atributo de formacion de distintas formaciones, estos se suman`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.crearAventureroProficienteEnAventurero(partyId)

        val requerimientos = listOf(claseAventurero())

        val statsConFuerzaYConsistencia = listOf(
            AtributoDeFormacion("Fuerza", 1),
            AtributoDeFormacion("Consistencia", 3)
        )

        val statsConFuerza = listOf(
            AtributoDeFormacion("Fuerza", 1)
        )

        formacionService.crearFormacion("Formacion 1", requerimientos, statsConFuerzaYConsistencia)
        formacionService.crearFormacion("Formacion 2", requerimientos, statsConFuerza)


        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        val statsEsperadas = listOf(
            AtributoDeFormacion("Fuerza", 2),
            AtributoDeFormacion("Consistencia", 3)
        )

        assertThat(atributosQueCorresponden.sortedBy { it.nombreAtributo })
            .usingRecursiveComparison().isEqualTo(statsEsperadas.sortedBy { it.nombreAtributo })
    }

    private fun atributoDeFormacionFuerza() = AtributoDeFormacion("Fuerza", 1)
    private fun atributoDeFormacionInteligencia() = AtributoDeFormacion("Inteligencia", 2)

    private fun claseAventurero() = Clase("Aventurero")

    private fun claseMago() = Clase("Mago")


    @Test
    fun `luego de persistir dos formaciones si las recupero obtengo las mismas dos que fueron persisitidas`() {
        val requerimientosFormacion1 = mapOf<String, Int>(Pair("Guerrero", 2), Pair("Guerrero", 5) )
        val statsFormacion1 = factory.crearStats(listOf("Fuerza" to 10, "Destreza" to 15))

        val requerimientosFormacion2 = mapOf<String, Int>(Pair("Gran Mago", 2), Pair("Magico", 5) )
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

    /*FORMACION QUE POSEE*/

    @Test
    fun `una party vacia no posee ninguna formacion`() {
        val requerimientos = mapOf<String, Int>(Pair("Gran Mago", 2), Pair("Magico", 5) )
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val party = Party("Los jefes", "url")
        val partyId = partyService.crear(party).id()

        assertThat(formacionService.formacionesQuePosee(partyId!!)).isEmpty()
    }

    @Test
    fun `una party puede no poseer una formacion por no tener la cantidad suficiente de clases`() {
        val requerimientos = mapOf<String, Int>(Pair("Gran Mago", 2), Pair("Magico", 5) )
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val party = crearPartyApropiadaParaFormacion()

        assertThat(formacionService.formacionesQuePosee(party.id()!!))
            .isEmpty()

    }

    @Test
    fun `una party puede poseer una formacion`() {
        val requerimientos = mapOf<String, Int>(Pair("Aventurero", 2), Pair("Magico", 1) )
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacion = formacionService.crearFormacion("ForBidden", requerimientos, stats)

        val party = crearPartyApropiadaParaFormacion()

        assertThat(formacionService.formacionesQuePosee(party.id()!!))
            .usingRecursiveFieldByFieldElementComparator(recursiveComparisonConfiguration)
            .contains(formacion)
    }

    @Test
    fun `una party puede poseer mas de una formacion`() {
        val requerimientosMagico = mapOf<String, Int>(Pair("Aventurero", 2), Pair("Magico", 1) )
        val requerimientosAventureros = mapOf<String, Int>(Pair("Aventurero", 3))
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacion1 = formacionService.crearFormacion("ForBidden", requerimientosMagico, stats)
        val formacion2 = formacionService.crearFormacion("Allowed", requerimientosAventureros, stats)

        val party = crearPartyApropiadaParaFormacion()

        assertThat(formacionService.formacionesQuePosee(party.id()!!))
            .usingRecursiveFieldByFieldElementComparator(recursiveComparisonConfiguration)
            .contains(formacion1, formacion2)
    }

    @Test
    fun `una party puede no poseer todas las formaciones`() {
        val requerimientosMagico = mapOf<String, Int>(Pair("Aventurero", 2), Pair("Magico", 1) )
        val requerimientosPaladin = mapOf<String, Int>(Pair("Paladin", 3))
        val stats = factory.crearStats(listOf("Inteligencia" to 20, "Constitucion" to 15))

        val formacion1 = formacionService.crearFormacion("ForBidden", requerimientosMagico, stats)
        val formacion2 = formacionService.crearFormacion("NotAllowed", requerimientosPaladin, stats)

        val party = crearPartyApropiadaParaFormacion()

        assertThat(formacionService.formacionesQuePosee(party.id()!!))
            .usingRecursiveFieldByFieldElementComparator(recursiveComparisonConfiguration)
            .contains(formacion1)
            .doesNotContain(formacion2)
    }

    private fun crearPartyApropiadaParaFormacion(): Party {
        val party = partyService.crear(Party("Los plus ultra", "url"))
        val aventurero1 = Aventurero("av1")
        val aventurero2 = Aventurero("av2")
        var magico = Aventurero("mag")
        partyService.agregarAventureroAParty(party.id()!!, aventurero1)
        partyService.agregarAventureroAParty(party.id()!!, aventurero2)
        magico = partyService.agregarAventureroAParty(party.id()!!, magico)
        magico.ganarPelea()
        aventureroService.actualizar(magico)

        claseService.crearClase("Aventurero")
        claseService.crearClase("Magico")
        claseService.crearMejora("Aventurero", "Magico", listOf(), 0)
        claseService.ganarProficiencia(magico.id()!!, "Aventurero", "Magico")
        return party
    }

    @AfterEach
    fun tearDown() {
        formacionDAO.deleteAll()
        claseDAO.clear()
    }
}