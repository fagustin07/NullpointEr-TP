package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import java.lang.RuntimeException

class Meditacion(val aventureroEmisor: Aventurero, aventureroReceptor: Aventurero) : Habilidad(aventureroReceptor) {

    companion object {

        val MENSAJE_AVENTUREROS_DISTINTOS = "La habilidad Meditar debe tener mismo emisor que receptor"

        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Meditacion {
            if(!aventureroEmisor.equals(aventureroReceptor)) { throw RuntimeException(this.MENSAJE_AVENTUREROS_DISTINTOS) }

            return Meditacion(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolverse() {
        aventureroReceptor.meditar()
    }

}
