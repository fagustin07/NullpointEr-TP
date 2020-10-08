package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class Defensa(val aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Habilidad(aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Defensa {
            aventureroEmisor.validacionParaDefenderA(aventureroReceptor)
            return Defensa(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolversePara(receptor: Aventurero) = aventureroEmisor.defenderA(receptor)

}
