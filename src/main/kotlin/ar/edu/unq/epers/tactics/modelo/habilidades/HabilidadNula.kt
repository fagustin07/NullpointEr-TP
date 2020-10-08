package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class HabilidadNula(val aventureroEmisor: Aventurero,  aventureroReceptor: Aventurero) : Habilidad(aventureroReceptor){

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): HabilidadNula {
            return HabilidadNula(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolverse() {
        print("No pasa nada")
    }
}