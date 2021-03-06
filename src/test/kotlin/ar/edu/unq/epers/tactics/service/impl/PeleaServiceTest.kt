package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.EstadoPartida
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.dado.DadoSimulado
import ar.edu.unq.epers.tactics.modelo.enums.Accion
import ar.edu.unq.epers.tactics.modelo.enums.Criterio
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeEstadistica
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.modelo.habilidades.*
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDataDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBInventarioPartyDAO
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PeleaServiceTest {
    val peleaDAO = HibernatePeleaDAO()
    val partyDAO = HibernatePartyDAO()
    val aventureroDAO = HibernateAventureroDAO()
    val peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO, OrientDBInventarioPartyDAO())
    val partyService = PartyServiceImpl(partyDAO, OrientDBInventarioPartyDAO(), MongoFormacionDAO())
    val aventureroService = AventureroServiceImpl(aventureroDAO, partyDAO, MongoFormacionDAO())
    lateinit var party: Party
    var miniId = 0
    val nombreDePartyEnemiga = "Nombre de party enemiga"

    @BeforeEach
    fun setUp() {
        party = Party("Los geniales", "URL")
        partyService.crear(party)
    }

    @Test
    fun `una party inicialmente no esta en una pelea`() {
        assertFalse(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `una party puede comenzar una pelea`() {
        peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)

        assertTrue(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `una party que esta en pelea no puede entrar en otra`() {
        peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)

        val exception = assertThrows<RuntimeException> { peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga) }
        assertThat(exception.message).isEqualTo("No se puede iniciar una pelea: la party ya esta peleando")
    }

    @Test
    fun `un aventurero sabe resolver su turno`() {
        val curador = Aventurero("Fede", "", 10.0, 10.0, 10.0, 10.0)
        val aliado = Aventurero("Jorge", "", 10.0, 10.0, 10.0, 10.0)
        val manaOriginal = curador.mana()

        curador.agregarTactica(Tactica(1, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.CURAR))
        curador.agregarTactica(Tactica(2, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 3.0, Accion.CURAR))

        partyService.agregarAventureroAParty(party.id()!!, curador)
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, curador.id()!!, listOf())

        runTrx {
            val curadorLuegoDeResolverTurno = aventureroDAO.recuperar(curador.id()!!)
            assertThat(curadorLuegoDeResolverTurno.mana()).isEqualTo(manaOriginal - 5)
            assertTrue(habilidadGenerada is Curacion)
        }
    }

    @Test
    fun `un aventurero elige una tactica que ataca a un enemigo`() {
        val atacante = Aventurero("Fede", "", 10.0, 10.0, 10.0, 10.0)
        val partyEnemiga = Party("Enemigos","fotoEnemigo")
        partyService.crear(partyEnemiga)
        val enemigo = partyService.agregarAventureroAParty(partyEnemiga.id()!!,Aventurero("Kinoto"))
        val enemigos = listOf(enemigo)
        val tactica = Tactica(
            1,
            TipoDeReceptor.ENEMIGO,
            TipoDeEstadistica.VIDA,
            Criterio.MENOR_QUE,
            9999.0,
            Accion.ATAQUE_FISICO
        )
        atacante.agregarTactica(tactica)

        partyService.agregarAventureroAParty(party.id()!!, atacante)

        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, atacante.id()!!, enemigos) as Ataque

        assertThat(habilidadGenerada.aventureroReceptor.id()).isEqualTo(enemigo.id())
    }


    @Test
    fun `un aventurero resuelve su turno buscando la tactica que cumpla su criterio dependiendo de la prioridad `() {
        val aventurero = Aventurero("Fede", "", 10.0, 10.0, 10.0, 10.0)
        val tactica1 = Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MENOR_QUE, 9999.0, Accion.ATAQUE_FISICO)
        val tactica2 = Tactica(2, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.MEDITAR)
        aventurero.agregarTactica(tactica1)
        aventurero.agregarTactica(tactica2)

        partyService.agregarAventureroAParty(party.id()!!, aventurero)

        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, aventurero.id()!!, listOf()) as Meditacion

        assertThat(habilidadGenerada.aventureroReceptor.id()).isEqualTo(aventurero.id()!!)
    }


    @Test
    fun `un aventurero que resuelve su turno ejecuta la habilidad de curar sobre otro y este recibe la habilidad`() {
        val curador = Aventurero("Fede", "", 10.0, 10.0, 15.0, 10.0)
        val aliado = Aventurero("Jorge", "", 10.0, 10.0, 10.0, 10.0)

        curador.agregarTactica(Tactica(1, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.CURAR))

        partyService.agregarAventureroAParty(party.id()!!, curador)
        aliado.actualizarDa??oRecibido(20.0)
        val da??oRecibidoAntesDeCuracion = aliado.da??oRecibido()
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga).id()!!
        val habilidadGenerada = peleaService.resolverTurno(peleaId, curador.id()!!, listOf())

        peleaService.recibirHabilidad(peleaId, aliado.id()!!, habilidadGenerada)

        val da??oRecibidoEsperado = da??oRecibidoAntesDeCuracion - curador.poderMagico()
        val aliadoRecuperado = aventureroService.recuperar(aliado.id()!!)
        assertThat(aliado.id()!!).isEqualTo(aliadoRecuperado.id())
        assertEquals(da??oRecibidoEsperado, aliadoRecuperado.da??oRecibido())
    }

    @Test
    fun `una party puede salir de una pelea`() {
        val party = Party("Nombre", "")
        partyService.crear(party)
        val peleaId = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga).id()!!

        peleaService.terminarPelea(peleaId)

        assertFalse(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `una party no puede salir de una pelea si no esta en ninguna`() {
        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        peleaService.terminarPelea(pelea.id()!!)
        val exception = assertThrows<RuntimeException> { peleaService.terminarPelea(pelea.id()!!) }
        assertThat(exception).hasMessage("La pelea ya ha terminado antes.")
        assertFalse(peleaService.estaEnPelea(party.id()!!))
    }

    @Test
    fun `luego de una pelea, los aventureros de la party perdedora vuelven a tener su vida y mana como antes de comenzar a pelear`() {
        val aliado = Aventurero("Jorge", "", 10.0, 10.0, 10.0, 10.0)
        val vidaAntesDePelea = aliado.vidaActual()
        val manaAntesDePelea = aliado.mana()
        val enemigo = Aventurero("Francisco","URL",90.0,90.0,90.0,90.0)
        val partyEnemiga = Party("Los malos","URL")
        val partyEnemigaId = partyService.crear(partyEnemiga).id()!!

        partyService.agregarAventureroAParty(partyEnemigaId,enemigo)

        aliado.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.ATAQUE_MAGICO))
        partyService.agregarAventureroAParty(party.id()!!, aliado)

        val peleaId = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga).id()!!

        val habilidadGeneradaAliado = peleaService.resolverTurno(peleaId,aliado.id()!!, listOf(enemigo))
        val ataqueMagicoEnemigo = AtaqueMagico(enemigo.poderMagico(),enemigo.nivel(),enemigo,aliado,DadoSimulado(1))
        peleaService.recibirHabilidad(peleaId,enemigo.id()!!,habilidadGeneradaAliado)

        peleaService.recibirHabilidad(peleaId, aliado.id()!!, ataqueMagicoEnemigo)

        val ataqueFisicoEnemigo = Ataque(enemigo.da??oFisico(),enemigo.precisionFisica(),enemigo,aliado,DadoSimulado(1))

        peleaService.recibirHabilidad(peleaId, aliado.id()!!, ataqueFisicoEnemigo)

        val pelea = peleaService.terminarPelea(peleaId)

        assertThat(pelea.estadoPartida()).isEqualTo(EstadoPartida.PERDIDA)
        runTrx {
            assertThat(this.aventureroDAO.recuperar(aliado.id()!!).mana()).isEqualTo(manaAntesDePelea)
            assertThat(this.aventureroDAO.recuperar(aliado.id()!!).vidaActual()).isEqualTo(vidaAntesDePelea)
        }

    }


    @Test
    fun `cuando una party estuvo en multiples peleas se retorna la ultima`() {
        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        peleaService.terminarPelea(pelea.id()!!)

        val ultimaPelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        peleaService.terminarPelea(ultimaPelea.id()!!)
        runTrx {
            assertThat(peleaDAO.recuperarUltimaPeleaDeParty(party.id()!!).id()).isEqualTo(ultimaPelea.id()!!)
        }
    }
    
    @Test
    fun `cuando se recupera la party de un aventurero que tenga varias tacticas, este aparece una sola vez`() {
        val party = Party("Nombre de Party", "/img.jpg")
        val aventurero = Aventurero("Nombre de Aventurero", "", 1.0, 2.0, 3.0, 4.0)
        aventurero.agregarTactica(Tactica(1, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.CURAR))
        aventurero.agregarTactica(Tactica(2, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.CURAR))

        val partyID = partyService.crear(party).id()!!
        partyService.agregarAventureroAParty(partyID, aventurero)

        runTrx {
            val partyRecuperada = partyService.recuperar(partyID)
            assertEquals(1, partyRecuperada.numeroDeAventureros())
        }
    }

    @Test
    fun `un aventurero genera una habilidad nula cuando no puede aplicar ninguna de sus tacticas`() {
        val aventurero = Aventurero("Fede", "", 10.0, 10.0, 10.0, 10.0)
        aventurero.agregarTactica(Tactica(1, TipoDeReceptor.ALIADO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE,800.0, Accion.CURAR))
        val aventureroPersistido = partyService.agregarAventureroAParty(party.id()!!, aventurero)
        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)

        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, aventurero.id()!!, listOf())

        assertTrue(habilidadGenerada is HabilidadNula)
        assertThat(habilidadGenerada.aventureroReceptor.id()!!).isEqualTo(aventureroPersistido.id()!!)
    }

    @Test
    fun `un aventurero resuelve su turno y ejecuta su segunda tactica porque la primera no cumple el criterio`() {
        val emisor = Aventurero("Fede", "", 10.0, 10.0, 10.0, 10.0)

        emisor.agregarTactica(Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 200.0, Accion.ATAQUE_FISICO))
        emisor.agregarTactica(Tactica(2, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.ATAQUE_MAGICO))
        partyService.agregarAventureroAParty(party.id()!!, emisor)

        val enemigo = Aventurero("Pepe","URL",10.0,10.0,10.0,10.0)
        val partyEnemigo = Party("Los Capos", "URL")
        partyService.crear(partyEnemigo)
        partyService.agregarAventureroAParty(partyEnemigo.id()!!, enemigo)


        val pelea = peleaService.iniciarPelea(party.id()!!, nombreDePartyEnemiga)
        val habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, emisor.id()!!,listOf(enemigo))

        assertTrue(habilidadGenerada is AtaqueMagico)

    }

    @Test
    fun `no se puede aplicar una tactica a un aventurero muerto`() {
        val aventurero = Aventurero("Fede","URL",10.0,10.0,10.0,10.0)
        val otroAventurero = Aventurero("Cacho","URL",80.0,80.0,80.0,80.0)

        val partyEnemigo = Party("Los Capos", "URL")
        partyService.crear(partyEnemigo)

        val tacticaEnemigo = Tactica(
                1,TipoDeReceptor.ENEMIGO,
                TipoDeEstadistica.VIDA,
                Criterio.MENOR_QUE,100.0,Accion.ATAQUE_FISICO
        )

        otroAventurero.agregarTactica(tacticaEnemigo)

        partyService.agregarAventureroAParty(party.id()!!,aventurero)
        partyService.agregarAventureroAParty(partyEnemigo.id()!!,otroAventurero)

        val pelea = peleaService.iniciarPelea(partyEnemigo.id()!!, nombreDePartyEnemiga)
        var habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, otroAventurero.id()!!, listOf(aventurero))
        val aventureroDa??ado = peleaService.recibirHabilidad(pelea.id()!!, aventurero.id()!!, habilidadGenerada)
        habilidadGenerada = peleaService.resolverTurno(pelea.id()!!, otroAventurero.id()!!, listOf(aventureroDa??ado))

       assertTrue(habilidadGenerada is HabilidadNula)

    }

    @Test
    fun `al recuperar peleas ordenadas siempre se obtienen de a 10`(){
        repeat(15) { crearPeleas() }
        val partyId = party.id()!!

        assertThat(peleaService.recuperarOrdenadas(partyId, 0).peleas.size).isEqualTo(10)
    }

    @Test
    fun `al recuperar la segunda pagina de las peleas ordenadas las primeras 10 no aparecen`() {
        repeat(15) { crearPeleas() }
        val partyId = party.id()!!

        val peleasDeSegundaPagina = peleaService.recuperarOrdenadas(partyId, 1).peleas
        val peleasDePrimeraPagina = peleaService.recuperarOrdenadas(partyId, 0).peleas
        assertThat(peleasDeSegundaPagina).allSatisfy { each -> !peleasDePrimeraPagina.contains(each) }
    }

    @Test
    fun `no puedo pedir una pagina negativa de peleas ordenadas`(){
        assertThatThrownBy { peleaService.recuperarOrdenadas(party.id()!!, -1) }
                .hasMessage("No se puede pedir una pagina negativa")
    }

    @Test
    fun `si no se indica el numero de pagina al obtener peleas ordenadas se retorna la primera pagina`() {
        repeat(15) { crearPeleas() }
        val partyId = party.id()!!

        val peleasObtenidasSinPagina = peleaService.recuperarOrdenadas(partyId, null)
        val peleasObtenidasDePrimeraPagina = peleaService.recuperarOrdenadas(partyId, 0)

        assertThat(peleasObtenidasSinPagina.peleas.size).isEqualTo(10)
        assertThat(peleasObtenidasSinPagina.peleas)
                .usingElementComparatorOnFields("id")
                .containsAll(peleasObtenidasDePrimeraPagina.peleas)
    }

    @Test
    fun `las peleas obtenidas se obtienen ordenadas por fecha en orden descendente`() {
        repeat(10) { crearPeleas() }
        val partyId = party.id()!!

        val peleasObtenidas = peleaService.recuperarOrdenadas(partyId, 0)

        assertThat(peleasObtenidas.peleas[0].fecha()).isAfter(peleasObtenidas.peleas[9].fecha())
    }

    @Test
    fun `se obtiene la cantidad de peleas persistidas al buscarlas ordenadas`() {
        repeat(10) { crearPeleas() }
        val partyId = party.id()!!

        val partyEnemiga = Party("Los capos", "URL")
        partyService.crear(partyEnemiga)
        val pelea = peleaService.iniciarPelea(partyEnemiga.id()!!, partyEnemiga.nombre())
        peleaService.terminarPelea(pelea.id()!!)

        val peleasObtenidas = peleaService.recuperarOrdenadas(partyId, 0)

        assertThat(peleasObtenidas.total).isEqualTo(11)

    }

    private fun crearPeleas() {
        val partyEnemiga = Party("Los capos${miniId}", "URL")
        partyService.crear(partyEnemiga)
        val pelea = peleaService.iniciarPelea(party.id()!!, partyEnemiga.nombre())
        peleaService.terminarPelea(pelea.id()!!)
        miniId+=1
    }

    @Test
    fun `cuando una pelea termina y todos los aventureros estan muertos, se la marca como perdida`(){
        val aventurero = partyService.agregarAventureroAParty(party.id()!!, Aventurero("Cacho"))
        val pelea = peleaService.iniciarPelea(party.id()!!,"La otra party")
        peleaService.recibirHabilidad(
            pelea.id()!!,
            aventurero.id()!!,
            AtaqueMagico(123123.0,1,null,aventurero,DadoSimulado(20))
        )

        val peleaFinalizada = peleaService.terminarPelea(pelea.id()!!)


        assertEquals(peleaFinalizada.estadoPartida(), EstadoPartida.PERDIDA)
    }

    @Test
    fun `cuando una pelea termina y tiene aventureros vivos, se la marca como ganada`(){
        partyService.agregarAventureroAParty(party.id()!!, Aventurero("Cacho"))
        val pelea = peleaService.iniciarPelea(party.id()!!,"La otra party")

        val peleaFinalizada = peleaService.terminarPelea(pelea.id()!!)

        assertEquals(peleaFinalizada.estadoPartida(),EstadoPartida.GANADA)
    }

    @Test
    fun `una pelea que no termino esta en curso`() {
        partyService.agregarAventureroAParty(party.id()!!, Aventurero("Cacho"))
        val pelea = peleaService.iniciarPelea(party.id()!!,"La otra party")

        assertEquals(pelea.estadoPartida(),EstadoPartida.EN_CURSO)

    }

    @Test
    fun `cuando una pelea termina el aventurero de la party victoriosa sube de nivel y obtiene un punto de experiencia`() {
        val party = Party("Los fenomenos", "URL")
        val miPartyId = partyService.crear(party).id()!!
        val aventureroAntesDePelea = Aventurero("Pepe","URL",10.0,10.0,10.0,10.0)

        partyService.agregarAventureroAParty(miPartyId,aventureroAntesDePelea)

        val pelea = peleaService.iniciarPelea(miPartyId, "Party enemiga")
        val peleaTerminada = peleaService.terminarPelea(pelea.id()!!)
        val partyVictoriosa = peleaTerminada.party

        val aventureroDespuesDePelea = partyVictoriosa.aventureros().first{ it.id() == aventureroAntesDePelea.id()}


        assertThat(peleaTerminada.estadoPartida()).isEqualTo(EstadoPartida.GANADA)

        assertThat(partyVictoriosa.aventureros()).allSatisfy { it.nivel() > 1}

        assertThat(aventureroAntesDePelea.nivel()).isLessThan(aventureroDespuesDePelea.nivel())
        assertThat(aventureroAntesDePelea.experiencia()).isLessThan(aventureroDespuesDePelea.experiencia())



    }

    @AfterEach
    fun tearDown() {
        partyDAO.eliminarTodo()
        OrientDBDataDAO().clear()
    }
}