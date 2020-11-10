package ar.edu.unq.epers.tactics.modelo

//import org.bson.codecs.pojo.annotations.BsonProperty

class Formacion {

//    @BsonProperty("id")           el id siempre es null aca y en el ejemplo, no se trae el id de la bdd
//    var id : String? = null
    var nombre : String? = null
    var requerimientos : List<String> = mutableListOf()
    var stats : List<AtributoDeFormacion> = mutableListOf()

    protected constructor() {}

    constructor(nombre: String, requerimientos: List<Clase>, stats : List<AtributoDeFormacion>){
        this.nombre = nombre
        this.requerimientos = requerimientos.map { it.nombre() }
        this.stats = stats
    }

}
