package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.exceptions.*
import ar.edu.unq.epers.tactics.service.impl.TiendaServicePersistente
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyMonedasDAOTest {
    val partyMonedasDAO = OrientDBPartyDAO()
    val itemDAO = OrientDBItemDAO()

    val tiendaService = TiendaServicePersistente(partyMonedasDAO, itemDAO)

    @BeforeEach
    fun setUp(){

    }

    @Test
    fun `se persiste un item`() {

    }

    @Test
    fun `se pueden registar partys`(){
        tiendaService.registrarParty(1, 500)

        var miParty = tiendaService.recuperar(1)

        assertThat(miParty.monedas).isEqualTo(500) //assert malisimo xd
    }

    @Test
    fun `no se puede registrar una party con un id existente`(){
        tiendaService.registrarParty(1,400)

        runTrx {
            val exception = assertThrows<PartyAlreadyRegisteredException> { partyMonedasDAO.registrar(1,400) }
            assertThat(exception.message).isEqualTo("La party 1 ya está en el sistema.")
        }
    }

    @Test
    fun `no se puede recuperar una party con un id sin registrar`(){
        runTrx {
            val exception = assertThrows<PartyUnregisteredException> { partyMonedasDAO.recuperar(6555) }
            assertThat(exception.message).isEqualTo("La party con id 6555 no se encuentra en el sistema.")
        }
    }

    @Test
    fun `no se puede recuperar un item con un nombre sin registrar`(){
        runTrx {
            val exception = assertThrows<InexistentItemException> { itemDAO.recuperar("Lanzallamas") }
            assertThat(exception.message).isEqualTo("No existe el item llamado Lanzallamas.")
        }
    }

    @Test
    fun `no se puede registrar un item con un nombre ya existente`(){
        runTrx {
            itemDAO.registrar("capa en llamas",400)

            val exception = assertThrows<ItemAlreadyRegisteredException> { itemDAO.registrar("capa en llamas",400) }
            assertThat(exception.message).isEqualTo("El item capa en llamas ya se encuentra en el sistema.")
        }
    }


    @Test
    fun `party compra item`(){
        //TODO: esto es más bien un test del service que de un dao, lo hice aca para simplificar tiempo
        // y testear esta funcionalidad

        val monedasAntesDeCompra = 500
        val precioItem = 200

        runTrx{
            partyMonedasDAO.registrar(1, monedasAntesDeCompra)
            itemDAO.registrar("bandera flameante", precioItem)
        }

        runTrx{
            partyMonedasDAO.comprar(1,"bandera flameante")
        }

        var partyRecuperada = runTrx { partyMonedasDAO.recuperar(1) }

        val monedasEsperadas = monedasAntesDeCompra - precioItem
        assertThat(partyRecuperada.monedas).isEqualTo(monedasEsperadas)
    }

    @Test
    fun `se levanta una excepcion al querer comprar un item de mas valor que las monedas de la party`(){
        //TODO: esto es más bien un test del service que de un dao, lo hice aca para simplificar tiempo
        // y testear esta funcionalidad

        runTrx { partyMonedasDAO.registrar(1,8) }

        runTrx {
            itemDAO.registrar("bandera flameante", 10)
        }

        runTrx{
            val exception = assertThrows<CannotBuyException> { partyMonedasDAO.comprar(1,"bandera flameante") }
            assertThat(exception.message).isEqualTo("No puedes comprar 'bandera flameante', te faltan 2 monedas.")
        }
    }

    @AfterEach
    fun tearDown(){
        OrientDBDataDAO().clear()
    }
}