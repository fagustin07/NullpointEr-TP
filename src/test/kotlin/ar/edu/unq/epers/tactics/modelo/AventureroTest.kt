package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class AventureroTest {
    lateinit var cacho: Aventurero

    @BeforeEach
    fun setUp() {
        cacho = Aventurero("Cacho","", 45.0, 10.0, 20.0, 17.0)

    }

    @Test
    fun inicialmenteEsNivelUno() {
        assertEquals(1, cacho.nivel())
    }

    @Test
    fun tieneAtributos() {
        assertEquals(45.0, cacho.fuerza())
        assertEquals(10.0, cacho.destreza())
        assertEquals(20.0, cacho.inteligencia())
        assertEquals(17.0, cacho.constitucion())
    }

    @Test
    fun sePuedeActualizarEnBaseALosAtributosDeOtroAventurero() {
        val otroAventurero = Aventurero("Otro aventurero", "xxx", 1.0, 2.0, 3.0, 4.0)
        cacho.actualizarse(otroAventurero)

        assertEquals(otroAventurero.fuerza(), cacho.fuerza())
        assertEquals(otroAventurero.destreza(), cacho.destreza())
        assertEquals(otroAventurero.inteligencia(), cacho.inteligencia())
        assertEquals(otroAventurero.constitucion(), cacho.constitucion())
    }


    @Test
    fun sabeCuantaVidaTiene() = assertEquals(84.0, cacho.vidaActual())

    @Test
    fun sabeCuantaArmaduraTiene() = assertEquals(18.0, cacho.armadura())

    @Test
    fun sabeCuantoManaTiene() = assertEquals(21.0, cacho.mana())

    @Test
    fun sabeCuantoDañoFisicoTiene() = assertEquals(51.0, cacho.dañoFisico())

    @Test
    fun sabeCuantoPoderMagicoTiene() = assertEquals(22.0, cacho.poderMagico())

    @Test
    fun sabeCuantaPrecisionFisica() = assertEquals(56.0, cacho.precisionFisica())

    @Test
    fun sabeCuantaVelocidadTiene() = assertEquals(11.0, cacho.velocidad())

    @Test
    fun inicialmenteNoTieneNingunAliado() {
        assertTrue(cacho.aliados().isEmpty())
    }

    @Test
    fun cuandoLaPartyALaQuePerteneceTieneOtrosAventurerosSonSusAliados() {
        val party = Party("Party", "")
        val aliado = Aventurero("Aliado")

        party.agregarUnAventurero(cacho)
        party.agregarUnAventurero(aliado)

        assertEquals(1, cacho.aliados().size)
        assertTrue(cacho.aliados().contains(aliado))
    }

    @Test
    fun sabeSiEsAliadoDeOtroAventurero() {
        val party = Party("Party", "")
        val aliado = Aventurero("Aliado")
        val noAliado = Aventurero("No aliado")

        party.agregarUnAventurero(cacho)
        party.agregarUnAventurero(aliado)

        assertTrue(cacho.esAliadoDe(aliado))
        assertFalse(cacho.esAliadoDe(noAliado))
    }

    @Test
    fun noEsAliadoDeSiMismo() {
        val party = Party("Party", "")
        party.agregarUnAventurero(cacho)
        assertFalse(cacho.esAliadoDe(cacho))
    }

    @Test
    fun noEsEnemigoDeSiMismo() {
        val party = Party("Party", "")
        party.agregarUnAventurero(cacho)
        assertFalse(cacho.esEnemigoDe(cacho))
    }

    @Test
    fun esEnemigoDeAventurerosQueNoSeanAliados() {
        val party = Party("Party", "")
        val aliado = Aventurero("Aliado")
        val noAliado = Aventurero("No aliado")

        party.agregarUnAventurero(cacho)
        party.agregarUnAventurero(aliado)

        assertFalse(cacho.esEnemigoDe(aliado))
        assertTrue(cacho.esEnemigoDe(noAliado))
    }

    @Test
    fun unAventureroNoPuedeTenerAtributosMayoresACienPuntos(){
        val exception = assertThrows<RuntimeException> { Aventurero("juan","url",978.0) }
        assertEquals("La fuerza no puede exceder los 100 puntos!", exception.message)
    }

    @Test
    fun unAventureroNoPuedeTenerAtributosMenoresAUnPuntos(){
        val exception = assertThrows<RuntimeException> { Aventurero("pepino",
                "url",1.0,12.0,1.0, 0.0) }
        assertEquals("La constitucion no puede ser menor a 1 punto!", exception.message)
    }

    @Test
    fun `un aventurero puede reestablecer su vida y mana`() {
        val vidaInicial = cacho.vidaActual()
        val manaInicial = cacho.mana()
        cacho.recibirAtaqueFisicoSiDebe(10.0, 5000.0)
        cacho.consumirMana()

        cacho.reestablecerse()

        assertThat(cacho.vidaActual()).isEqualTo(vidaInicial)
        assertThat(cacho.mana()).isEqualTo(manaInicial)
    }

    @Test
    fun `un aventurero deja de defender cuando se reestablece`() {
        val otroAventurero = Aventurero("Pepe")
        cacho.defenderA(otroAventurero)

        cacho.reestablecerse()

        assertFalse(cacho.estaDefendiendo())
    }

    @Test
    fun `un aventurero deja de defender a otro luego de defenderlo por 3 ataques efectivos`() {
        val otroAventurero = Aventurero("Pepe")
        cacho.defenderA(otroAventurero)

        repeat(3){
            otroAventurero.recibirAtaqueFisicoSiDebe(0.5,9999.0)
        }

        assertFalse(cacho.estaDefendiendo())
    }

    @Test
    fun `un aventurero deja de estar defendido cuando se reestablece`() {
        val otroAventurero = Aventurero("Pepe")
        cacho.defenderA(otroAventurero)

        otroAventurero.reestablecerse()

        assertFalse(otroAventurero.estaSiendoDefendiendo())
    }

    @Test
    fun `un aventurero tiene como daño recibido maximo la misma cantidad de vida`(){
        val aventurero = Aventurero("Juan")
        val vidaAntesDeAtaque = aventurero.vidaActual()
        aventurero.recibirAtaqueFisicoSiDebe(1000.0,23232.0)

        assertThat(aventurero.dañoRecibido()).isEqualTo(vidaAntesDeAtaque)
    }

    @Test
    fun `un aventurero sabe calcular su poder total`() {
        val aventurero = Aventurero("Pepe","URL",10.0,10.0,10.0,10.0)

        val poderTotalDeAventureroEsperado = aventurero.dañoFisico() +
                                            aventurero.precisionFisica() +
                                            aventurero.poderMagico()
        assertThat(aventurero.poderTotal()).isEqualTo(poderTotalDeAventureroEsperado)
    }

    @Test
    fun `cuando a un aventurero se le actualiza su atributo fuerza se actualiza su poder total`() {
        val aventurero = Aventurero("Juan","URL",10.0,10.0,10.0,10.0)
        val poderTotalAntes = aventurero.poderTotal()
        val aventureroActualizado = Aventurero("Juan","URL",20.0,10.0,10.0,10.0)

        aventurero.actualizarse(aventureroActualizado)
        val poderTotalActualEsperado = aventurero.dañoFisico() + aventurero.precisionFisica() + aventurero.poderMagico()

        assertThat(poderTotalAntes).isLessThan(poderTotalActualEsperado)
        assertThat(aventurero.poderTotal()).isEqualTo(poderTotalActualEsperado)

    }


    @Test
    fun `cuando a un aventurero se le actualiza su atributo fuerza su vida tambien se actualiza`() {
        val aventurero = Aventurero("Juan","URL",10.0,10.0,10.0,10.0)
        val vidaAntes = aventurero.vidaActual()
        val aventureroActualizado = Aventurero("Juan","URL",20.0,10.0,10.0,10.0)

        aventurero.actualizarse(aventureroActualizado)
        val vidaActualEsperada = ((aventurero.nivel() * 5) + (aventurero.constitucion()* 2) + aventurero.fuerza())

        assertThat(vidaAntes).isLessThan(vidaActualEsperada)
        assertThat(aventurero.vidaActual()).isEqualTo(vidaActualEsperada)
    }


    @Test
    fun `cuando a un aventurero se le actualiza su atributo inteligencia su mana tambien se actualiza`() {
        val aventurero = Aventurero("Juan","URL",10.0,10.0,10.0,10.0)
        val manaAntes = aventurero.mana()
        val aventureroActualizado = Aventurero("Juan","URL",10.0,10.0,20.0,10.0)

        aventurero.actualizarse(aventureroActualizado)
        val manaActualEsperado = aventurero.nivel() + aventurero.inteligencia()

        assertThat(manaAntes).isLessThan(manaActualEsperado)
        assertThat(aventurero.mana()).isEqualTo(manaActualEsperado)
    }

    @Test
    fun `un aventurero inicialmente tiene cero puntos de experiencia`() {
        val aventurero = Aventurero("Marcos","URL",10.0,10.0,10.0,10.0)

        assertThat(aventurero.experiencia()).isEqualTo(0)
        assertFalse(aventurero.tieneExperiencia())
    }

    @Test
    fun `un aventurero aumenta su vida, mana y poder total al ganar una pelea`(){
        val aventurero = Aventurero("Pepe")
        val vidaAntesDeGanarPelea = aventurero.vidaActual()
        val manaAntesDeGanarPelea = aventurero.mana()
        val poderTotalAntesDeGanarPelea = aventurero.poderTotal()

        aventurero.ganarPelea()

        assertThat(aventurero.vidaActual()).isGreaterThan(vidaAntesDeGanarPelea)
        assertThat(aventurero.mana()).isGreaterThan(manaAntesDeGanarPelea)
        assertThat(aventurero.poderTotal()).isGreaterThan(poderTotalAntesDeGanarPelea)
    }

    @Test
    fun `un aventurero pierde un punto de experiencia al obtener una mejora`() {
        val mejora = Mejora("Aventurero", "Guerrero",listOf(),0)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()
        val experienciaAntesDeMejora = aventurero.experiencia()

        aventurero.obtenerMejora(mejora)

        assertThat(aventurero.experiencia()).isEqualTo(experienciaAntesDeMejora - 1)
    }

    @Test
    fun `un aventurero adquiere una nueva clase`() {
        val mejora = Mejora("Aventurero", "Guerrero",listOf(),0)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()

        aventurero.obtenerMejora(mejora)

        assertTrue(aventurero.clases().contains("Guerrero"))
    }

    @Test
    fun `un aventurero sube puntos en atributos ignorando el uppercase??????`() {
        val mejora = Mejora("Aventurero", "Guerrero",listOf(Atributo.DESTREZA),4)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()
        val destrezaAntesDeMejora = aventurero.destreza()

        aventurero.obtenerMejora(mejora)

        assertThat(aventurero.destreza()).isEqualTo(destrezaAntesDeMejora + 4)
    }

    @Test
    fun `un aventurero aumenta su poder total cuando adquiere una mejora con fuerza, destreza o inteligencia`() {
        val mejora = Mejora("Aventurero", "Guerrero",
            listOf(Atributo.DESTREZA, Atributo.INTELIGENCIA, Atributo.FUERZA),4)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()
        val poderTotalAntesDeMejora = aventurero.poderTotal()
        aventurero.obtenerMejora(mejora)

        assertThat(aventurero.poderTotal()).isGreaterThan(poderTotalAntesDeMejora)
    }

    @Test
    fun `un aventurero mantiene el mismo poder total si la mejora solo sube su constitucion`() {
        val mejora = Mejora("Aventurero", "Tanque",
            listOf(Atributo.CONSTITUCION),4)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()
        val poderTotalAntesDeMejora = aventurero.poderTotal()
        aventurero.obtenerMejora(mejora)

        assertThat(aventurero.poderTotal()).isEqualTo(poderTotalAntesDeMejora)
    }

    @Test
    fun `un aventurero aumenta su vida cuando adquiere una nueva mejora que aumenta su constitucion o fuerza`() {
        val mejora = Mejora("Aventurero", "Guerrero",
            listOf(Atributo.CONSTITUCION, Atributo.FUERZA),4)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()
        val vidaAntesDeMejora = aventurero.vidaActual()

        aventurero.obtenerMejora(mejora)

        assertThat(aventurero.vidaActual()).isGreaterThan(vidaAntesDeMejora)
    }

    @Test
    fun `un aventurero no aumenta su vida si adquiere una mejora que no incluye fuerza o constitucion`() {
        val mejora = Mejora("Aventurero", "Guerrero",
            listOf(Atributo.INTELIGENCIA),4)
        val aventurero = Aventurero("Jose")
        aventurero.ganarPelea()
        val vidaAntesDeMejora = aventurero.vidaActual()

        aventurero.obtenerMejora(mejora)

        assertThat(aventurero.vidaActual()).isEqualTo(vidaAntesDeMejora)
    }

    @Test
    fun `un aventurero es otro si alguno no tiene id y se llaman igual`(){
        val pepito = Aventurero("Pepito")
        val pepitoClon = Aventurero("Pepito")
        assertThat(pepito.esElAventurero(pepitoClon)).isTrue()
    }


    @Test
    fun `un aventurero NO es otro si alguno no tiene id y se llaman distinto`(){
        val pepito = Aventurero("Pepito")
        val pepitoClon = Aventurero("Pepito 2")
        assertThat(pepito.esElAventurero(pepitoClon)).isFalse()
    }

    @Test
    fun `un aventurero NO es otro si ambos tienen el mismo id y se llaman distinto`(){
        val pepito = Aventurero("Pepito")
        pepito.darleElId(43)
        val pepitoClon = Aventurero("Pepito Clon")
        pepitoClon.darleElId(43)

        assertThat(pepito.esElAventurero(pepitoClon)).isFalse()
    }

    @Test
    fun `un aventurero NO es otro si tienen ids distintos`(){
        val pepito = Aventurero("Pepito")
        pepito.darleElId(43)
        val pepitoClon = Aventurero("Pepito")
        pepitoClon.darleElId(45)

        assertThat(pepito.esElAventurero(pepitoClon)).isFalse()
    }

    @Test
    fun `un aventurero es otro si ambos tienen el mismo id y se llaman igual`(){
        val pepito = Aventurero("Pepito")
        pepito.darleElId(43)
        val pepitoClon = Aventurero("Pepito")
        pepitoClon.darleElId(43)

        assertThat(pepito.esElAventurero(pepitoClon)).isTrue()
    }
}