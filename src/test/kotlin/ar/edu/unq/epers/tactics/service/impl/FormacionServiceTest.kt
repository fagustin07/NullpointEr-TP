package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.exceptions.DuplicateFormationException
import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBInventarioPartyDAO
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

        formacionDAO = MongoFormacionDAO()
        claseDAO = Neo4JClaseDAO()

        aventureroService = AventureroServiceImpl(aventureroDAO, partyDAO, MongoFormacionDAO())
        formacionService = FormacionServiceImpl(formacionDAO, HibernatePartyDAO())
        partyService = PartyServiceImpl(partyDAO, OrientDBInventarioPartyDAO(), MongoFormacionDAO())
        claseService = ClaseServiceImpl(claseDAO, aventureroDAO, MongoFormacionDAO())

        recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder().withIgnoredFields("id").build()
    }

    /*CREAR FORMACION*/
    @Test
    fun `se puede persistir una formacion`() {
        val requerimientos = mapOf(Pair("Gran Mago", 2), Pair("Magico", 5) )
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

        formacionService.crearFormacion("Nombre de formacion", mapOf(claseAventurero() to 1), listOf(atributoDeFormacionFuerza()))

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        assertThat(atributosQueCorresponden).isEmpty()
    }

    @Test
    fun `cuando una party con aventureros cumple con los todos requisitos de una formacion le corresponden atributos de formacion`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.crearAventureroProficienteEnAventurero(partyId)

        val requerimientos = mapOf(claseAventurero() to 1)
        val stats = listOf(atributoDeFormacionFuerza(), atributoDeFormacionInteligencia())
        formacionService.crearFormacion("Nombre de formacion", requerimientos, stats)

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        assertThat(atributosQueCorresponden).usingRecursiveComparison().isEqualTo(stats)
    }

    @Test
    fun `cuando una party con aventureros no cumple con los todos requisitos de una formacion no le corresponden atributos de esa formacion`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.crearAventureroProficienteEnAventurero(partyId)

        val requerimientos = mapOf(claseAventurero() to 1, claseMago() to 1)
        val stats = listOf(atributoDeFormacionFuerza())
        formacionService.crearFormacion("Nombre de formacion", requerimientos, stats)

        val atributosQueCorresponden = formacionService.atributosQueCorresponden(partyId)

        assertThat(atributosQueCorresponden).isEmpty()
    }

    @Test
    fun `cuando una party obtiene el mismo atributo de formacion de distintas formaciones, estos se suman`() {
        val partyId = factory.nuevaPartyPersistida()
        factory.crearAventureroProficienteEnAventurero(partyId)

        val requerimientos = mapOf(claseAventurero() to 1)

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

    private fun claseAventurero() = "Aventurero"

    private fun claseMago() = "Mago"


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
        val requerimientosMagico = mapOf(Pair("Aventurero", 2), Pair("Magico", 1) )
        val requerimientosPaladin = mapOf(Pair("Paladin", 3))
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

    @Test
    fun `cuando a una party cumple con los requisitos de una formacion sus aventureros incrementan el puntaje de los atributos que correspondan a la formacino`() {
        val puntosDeFuerzaInicial = 2.0
        val puntosDeFuerzaAGanar = 60
        val aventurero = Aventurero("Nombre de aventurero", fuerza=puntosDeFuerzaInicial)

        val requisitos = mapOf(aventurero.clases().first() to 1)
        val stats = listOf(AtributoDeFormacion("fuerza", puntosDeFuerzaAGanar))
        val formacion = Formacion("nombre de formacion", requisitos, stats)

        formacionDAO.guardar(formacion)
        val partyId = partyService.crear(Party("Nombre de party", "url")).id()!!


        val aventureroPersistido = partyService.agregarAventureroAParty(partyId, aventurero)


        assertThat(aventureroPersistido.fuerza()).isEqualTo((puntosDeFuerzaInicial + puntosDeFuerzaAGanar))
    }

    @Test
    fun `cuando un aventurero gana una clase requerida por una formacion los aventureros de la party reciben un incremento de atributos de formacion`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(10)
        val puntosDeFuerzaIniciales = aventureroConExperiencia.fuerza()
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        val puntosDeFuerzaAGanarPorFormacion = 60
        val claseRequeridaPorFormacion = claseMago()
        crearFormacionConRequisitoUnico(claseRequeridaPorFormacion, "fuerza", puntosDeFuerzaAGanarPorFormacion)
        crearMejoraQueNoIncrementeAtributos(claseDelAventurero, claseRequeridaPorFormacion)


        val aventureroConNuevaClase = claseService.ganarProficiencia(aventureroConExperiencia.id()!!, claseDelAventurero, claseRequeridaPorFormacion)


        assertThat(aventureroConNuevaClase.fuerza()).isEqualTo(puntosDeFuerzaIniciales + puntosDeFuerzaAGanarPorFormacion)
    }

    @Test
    fun `cuando un aventurero gana una clase que no es requerida por ninguna formacion, los aventureros de la party reciben un incremento de atributos de formacion`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(10)
        val puntosDeFuerzaIniciales = aventureroConExperiencia.fuerza()
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        val claseAMejorar = claseMago()
        crearMejoraQueNoIncrementeAtributos(claseDelAventurero, claseMago())

        val aventureroConNuevaClase = claseService.ganarProficiencia(aventureroConExperiencia.id()!!, claseDelAventurero, claseAMejorar)

        assertThat(aventureroConNuevaClase.fuerza()).isEqualTo(puntosDeFuerzaIniciales)
    }

    @Test
    fun `cuando una party pierde un aventurero y deja de cumplir con algun requisito de una formacion, la pierde y sus aventureros pierten puntos de atributos de formacion`() {
        val claseRequeridaPorFormacion = claseAventurero()
        crearFormacion(mapOf(claseRequeridaPorFormacion to 2), "fuerza", 66)

        val party = Party("Nombre de party", "")
        val pepe = Aventurero("Pepe")
        val juan = Aventurero("Juan")
        val puntosDeFuerzaIniciales = juan.fuerza()

        val partyId = partyService.crear(party).id()!!
        val aventureroAEliminar = partyService.agregarAventureroAParty(partyId, pepe)
        val aventureroADejarId = partyService.agregarAventureroAParty(partyId, juan).id()!!


        aventureroService.eliminar(aventureroAEliminar)


        val unicoAventurero = aventureroService.recuperar(aventureroADejarId)
        assertThat(unicoAventurero.fuerza()).isEqualTo(puntosDeFuerzaIniciales)
    }

    @Test
    fun `cuando una party pierde un aventurero y sigue cumpliendo con los requisitos de una formacion, no la pierde y sus aventureros siguien sumando atributos de formacion`() {
        crearFormacionConRequisitoUnico(claseAventurero(), "fuerza", 66)

        val party = Party("Nombre de party", "")
        val pepe = Aventurero("Pepe")
        val juan = Aventurero("Juan")

        val partyId = partyService.crear(party).id()!!
        val aventureroAEliminar = partyService.agregarAventureroAParty(partyId, pepe)
        val aventureroADejar = partyService.agregarAventureroAParty(partyId, juan)

        val puntosDeFuerzaAntesDeEliminarAventurero = aventureroADejar.fuerza()


        aventureroService.eliminar(aventureroAEliminar)


        val unicoAventurero = aventureroService.recuperar(aventureroADejar.id()!!)
        assertThat(unicoAventurero.fuerza()).isEqualTo(puntosDeFuerzaAntesDeEliminarAventurero)
    }

    private fun crearFormacionConRequisitoUnico(claseRequeridaPorFormacion: String, atributoAMejorar: String, puntosDeFuerzaAGanarPorFormacion: Int) {
        crearFormacion(mapOf(claseRequeridaPorFormacion to 1), atributoAMejorar, puntosDeFuerzaAGanarPorFormacion)
    }

    private fun crearFormacion(requisitos: Map<String, Int>, atributoAMejorar: String, puntosDeFuerzaAGanarPorFormacion: Int) {
        val stats = listOf(AtributoDeFormacion(atributoAMejorar, puntosDeFuerzaAGanarPorFormacion))
        val formacion = Formacion("Un nombre", requisitos, stats)
        formacionDAO.guardar(formacion)
    }

    private fun crearMejoraQueNoIncrementeAtributos(claseDePartida: String, claseAMejorar: String) {
        claseService.crearClase(claseDePartida)
        claseService.crearClase(claseAMejorar)
        claseService.crearMejora(claseDePartida, claseAMejorar, listOf(), 0)
    }

    @AfterEach
    fun tearDown() {
        formacionDAO.deleteAll()
        claseDAO.clear()
        OrientDBDataDAO().clear()
    }
}