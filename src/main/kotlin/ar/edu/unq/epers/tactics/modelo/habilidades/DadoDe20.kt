package ar.edu.unq.epers.tactics.modelo.habilidades

class DadoDe20(val tiradaFalsa: Int?) {
    constructor() : this(null)

    //TODO esto podrian ser dos clases, un D20 real y uno falso, y no tener que checkear po null
    fun tirada(): Int {
        if (this.tiradaFalsa != null) {
            return tiradaFalsa
        } else {
            return ((Math.random() * 19) + 1).toInt()
        }
    }

}
