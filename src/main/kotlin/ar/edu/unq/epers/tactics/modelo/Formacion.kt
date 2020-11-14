package ar.edu.unq.epers.tactics.modelo

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import javax.persistence.ElementCollection
import javax.persistence.MapKey
import javax.persistence.OneToMany

class Formacion {

    @BsonProperty("_id")
    var id : ObjectId? = null
    var nombre : String? = null

    @ElementCollection
    @OneToMany
    @MapKey(name = "requerimiento")
    var requerimientos : Map<String, Int> = mutableMapOf()

    var stats : List<AtributoDeFormacion> = mutableListOf()
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
