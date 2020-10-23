package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import java.lang.RuntimeException
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class HabilidadNula(
    aventureroEmisor: Aventurero?,
    aventureroReceptor: Aventurero
) : Habilidad(aventureroEmisor, aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): HabilidadNula {
            if (aventureroEmisor!=aventureroReceptor) throw RuntimeException("La habilidad nula es solo sobre uno mismo")
            return HabilidadNula(aventureroEmisor, aventureroReceptor)
        }
    }

    override fun resolversePara(receptor: Aventurero) { }
}