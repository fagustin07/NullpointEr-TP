package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class Curacion(val aventureroEmisor: Aventurero, val aventureroReceptor: Aventurero) : Habilidad() {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Curacion {
            return Curacion(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolverse() {
        aventureroReceptor.curar(aventureroEmisor.poderMagico())
        aventureroEmisor.restarMana(5)
    }

}
