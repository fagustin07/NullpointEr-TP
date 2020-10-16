package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import helpers.DataServiceHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyServiceTest {

    private val dao: PartyDAO = HibernatePartyDAO()
    private lateinit var partyService: PartyServiceImpl

    @BeforeEach
    fun setUp() {
        partyService = PartyServiceImpl(dao)
    }

    @Test
    fun inicialmenteNoHayNingunaPartyRegistrada() {
        assertTrue(partyService.recuperarTodas().isEmpty())
    }

    @Test
    fun seCreaExitosamenteUnaParty() {
        val party = Party("UnNombre", "URL")

        val partyId = partyService.crear(party).id()!!

        val todasLasParties = partyService.recuperarTodas()

        assertEquals(1, todasLasParties.size)
        assertEquals(partyId, todasLasParties[0].id())
        assertEqualParty(party, todasLasParties[0])
    }

    @Test
    fun sePuedeRecuperarUnaPartyConSuId() {
        val partyOriginal = Party("nombre de party", "URL")
        val partyId = partyService.crear(partyOriginal).id()!!

        val partyRecuperada = partyService.recuperar(partyId)

        assertEquals(partyId, partyRecuperada.id())
        assertEqualParty(partyOriginal, partyRecuperada)
    }

    @Test
    fun noSePuedeRecuperarUnaPartySiNoExisteNingunaPartyConElIdProvisto() {
        val idInvalido = 0L
        val exception = assertThrows<RuntimeException> { partyService.recuperar(idInvalido) }
        assertEquals("No existe Party con id ${idInvalido}", exception.message)
    }

    @Test
    fun alAgregarUnNuevoAventureroAUnaParty_aumentaLaCantidadDeAventurerosDeLaMisma() {
        val party = Party("Nombre de party", "URL")
        val aventurero = Aventurero("Pepe")
        val partyId = partyService.crear(party).id()!!

        partyService.agregarAventureroAParty(partyId, aventurero)
        val partyRecuperada = partyService.recuperar(partyId)

        assertEquals(1, partyRecuperada.numeroDeAventureros())
        assertNotEquals(party.numeroDeAventureros(), partyRecuperada.numeroDeAventureros())
    }

    @Test
    fun noSePuedeActualizarUnaPartyQueNoFuePersistida() {
        val party = Party("Nombre de party", "URL")

        val exception = assertThrows<RuntimeException> { partyService.actualizar(party) }
        assertEquals("No se puede actualizar una party que no fue persistida", exception.message)
    }

    @Test
    fun noSePuedeAgregarUnAventureroAUnaPartyQueNoFueCreada() {
        val aventurero = Aventurero("Pepe")
        val idNoRegistrado: Long = 45

        val exception = assertThrows<RuntimeException> { partyService.agregarAventureroAParty(idNoRegistrado, aventurero) }
        assertEquals("No existe Party con id ${idNoRegistrado}", exception.message)
    }

    @Test
    fun `al recuperar partys ordenadas se reciben de a 10`() {
        factoryDePartys(15)

        assertThat(partyService.recuperarOrdenadas(Orden.PODER,Direccion.ASCENDENTE,0).total).isEqualTo(10)
    }

    @Test
    fun `al recuperar partys ordenadas por poder y de forma descendente se obtienen de a 10`() {
        factoryDePartys(10)
        val party = Party("Los capos", "URL")
        val partyId = partyService.crear(party).id()!!
        val aventureroPolenta = Aventurero("Fede","URL",80.0,80.0,80.0,80.0)
        partyService.agregarAventureroAParty(partyId,aventureroPolenta)
        val partysPaginadas = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0)


        assertThat(partysPaginadas.total).isEqualTo(10)
        assertThat(partysPaginadas.parties[0].id()!!).isEqualTo(partyId)
    }

    @Test
    fun `al recuperar partys ordenadas por poder de forma ascendente se obtienen de a 10`() {
        factoryDePartys(10)
        val party = Party("Los capos", "URL")
        val partyId = partyService.crear(party).id()!!
        val aventureroDebil = Aventurero("Fede","URL",1.0,1.0,1.0,1.0)
        partyService.agregarAventureroAParty(partyId,aventureroDebil)
        val partysPaginadas = partyService.recuperarOrdenadas(Orden.PODER,Direccion.ASCENDENTE,0)


        assertThat(partysPaginadas.total).isEqualTo(10)
        assertThat(partysPaginadas.parties[0].id()!!).isEqualTo(partyId)
    }

    @Test
    fun `se puede recuperar partys ordenadas por victorias de forma descendente`() {
        val partyId = factoryDePeleas("Mi party",2)
        factoryDePeleas("Party",1)

        val recuperadas = partyService.recuperarOrdenadas(Orden.VICTORIAS,Direccion.DESCENDENTE,0)

        assertThat(recuperadas.total).isEqualTo(2)
        assertThat(recuperadas.parties[0].id()!!).isEqualTo(partyId)
    }

    @Test
    fun `no se puede pedir paginas negativas`() {
        factoryDePartys(10)

        val exception = assertThrows<RuntimeException> { partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE, -1)}
        assertEquals("No puedes pedir paginas negativas", exception.message)
    }

    @Test
    fun `al recuperar las partys ordenadas de la segunda pagina las de la primera no aparecen`() {
        factoryDePartys(20)

        val recuperadasPrimeraPagina = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0)
        val recuperadasSegundaPagina = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,1)

        assertThat(recuperadasPrimeraPagina.parties).allSatisfy { party -> !recuperadasSegundaPagina.parties.contains(party) }
    }

    @Test
    fun `si no se escribe la pagina a buscar se devuelve la primera por defecto`() {
        factoryDePartys(20)

        val recuperadasEnNull = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,null)
        val recuperadasPrimeraPagina = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0)

        assertThat(recuperadasEnNull.parties).allSatisfy { party -> recuperadasPrimeraPagina.parties.contains(party) }
    }

    private fun factoryDePeleas(nombreParty:String, cantidad:Int):Long {
        val peleaService = PeleaServiceImpl(HibernatePeleaDAO(),dao,HibernateAventureroDAO())
        val party = Party(nombreParty, "URL")
        val partyId = partyService.crear(party).id()!!
        val aventurero = Aventurero("Aventurero", "URL", 10.0, 10.0, 10.0, 10.0)
        partyService.agregarAventureroAParty(partyId,aventurero)
        repeat(cantidad) {
            val peleaId = peleaService.iniciarPelea(partyId, "Otra party").id()!!
            peleaService.terminarPelea(peleaId)
        }

        return partyId
    }

    private fun factoryDePartys(cantidad: Int){
        var partyNumero = 1
        var aventureroNumero = 1
        repeat(cantidad){
            val party = Party(("Party " + partyNumero), "URL")
            val partyId = partyService.crear(party).id()!!
            val aventurero = Aventurero(("Aventurero " + aventureroNumero),"URL", 10.0,10.0,10.0,10.0)
            partyService.agregarAventureroAParty(partyId,aventurero)
            partyNumero++
            aventureroNumero++
        }
    }

    @AfterEach
    fun tearDown() {
        DataServiceHelper(partyService).eliminarTodo()
    }

    private fun assertEqualParty(expectedParty: Party, obtainedParty: Party) {
        assertEquals(expectedParty.nombre(), obtainedParty.nombre())
        assertEquals(expectedParty.numeroDeAventureros(), obtainedParty.numeroDeAventureros())
    }



}
