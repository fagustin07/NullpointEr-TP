package ar.edu.unq.epers.tactics.modelo

import org.bson.codecs.pojo.annotations.BsonProperty

class Formacion {

    @BsonProperty("id")
    private var id: String? = null

    var nombre : String? = null
    var requerimientos : List<Clase> = mutableListOf()
    var stats : List<AtributoDeFormacion> = mutableListOf()

    protected constructor() {}

    constructor(nombre: String, requerimientos: List<Clase>, stats : List<AtributoDeFormacion>){
        this.nombre = nombre
        this.requerimientos = requerimientos
        this.stats = stats
    }
}
