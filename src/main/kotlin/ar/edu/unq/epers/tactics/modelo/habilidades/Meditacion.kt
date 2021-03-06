package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import java.lang.RuntimeException
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class Meditacion(
    aventureroEmisor: Aventurero?,
    aventureroReceptor: Aventurero
) : Habilidad(
    aventureroEmisor,
    aventureroReceptor
) {

    companion object {

        val MENSAJE_AVENTUREROS_DISTINTOS = "La habilidad Meditar debe tener mismo emisor que receptor"

        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Meditacion {
            if(!aventureroEmisor.equals(aventureroReceptor)) { throw RuntimeException(this.MENSAJE_AVENTUREROS_DISTINTOS) }

            return Meditacion(aventureroReceptor, aventureroEmisor)
        }
    }

    override val esMeditacion = true

    override fun resolversePara(receptor: Aventurero) {
        receptor.meditar()
    }

}
