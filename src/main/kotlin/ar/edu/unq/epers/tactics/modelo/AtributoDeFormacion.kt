package ar.edu.unq.epers.tactics.modelo

class AtributoDeFormacion {
    var nombreAtributo : String? = null
    var puntosDeGanancia : Int? = null

    protected constructor() {}

    constructor(nombreAtributo: String, puntosDeGanancia: Int){
        this.nombreAtributo   = nombreAtributo
        this.puntosDeGanancia = puntosDeGanancia
    }
}
