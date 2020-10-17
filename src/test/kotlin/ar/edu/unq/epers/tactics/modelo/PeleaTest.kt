package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PeleaTest {

    @Test
    fun `una pelea conoce el nombre de la party contra la que fue creada`(){
        val party = Party("Malotes", "foto.jpg")

        val pelea = Pelea(party,"Party enemiga")

        Assertions.assertThat(pelea.nombrePartyEnemiga()).isEqualTo("Party enemiga")
    }

}