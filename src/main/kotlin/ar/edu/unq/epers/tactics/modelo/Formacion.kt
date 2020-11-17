package ar.edu.unq.epers.tactics.modelo

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

class Formacion {

    @BsonProperty("_id")
    var id: ObjectId? = null
    var nombre: String? = null
    var requerimientos: Map<String, Int> = mutableMapOf()
    var stats: List<AtributoDeFormacion> = mutableListOf()

    protected constructor() {}

    constructor(nombre: String, requerimientos: Map<String, Int>, stats: List<AtributoDeFormacion>) {
        this.nombre = nombre
        this.requerimientos = requerimientos
        this.stats = stats
    }

}
