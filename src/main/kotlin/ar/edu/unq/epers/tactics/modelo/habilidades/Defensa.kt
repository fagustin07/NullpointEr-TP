package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class Defensa(val aventureroEmisor: Aventurero, val aventureroReceptor: Aventurero): Habilidad() {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Defensa {
            aventureroEmisor.validacionParaDefenderA(aventureroReceptor)
            return Defensa(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolverse() = aventureroEmisor.defenderA(aventureroReceptor)

}
