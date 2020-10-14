package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PeleaTest {

    @Test
    fun `una pelea conoce el nombre de la party contra la que fue creada`(){
        val pelea = Pelea(Party("Malotes","fotito"),"Party enemiga")

        Assertions.assertThat(pelea.partyEnemiga()).isEqualTo("Party enemiga")
    }

}