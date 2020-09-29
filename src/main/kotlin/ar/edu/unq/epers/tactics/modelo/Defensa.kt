package ar.edu.unq.epers.tactics.modelo

class Defensa(val aventureroEmisor: Aventurero, val aventureroReceptor: Aventurero): Habilidad() {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Defensa {
            return Defensa(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolverse() = aventureroReceptor.esDefendidoPor(aventureroEmisor)

}
