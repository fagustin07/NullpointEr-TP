package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.neo4j.Neo4JClaseDAO
import helpers.FactoryAventureroLeaderboardService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ClaseServiceTest {
    val factory = FactoryAventureroLeaderboardService()

    private val NOMBRE_DE_CLASE_AVENTURERO = "Aventurero"
    private val NOMBRE_DE_CLASE_MAGO = "Mago"
    private val NOMBRE_DE_CLASE_FISICO = "Fisico"

    private val claseDAO = Neo4JClaseDAO()
    private val claseService: ClaseServiceImpl = ClaseServiceImpl(claseDAO, HibernateAventureroDAO())

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

        claseService.requerir(clasePredecesora.nombre(), claseSucesora.nombre())

        assertThat(claseDAO.requeridasDe(clasePredecesora))
            .usingRecursiveFieldByFieldElementComparator()
            .contains(claseSucesora)
    }

    @Test
    fun `dos clases no pueden requerirse entre si`() {
        val clasePredecesora = claseService.crearClase("Paladin")
        val claseSucesora = claseService.crearClase("Clerigo")
        claseService.requerir(clasePredecesora.nombre(), claseSucesora.nombre())

        assertThatThrownBy { claseService.requerir(claseSucesora.nombre(), clasePredecesora.nombre()) }
            .hasMessageContaining("No se puede establecer una relacion bidireccional")
            .hasMessageContaining(clasePredecesora.nombre())
            .hasMessageContaining(claseSucesora.nombre())

        assertTrue(claseDAO.requeridasDe(claseSucesora).isEmpty())
    }

    @Test
    fun `el grafo de clases no puede ser ciclico`(){
        val clasePredecesora = claseService.crearClase("Paladin")
        val claseDelMedio = claseService.crearClase("Guerrero de la Luz")
        val claseSucesora = claseService.crearClase("Clerigo")
        claseService.requerir(clasePredecesora.nombre(), claseDelMedio.nombre())
        claseService.requerir(claseDelMedio.nombre(), claseSucesora.nombre())

        assertThatThrownBy { claseService.requerir(claseSucesora.nombre(), clasePredecesora.nombre()) }
            .hasMessageContaining("No se puede establecer una relacion bidireccional")
            .hasMessageContaining(clasePredecesora.nombre())
            .hasMessageContaining(claseSucesora.nombre())
    }

    @Test
    fun `cuando se crea una relacion que habilita pasar de clase aventurero a fisico se recupera una mejora`() {
        claseService.crearClase("Aventurero")
        claseService.crearClase("Fisico")
        val mejoraEsperada = Mejora("Aventurero","Fisico", listOf(Atributo.FUERZA),3)
        val mejoraRecuperada = claseService.crearMejora("Aventurero","Fisico", listOf<Atributo>(Atributo.FUERZA),3)

        assertThat(mejoraEsperada).usingRecursiveComparison().isEqualTo(mejoraRecuperada)
    }

    @Test
    fun `una clase no puede habilitar a aquella que la habilita`() {
        claseService.crearClase("Aventurero")
        claseService.crearClase("Fisico")
        claseService.crearMejora("Aventurero","Fisico", listOf(Atributo.FUERZA),3)

        val exception = assertThrows<RuntimeException> {
            claseService.crearMejora("Fisico","Aventurero", listOf(Atributo.FUERZA),3)
        }
        assertThat(exception.message).isEqualTo("La mejora que estas queriendo crear no es posible")
    }

    // PUEDE MEJORAR
    @Test
    fun `un aventurero sin experiencia no puede mejorar`() {
        val aventureroSinExperiencia = factory.crearAventureroConExperiencia(0)
        val claseDelAventurero = aventureroSinExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        val mejora = claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.DESTREZA), 0)

        assertFalse(claseService.puedeMejorar(aventureroSinExperiencia.id()!!, mejora))
    }

    @Test
    fun `un aventurero con experiencia puede mejorar cuando es proficiente en la clase de la cual parte la mejora y la clase a mejorar no tiene ningun requerimiento`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        val mejora = claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.DESTREZA), 0)

        assertTrue(claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejora))
    }

    @Test
    fun `un aventurero con experiencia no puede mejorar cuando no es proficiente en la clase de la cual parte la mejora`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)

        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        val mejora = claseService.crearMejora(NOMBRE_DE_CLASE_FISICO, NOMBRE_DE_CLASE_MAGO, listOf(Atributo.DESTREZA), 0)

        assertFalse(claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejora))
    }

    @Test
    fun `no se puede preguntar si un aventurero puede mejorar cuando la mejora no existe`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        val mejoraExistente = claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_MAGO, listOf(Atributo.FUERZA), 1)

        val mejoraConInicioInvalido = Mejora("Inicio invalido", mejoraExistente.nombreDeLaClaseAMejorar(), mejoraExistente.atributos(), mejoraExistente.puntosAMejorar())
        val mejoraConClaseAMejorarInvalida = Mejora(mejoraExistente.nombreDeLaClaseInicio(), "A mejorar invalido", mejoraExistente.atributos(), mejoraExistente.puntosAMejorar())
        val mejoraConAtributosInvalidos = Mejora(mejoraExistente.nombreDeLaClaseInicio(), mejoraExistente.nombreDeLaClaseAMejorar(), listOf(), mejoraExistente.puntosAMejorar())
        val mejoraConAtributosPuntosAMejorarInvalidos = Mejora(mejoraExistente.nombreDeLaClaseInicio(), mejoraExistente.nombreDeLaClaseAMejorar(), mejoraExistente.atributos(), 9999)

        assertThatThrownBy { claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejoraConInicioInvalido) }.hasMessageContaining("No existe la mejora")
        assertThatThrownBy { claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejoraConClaseAMejorarInvalida) }.hasMessageContaining("No existe la mejora")
        assertThatThrownBy { claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejoraConAtributosInvalidos) }.hasMessageContaining("No existe la mejora")
        assertThatThrownBy { claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejoraConAtributosPuntosAMejorarInvalidos) }.hasMessageContaining("No existe la mejora")
    }

    @Test
    fun `un aventurero no puede mejorar cuando no es proficiente en una clase requerida por la clase a mejorar`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        val mejora = claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.FUERZA), 0)

        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        claseService.requerir(mejora.nombreDeLaClaseAMejorar(), NOMBRE_DE_CLASE_MAGO)

        assertFalse(claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejora))
    }

    @Test
    fun `un aventurero puede mejorar cuando es proficiente en la clase que habilita una mejorar y en todas las clases requeridas por la clase a mejorar`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        val mejora = claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.FUERZA), 0)

        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        claseService.requerir(claseDelAventurero, mejora.nombreDeLaClaseAMejorar())

        assertTrue(claseService.puedeMejorar(aventureroConExperiencia.id()!!, mejora))
    }

    // POSIBLES MEJORAS
    @Test
    fun `cuando un aventurero es proficiente en una clase que habilita una mejora, pero no tiene experiencia, no existen posibles mejoras para el`() {
        val aventureroSinExperiencia = factory.crearAventureroConExperiencia(0)
        val claseDelAventurero = aventureroSinExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.FUERZA), 0)

        val posiblesMejoras = claseService.posiblesMejoras(aventureroSinExperiencia.id()!!)

        assertTrue(posiblesMejoras.isEmpty())
    }

    @Test
    fun `cuando un aventurero con experiencia es proficiente en una clase que habilita una mejora sin requisitos, existen posibles mejoras para el`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        val mejora = claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.FUERZA), 0)

        val posiblesMejoras = claseService.posiblesMejoras(aventureroConExperiencia.id()!!)

        assertEquals(1, posiblesMejoras.size)
        assertThat(posiblesMejoras.first()).usingRecursiveComparison().isEqualTo(mejora)
    }

    @Test
    fun `cuando un aventurero con experiencia es proficiente en una clase que habilita una mejora, pero no cumple con algun requisito, dicha mejora no esta disponible para el`() {
        val aventureroConExperiencia = factory.crearAventureroConExperiencia(1)
        val claseDelAventurero = aventureroConExperiencia.clases().first()

        claseService.crearClase(claseDelAventurero)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearMejora(claseDelAventurero, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.FUERZA), 0)

        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        claseService.requerir(NOMBRE_DE_CLASE_FISICO, NOMBRE_DE_CLASE_MAGO)

        val posiblesMejoras = claseService.posiblesMejoras(aventureroConExperiencia.id()!!)

        assertTrue(posiblesMejoras.isEmpty())
    }

    /*OBTENER MEJORA*/
    @Test
    fun `un aventurero puede adquirir una nueva clase`(){
        val aventureroConExp = factory.crearAventureroConExperiencia(3)
        val atributosMejorables = listOf(Atributo.FUERZA, Atributo.CONSTITUCION)
        claseService.crearClase(NOMBRE_DE_CLASE_AVENTURERO)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO, atributosMejorables, 4)
        claseService.requerir(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO)

        val aventureroMejorado = claseService.ganarProficiencia(aventureroConExp.id()!!,NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO)

        assertTrue(aventureroMejorado.clases().contains("Fisico"))
    }

    @Test
    fun `un aventurero puede adquirir mejoras en sus atributos ganando una nueva proficiencia`(){
        val aventureroConExp = factory.crearAventureroConExperiencia(3)
        val fuerzaAntesDeMejora = aventureroConExp.fuerza()
        val constitucionAntesDeMejora = aventureroConExp.constitucion()
        val atributosMejorables = listOf(Atributo.FUERZA,Atributo.CONSTITUCION)
        claseService.crearClase(NOMBRE_DE_CLASE_AVENTURERO)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO, atributosMejorables, 4)
        claseService.requerir(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO)

        val aventureroMejorado = claseService.ganarProficiencia(aventureroConExp.id()!!,NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO)

        assertThat(aventureroMejorado.fuerza()).isEqualTo(fuerzaAntesDeMejora + 4)
        assertThat(aventureroMejorado.constitucion()).isEqualTo(constitucionAntesDeMejora + 4)
    }

    @Test
    fun `un aventurero no puede ganar una proficiencia si no existe una mejora que lo avale`(){
        val aventurero = factory.crearAventureroConExperiencia(0)

        val exception = assertThrows<RuntimeException> {
            claseService.ganarProficiencia(aventurero.id()!!,NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_MAGO)
        }
        assertThat(exception.message).isEqualTo("La mejora de Aventurero hacia Mago no existe.")
    }

    @Test
    fun `un aventurero no puede ganar una proficiencia si no posee la experiencia necesaria`(){
        val aventurero = factory.crearAventureroConExperiencia(0)
        claseService.crearClase(NOMBRE_DE_CLASE_AVENTURERO)
        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO, listOf(Atributo.INTELIGENCIA), 4)
        claseService.requerir(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO)

        val exception = assertThrows<RuntimeException> {
            claseService.ganarProficiencia(aventurero.id()!!,NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO)
        }
        assertThat(exception.message).isEqualTo("El aventurero no cumple las condiciones para obtener una mejora.")
    }

    /** CAMINO MAS RENTABLE **/ // TODO: de aca para abajo los nombres de tests son un desastre. la forma de testear que se fue por el mejor camino no esta muy buena
    @Test
    fun `cuando niguna clase del aventurero habilita ninguna mejora, no existe un camino mas rentable`() {
        val aventureroId = factory.crearAventureroConExperiencia(0).id()!!
        claseService.crearClase(NOMBRE_DE_CLASE_AVENTURERO)

        val camino = claseService.caminoMasRentable(1, aventureroId, Atributo.FUERZA )
        assertTrue(camino.isEmpty())
    }

    @Test
    fun `cuando una clase del aventurero habilita una mejora que otorga fuerza esta forma parte del camino mas rentable`() {
        val aventureroId = factory.crearAventureroConExperiencia(0).id()!!

        claseService.crearClase(NOMBRE_DE_CLASE_AVENTURERO)
        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        val mejora = claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_MAGO, listOf(Atributo.FUERZA), 10)

        val caminoMasRentable = claseService.caminoMasRentable(1, aventureroId, Atributo.FUERZA)

        assertEquals(1, caminoMasRentable.size)
        assertEquals(
            mejora.puntosAMejorar(),
            caminoMasRentable.sumBy { it.puntosAMejorar() }
        )
    }

    @Test
    fun `cuando una clase del aventurero habilita varias mejoras y un solo camino otorga fuerza se va por ese`() {
        val aventureroId = factory.crearAventureroConExperiencia(0).id()!!

        claseService.crearClase(NOMBRE_DE_CLASE_AVENTURERO)

        claseService.crearClase(NOMBRE_DE_CLASE_MAGO)
        val mejoraConFuerza = claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_MAGO, listOf(Atributo.FUERZA), 10)

        claseService.crearClase(NOMBRE_DE_CLASE_FISICO)
        claseService.crearMejora(NOMBRE_DE_CLASE_AVENTURERO, NOMBRE_DE_CLASE_FISICO, listOf(), 0)

        val caminoMasRentable = claseService.caminoMasRentable(1, aventureroId, Atributo.FUERZA)

        assertEquals(1, caminoMasRentable.size)
        assertEquals(
            mejoraConFuerza.puntosAMejorar(),
            caminoMasRentable.sumBy { it.puntosAMejorar() }
        )
    }

    @AfterEach
    internal fun tearDown() {
        PartyServiceImpl(HibernatePartyDAO()).eliminarTodo()
        claseDAO.clear()
    }
}

