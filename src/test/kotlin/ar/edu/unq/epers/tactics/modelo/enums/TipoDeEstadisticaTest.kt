package ar.edu.unq.epers.tactics.modelo.enums

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TipoDeEstadisticaTest {
    lateinit var party: Party
    lateinit var aventurero: Aventurero

    @BeforeEach
    fun setUp() {
        party = Party("Nombre de party", "/foto.jpg")
        aventurero = Aventurero("Nombre", "",1, 2, 3, 4)
    }

    @Test
    fun VIDA() {
        Assertions.assertEquals(aventurero.vidaActual(), TipoDeEstadistica.VIDA.valorPara(aventurero))
    }

    @Test
    fun ARMADURA() {
        Assertions.assertEquals(aventurero.armadura(), TipoDeEstadistica.ARMADURA.valorPara(aventurero))
    }

    @Test
    fun MANA() {
        Assertions.assertEquals(aventurero.mana(), TipoDeEstadistica.MANA.valorPara(aventurero))
    }

    @Test
    fun VELOCIDAD() {
        Assertions.assertEquals(aventurero.velocidad(), TipoDeEstadistica.VELOCIDAD.valorPara(aventurero))
    }

    @Test
    fun DAÑO_FISICO() {
        Assertions.assertEquals(aventurero.dañoFisico(), TipoDeEstadistica.DAÑO_FISICO.valorPara(aventurero))
    }

    @Test
    fun DAÑO_MAGICO() {
        Assertions.assertEquals(aventurero.poderMagico(), TipoDeEstadistica.DAÑO_MAGICO.valorPara(aventurero))
    }

    @Test
    fun PRECISION_FISICA() {
        Assertions.assertEquals(aventurero.precisionFisica(), TipoDeEstadistica.PRECISION_FISICA.valorPara(aventurero))
    }

}
