package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBDataDAO
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
    fun `al recuperar partys ordenadas de un total de 20 partys se reciben de a 10`() {
        val setDePartys = crearSetDePartysConUnAventurero(20)
        val paginadas = partyService.recuperarOrdenadas(Orden.PODER,Direccion.ASCENDENTE,0)

        assertThat(paginadas.total).isEqualTo(setDePartys.size)
        assertThat(paginadas.parties.size).isEqualTo(10)
    }

    @Test
    fun `se pueden recuperar partys ordenadas por poder y de forma descendente`() {
        val (partyPolentaId, partySemiPolentaId, partyMedioMedioId) = generarParties()
        val partysPaginadas = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0).parties

        assertThat(partysPaginadas[0]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPolentaId))
        assertThat(partysPaginadas[1]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partySemiPolentaId))
        assertThat(partysPaginadas[2]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyMedioMedioId))

    }
    @Test
    fun `se pueden recuperar partys ordenadas por poder de forma ascendente`() {
        val (partyPolentaId, partySemiPolentaId, partyMedioMedioId) = generarParties()
        val partysPaginadas = partyService.recuperarOrdenadas(Orden.PODER,Direccion.ASCENDENTE,0).parties

        assertThat(partysPaginadas[0]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyMedioMedioId))
        assertThat(partysPaginadas[1]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partySemiPolentaId))
        assertThat(partysPaginadas[2]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPolentaId))

    }

    private fun generarParties(): Triple<Long, Long, Long> {
        val party = Party("Los capos", "URL")
        val party2 = Party("Los semi capos", "URL")
        val party3 = Party("Los medio medio", "URL")

        val partyPolentaId = partyService.crear(party).id()!!
        val partySemiPolentaId = partyService.crear(party2).id()!!
        val partyMedioMedioId = partyService.crear(party3).id()!!

        val aventureroPolenta = Aventurero("Fede", "URL", 80.0, 80.0, 80.0, 80.0)
        val aventureroSemiPolenta = Aventurero("Nacho", "URL", 50.0, 50.0, 50.0, 50.0)

        partyService.agregarAventureroAParty(partyPolentaId, aventureroPolenta)
        partyService.agregarAventureroAParty(partySemiPolentaId, aventureroSemiPolenta)
        return Triple(partyPolentaId, partySemiPolentaId, partyMedioMedioId)
    }

    @Test
    fun `se puede recuperar partys ordenadas por victorias de forma descendente`() {
        val partyIdGanadora = generarPartyQueHayaPeleado("Mi party",5,true)
        val otraPartyId = generarPartyQueHayaPeleado("Party",3,true)
        val partyPocoGanadoraId = generarPartyQueHayaPeleado("Party poco ganadora",1,true)

        val recuperadas = partyService.recuperarOrdenadas(Orden.VICTORIAS,Direccion.DESCENDENTE,0).parties

        assertThat(recuperadas[0]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyIdGanadora))
        assertThat(recuperadas[1]).usingRecursiveComparison().isEqualTo(partyService.recuperar(otraPartyId))
        assertThat(recuperadas[2]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPocoGanadoraId))
    }

    @Test
    fun `se puede recuperar partys ordenadas por victorias de forma ascendente`() {
        val partyIdGanadora = generarPartyQueHayaPeleado("Mi party",5,true)
        val otraPartyId = generarPartyQueHayaPeleado("Party",3,true)
        val partyPocoGanadoraId = generarPartyQueHayaPeleado("Party poco ganadora",1,true)

        val recuperadas = partyService.recuperarOrdenadas(Orden.VICTORIAS,Direccion.ASCENDENTE,0).parties

        assertThat(recuperadas[0]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPocoGanadoraId))
        assertThat(recuperadas[1]).usingRecursiveComparison().isEqualTo(partyService.recuperar(otraPartyId))
        assertThat(recuperadas[2]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyIdGanadora))
    }

    @Test
    fun `no se puede pedir paginas negativas`() {
        val exception = assertThrows<RuntimeException> { partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE, -1)}
        assertEquals("No puedes pedir paginas negativas", exception.message)
    }

    @Test
    fun `al recuperar las partys ordenadas de la segunda pagina las de la primera no aparecen`() {
        crearSetDePartysConUnAventurero(20)

        val recuperadasPrimeraPagina = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0)
        val recuperadasSegundaPagina = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,1)

        assertThat(recuperadasPrimeraPagina.parties)
                .allSatisfy { party1 -> recuperadasSegundaPagina.parties.none { party2 -> party2.id()!! == party1.id()!!}}

    }

    @Test
    fun `si no se escribe la pagina a buscar se devuelve la primera por defecto`() {
        crearSetDePartysConUnAventurero(10)

        val recuperadasEnNull = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,null)
        val recuperadasPrimeraPagina = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0)

        assertThat(recuperadasEnNull.parties).usingElementComparatorOnFields("id").containsAll(recuperadasPrimeraPagina.parties)
    }

    @Test
    fun `las parties se pueden ordenar descendentemente por la suma del poder total de sus aventureros`() {
        val party = partyService.crear(Party("Jamaica","foto-j.jpg"))
        partyService.agregarAventureroAParty(party.id()!!,
            Aventurero("FelipePigna","",
                90.0, 90.0,90.0,90.0))

        val party2 = partyService.crear(Party("Nueva Guinea","foto-ng.jpg"))
        partyService.agregarAventureroAParty(party2.id()!!,
            Aventurero("Olmedo","",
                50.0, 50.0,50.0,50.0))

        partyService.agregarAventureroAParty(party2.id()!!,
            Aventurero("Olmedo2","",
                50.0, 50.0,50.0,50.0))

        partyService.agregarAventureroAParty(party2.id()!!,
            Aventurero("Olmedo3","",
                50.0, 50.0,50.0,50.0))

        val partiesPaginadas = partyService.recuperarOrdenadas(Orden.PODER,Direccion.DESCENDENTE,0)

        assertThat(partiesPaginadas.parties[0].id()!!).isEqualTo(party2.id()!!)
        assertThat(partiesPaginadas.parties[1].id()!!).isEqualTo(party.id()!!)
    }

    @Test
    fun `se puede recuperar partys ordenadas por derrotas de manera descendente`() {
        val partyPerdedora1Id = generarPartyQueHayaPeleado("Party perdedora 1",4,false)
        val partyPerdedora2Id =generarPartyQueHayaPeleado("Party perdedora 2",2,false)
        val partyPerdedora3Id = generarPartyQueHayaPeleado("Party perdedora 3",1,false)

        val recuperadas = partyService.recuperarOrdenadas(Orden.DERROTAS,Direccion.DESCENDENTE,0).parties

        assertThat(recuperadas[0]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPerdedora1Id))
        assertThat(recuperadas[1]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPerdedora2Id))
        assertThat(recuperadas[2]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPerdedora3Id))

    }

    @Test
    fun `se puede recuperar partys ordenadas por derrotas de manera ascendente`() {
        val partyPerdedora1Id = generarPartyQueHayaPeleado("Party perdedora 1",4,false)
        val partyPerdedora2Id =generarPartyQueHayaPeleado("Party perdedora 2",2,false)
        val partyPerdedora3Id = generarPartyQueHayaPeleado("Party perdedora 3",1,false)

        val recuperadas = partyService.recuperarOrdenadas(Orden.DERROTAS,Direccion.ASCENDENTE,0).parties

        assertThat(recuperadas[0]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPerdedora3Id))
        assertThat(recuperadas[1]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPerdedora2Id))
        assertThat(recuperadas[2]).usingRecursiveComparison().isEqualTo(partyService.recuperar(partyPerdedora1Id))

    }

    private fun crearSetDePartysConUnAventurero(cantidadDePartys: Int): MutableList<Party>{
        var partyNumero = 1
        var aventureroNumero = 1
        val partys = mutableListOf<Party>()
        repeat(cantidadDePartys){
            val party = Party(("Party " + partyNumero), "URL")
            val partyId = partyService.crear(party).id()!!
            val aventurero = Aventurero(("Aventurero " + aventureroNumero),"URL", 10.0,10.0,10.0,10.0)
            partyService.agregarAventureroAParty(partyId,aventurero)
            partyNumero++
            aventureroNumero++
            partys.add(party)

        }
        return partys
    }

    private fun generarPartyQueHayaPeleado(nombreParty: String,cantidadDePeleas: Int, tieneAventureros:Boolean):Long{
        val peleaService = PeleaServiceImpl(HibernatePeleaDAO(),dao,HibernateAventureroDAO())
        val party = Party(nombreParty, "URL")
        val partyId = partyService.crear(party).id()!!
        if(tieneAventureros){
            val aventurero = Aventurero("Aventurero", "URL", 10.0, 10.0, 10.0, 10.0)
            partyService.agregarAventureroAParty(partyId,aventurero)
        }
        repeat(cantidadDePeleas) {
            val peleaId = peleaService.iniciarPelea(partyId, "Otra party").id()!!
            peleaService.terminarPelea(peleaId)
        }

        return partyId
    }

    @AfterEach
    fun tearDown() {
        DataServiceHelper(partyService).eliminarTodo()
        OrientDBDataDAO().clear()

    }

    private fun assertEqualParty(expectedParty: Party, obtainedParty: Party) {
        assertEquals(expectedParty.nombre(), obtainedParty.nombre())
        assertEquals(expectedParty.numeroDeAventureros(), obtainedParty.numeroDeAventureros())
    }

}
