package ar.edu.unq.epers.tactics.modelo.enums

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TipoDeReceptorTest {

    private lateinit var partyDeEmisor: Party
    private lateinit var partyEnemiga: Party

    private lateinit var emisor: Aventurero
    private lateinit var aliado: Aventurero
    private lateinit var enemigo: Aventurero

    // TODO: los tests y la implementacion son una chanchada total
    @BeforeEach
    fun setUp() {
        partyDeEmisor = Party("Party del emisor", "/party.jpg")
        emisor = Aventurero("Emisor")
        aliado = Aventurero("Compañero")

        partyDeEmisor.agregarUnAventurero(emisor)
        partyDeEmisor.agregarUnAventurero(aliado)

        partyEnemiga = Party("Party enemiga", "/party.jpg")
        enemigo = Aventurero("Enemigo")
        partyEnemiga.agregarUnAventurero(enemigo)
    }

    @Test
    fun ALIADO_asdasdasd1111222333() {
        assertTrue(TipoDeReceptor.ALIADO.test(emisor, aliado))
        assertFalse(TipoDeReceptor.ALIADO.test(emisor, emisor))
        assertFalse(TipoDeReceptor.ALIADO.test(emisor, enemigo))
    }

    @Test
    fun ENEMIGO_asdasdasd1111222333() {
        assertTrue(TipoDeReceptor.ENEMIGO.test(emisor, enemigo))
        assertFalse(TipoDeReceptor.ENEMIGO.test(emisor, aliado))
        assertFalse(TipoDeReceptor.ENEMIGO.test(emisor, emisor))

    }

    @Test
    fun UNO_MISMO_asdasdasd1111222333() {
        assertTrue(TipoDeReceptor.UNO_MISMO.test(emisor, emisor))
        assertFalse(TipoDeReceptor.UNO_MISMO.test(emisor, enemigo))
        assertFalse(TipoDeReceptor.UNO_MISMO.test(emisor, aliado))
    }

}