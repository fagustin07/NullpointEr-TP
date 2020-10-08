package ar.edu.unq.epers.tactics.modelo.dado

class DadoSimulado(private val tiradaFalsa: Int) : Dado {

    override fun tirada() = tiradaFalsa

}
