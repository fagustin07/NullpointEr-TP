package ar.edu.unq.epers.tactics.modelo.dado

class DadoDe20: Dado {

    override fun tirada() = ((Math.random() * 19) + 1).toInt()

}