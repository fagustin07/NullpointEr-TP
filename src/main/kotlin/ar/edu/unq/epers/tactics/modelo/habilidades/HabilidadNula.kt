package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import java.lang.RuntimeException

class HabilidadNula(aventureroReceptor: Aventurero) : Habilidad(aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): HabilidadNula {
            if (aventureroEmisor!=aventureroReceptor) throw RuntimeException("La habilidad nula es solo sobre uno mismo")
            return HabilidadNula(aventureroReceptor)
        }
    }

    override fun resolverse() {}
}