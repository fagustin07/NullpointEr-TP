package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyMonedasDAOTest {
    val partyMonedasDAO = OrientDBPartyDAO()
    val itemDAO = OrientDBItemDAO()
    val party = PartyConMonedas(1, 500)

    @BeforeEach
    fun setUp(){
        runTrx{
            partyMonedasDAO.registrar(1, 500)
        }
    }

    @Test
    fun `se pueden registar partys`(){

        lateinit var miParty: PartyConMonedas
        runTrx {
            miParty = partyMonedasDAO.recuperar(1)

        }

        assertThat(miParty.monedas).isEqualTo(500) //assert malisimo xd
    }

    @Test
    fun `party compra item`(){
        //TODO: esto es más bien un test del service que de un dao, lo hice aca para simplificar tiempo
        // y testear esta funcionalidad

        val monedasAntesDeCompra = party.monedas
        val precioItem = 200
        runTrx {
            itemDAO.registrar("bandera flameante", 200)
        }

        runTrx{
            partyMonedasDAO.comprar(1,"bandera flameante")

        }

        lateinit var party : PartyConMonedas
        runTrx {
            party = partyMonedasDAO.recuperar(1)
        }

        val monedasEsperadas = monedasAntesDeCompra-precioItem
        assertThat(party.monedas).isEqualTo(monedasEsperadas)
    }

    @AfterEach
    fun tearDown(){
        OrientDBDataDAO().clear()
    }
}