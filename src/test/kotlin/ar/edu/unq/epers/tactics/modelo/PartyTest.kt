package ar.edu.unq.epers.tactics.modelo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PartyTest {
    private lateinit var party: Party
    private lateinit var pepe: Aventurero

    @BeforeEach
    fun setUp() {
        party = Party("Los Bulls", "URL")
        pepe = Aventurero("Pepe")
    }

    @Test
    fun unaPartyComienzaSinAventureros() = assertEquals(0, party.numeroDeAventureros())

    @Test
    fun sePuedenAgregarAventurerosAUnaParty() {
        party.agregarUnAventurero(pepe)

        assertEquals(1, party.numeroDeAventureros())
        assertEquals(1, party.aventureros().size)
    }

    @Test
    fun noSePuedenAgregarAventurerosAUnaPartyCompleta() {
        val cincoAventureros = (1..5).map { Aventurero("Aventurero${it}") }

        cincoAventureros.forEach { aventurero -> party.agregarUnAventurero(aventurero) }

        val exception = assertThrows<RuntimeException> { party.agregarUnAventurero(pepe) }
        assertEquals("La party ${party.nombre()} está completa.", exception.message)

        assertEquals(5, party.numeroDeAventureros())
        assertTrue(party.aventureros().containsAll(cincoAventureros))
    }

    @Test
    fun unaPartyNoAgregaAventurerosQueNoPertenezcanAElla() {
        val otraParty = Party("Champions of Red Hawk.", "URL")
        val aventureroDeOtraParty = Aventurero("OTK Garfield")
        otraParty.agregarUnAventurero(aventureroDeOtraParty)

        val exception = assertThrows<RuntimeException> { party.agregarUnAventurero(aventureroDeOtraParty) }
        assertEquals(exception.message, "${aventureroDeOtraParty.nombre()} no pertenece a ${party.nombre()}.")
        assertEquals(0, party.numeroDeAventureros())
    }


    @Test
    fun noPuedeAgregarseAUnaPartyUnAventureroQueYaFueAgregado() {
        val party = Party("NombreDeParty", "URL")
        val aventurero = Aventurero("NombreDeAventurero")

        party.agregarUnAventurero(aventurero)

        val exception = assertThrows<RuntimeException> { party.agregarUnAventurero(aventurero) }
        assertEquals(exception.message, "${aventurero.nombre()} ya forma parte de la party ${party.nombre()}.")

        assertEquals(1, party.numeroDeAventureros())
        assertTrue(party.aventureros().contains(aventurero))
    }

    @Test
    fun unaPartyDebeTenerUnNombre() {
        val exception = assertThrows<RuntimeException> { Party("", "URL") }
        assertEquals(exception.message, "Una party debe tener un nombre")
    }

    @Test
    fun seRemueveUnaAventurerosDeUnaParty() {
        val party = Party("Nombre de party", "URL")
        val aventurero = Aventurero("Nombre de aventurero")
        party.agregarUnAventurero(aventurero)

        party.removerA(aventurero)

        assertEquals(0, party.numeroDeAventureros())
        assertEquals(null, aventurero.party)
    }

    @Test
    fun noSePuedeRemoverUnAventureroDeUnaPartyALaQueNoPertenece() {
        val party = Party("Nombre de party", "URL")
        val aventurero = Aventurero("Nombre de aventurero")

        val exception = assertThrows<java.lang.RuntimeException> { party.removerA(aventurero) }
        assertEquals("${aventurero.nombre()} no pertenece a ${party.nombre()}.", exception.message)
    }

    @Test
    fun `al pedirle a una party sin aventureros las clase en las que estos son proficientes no se obtiene ninguna`() {
        val party = Party("Nombre de party", "/imagen.jpg")
        assertTrue(party.clasesDeSusPersonajes().isEmpty())
    }

    @Test
    fun `al pedirle a una party con un aventurero las clase en las que estos son proficientes se obtienen aquellas en la que el mismo es proficiente`() {
        val party = Party("Nombre de party", "/imagen.jpg")
        val aventurero = Aventurero("Nombre aventurero")
        party.agregarUnAventurero(aventurero)

        val clasesObtenidas = party.clasesDeSusPersonajes()

        assertEquals(1, clasesObtenidas.size)
        assertTrue(clasesObtenidas.containsAll(aventurero.clases()))
    }

    @Test // TODO: no me convence como quedo expresada la parte de la asercion
    fun `al pedirle a una party con varios aventureros las clase en las que estos son proficientes se obtienen aquellas en la que son proficientes incluyendo repetidos`() {
        val party = Party("Nombre de party", "/imagen.jpg")
        val unAventurero = Aventurero("Pepe")
        val otroAventurero = Aventurero("Juan")

        party.agregarUnAventurero(unAventurero)
        party.agregarUnAventurero(otroAventurero)

        val clasesObtenidas = party.clasesDeSusPersonajes()

        assertEquals(2, clasesObtenidas.size)
        assertTrue(clasesObtenidas.containsAll(unAventurero.clases()))
        assertTrue(clasesObtenidas.containsAll(otroAventurero.clases()))
    }

    @Test
    fun `cuando se asignan atributos de formacion a una party, sus aventureros incrementan sus atributos`() {
        party.agregarUnAventurero(pepe)

        party.asignarAtributosDeFormacion(listOf(
            AtributoDeFormacion("fuerza", 10),
            AtributoDeFormacion("destreza", 20),
            AtributoDeFormacion("inteligencia", 30),
            AtributoDeFormacion("constitucion", 40)
        ))

        assertEquals(11.0, pepe.fuerza())
        assertEquals(21.0, pepe.destreza())
        assertEquals(31.0, pepe.inteligencia())
        assertEquals(41.0, pepe.constitucion())
    }

    @Test
    fun `cuando se le asignan atributos de formacion a una party que ya tenia, se reemplazan los anteriores`() {
        party.agregarUnAventurero(pepe)
        party.asignarAtributosDeFormacion(listOf(AtributoDeFormacion("fuerza", 10)))

        party.asignarAtributosDeFormacion(listOf(AtributoDeFormacion("constitucion", 50)))

        assertEquals(1.0, pepe.fuerza())
        assertEquals(1.0, pepe.destreza())
        assertEquals(1.0, pepe.inteligencia())
        assertEquals(51.0, pepe.constitucion())
    }

    @Test
    fun `cuando se le asignan atributos de formacion a una party, se incrementan los atributos que la formacion brinda a sus aventureros`() {
        party.agregarUnAventurero(pepe)
        party.asignarAtributosDeFormacion(listOf(AtributoDeFormacion("fuerza", 10)))

        assertEquals(pepe.nivel() * 5 + pepe.constitucion() * 2 + pepe.fuerza(), pepe.vidaActual())
        assertEquals(pepe.nivel() + pepe.constitucion(), pepe.armadura())
        assertEquals(pepe.nivel() + pepe.destreza(), pepe.velocidad())
        assertEquals(pepe.nivel() + pepe.fuerza() + pepe.destreza() / 2, pepe.dañoFisico())
        assertEquals(pepe.nivel() + pepe.mana(), pepe.poderMagico())
        assertEquals(pepe.nivel() + pepe.fuerza() + pepe.destreza(), pepe.precisionFisica())
    }

    @Test
    fun `una party puede remover a un aventurero`() {
        party.agregarUnAventurero(pepe)
        pepe.darleElId(7)
        val cantidadAventurerosAntesDeRemoverAUno = party.aventureros().size
        val pepeClon = Aventurero("Pepe")
        pepeClon.party = party
        pepeClon.darleElId(7)

        party.removerA(pepeClon)

        assertThat(party.aventureros().size).isEqualTo(cantidadAventurerosAntesDeRemoverAUno-1)
        assertThat(!party.aventureros().contains(pepe))
    }

    @Test
    fun `cuando una party remover un aventurero que pertenece a su party, este deja de pertenecer a la misma`() {
        party.agregarUnAventurero(pepe)
        pepe.darleElId(7)
        val pepeClon = Aventurero("Pepe")
        pepeClon.party = party
        pepeClon.darleElId(7)

        party.removerA(pepeClon)

        assertThat(pepe.party).isNull()
    }

    @Test
    fun `cuando se quiere remover un aventureo con una referencia invalida al mismo, la party no lo remueve`() {
        party.agregarUnAventurero(pepe)
        pepe.darleElId(7)
        val pepeClon = Aventurero("Pepe")
        pepeClon.party = party
        pepeClon.darleElId(9)

        party.removerA(pepeClon)

        assertThat(party.aventureros().contains(pepe)).isTrue()
    }

}