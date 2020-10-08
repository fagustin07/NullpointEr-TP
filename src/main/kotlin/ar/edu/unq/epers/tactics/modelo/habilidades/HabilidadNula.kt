package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class HabilidadNula(val aventureroEmisor: Aventurero,  aventureroReceptor: Aventurero) : Habilidad(aventureroReceptor){

    override fun resolverse() {
    }
}