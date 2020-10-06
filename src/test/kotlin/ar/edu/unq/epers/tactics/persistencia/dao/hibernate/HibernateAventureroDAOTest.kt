package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class HibernateAventureroDAOTest {
    private val aventureroDAO = HibernateAventureroDAO()
    private var partyDAO = HibernatePartyDAO()
    private lateinit var pepito : Aventurero
    private lateinit var bigTeam : Party

    @BeforeEach
    fun setUp() {
        bigTeam = Party("MC-nificos", "soldados.jpg")
        pepito = Aventurero("Pepito")
    }

    @Test
    fun alRecuperarUnAventureroSeObtienenObjetosSimilares() {
        HibernateTransactionRunner.runTrx {
            val pepitoId = generateModel()
            val recoveryPepito = aventureroDAO.recuperar(pepitoId)

            assertThat(pepito).usingRecursiveComparison().isEqualTo(recoveryPepito)
        }
    }

    @Test
    fun alEliminarUnAventureroPersistidoYLuegoRecuperarloNoExiste() {
        HibernateTransactionRunner.runTrx {
            val pepitoId = aventureroDAO.crear(pepito).id()!!
            aventureroDAO.eliminar(pepito)

            val exception = assertThrows<RuntimeException> {
                aventureroDAO.recuperar(pepitoId)
            }
            assertEquals(exception.message, "No entity found for query")
        }
    }

    private fun generateModel(): Long {
        val pepitoId = aventureroDAO.crear(pepito).id()!!
        bigTeam.agregarUnAventurero(pepito)
        partyDAO.crear(bigTeam)
        return pepitoId
    }

    @AfterEach
    fun clear() {
        aventureroDAO.eliminarTodo()
    }
}