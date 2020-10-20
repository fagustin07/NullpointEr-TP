package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.dado.Dado
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn


@Entity
@PrimaryKeyJoinColumn(name="id")
class AtaqueMagico(
    val poderMagicoEmisor: Double,
    val nivelEmisor: Int,
    aventureroEmisor: Aventurero?,
    aventureroReceptor: Aventurero,
    @Transient val dado: Dado
) : Habilidad(
    aventureroEmisor,
    aventureroReceptor
) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dado: Dado): AtaqueMagico {
            val ataqueMagico = AtaqueMagico(
                aventureroEmisor.poderMagico(),
                aventureroEmisor.nivel(),
                aventureroEmisor,
                aventureroReceptor,
                dado
            )
            aventureroEmisor.consumirMana()
            return ataqueMagico
        }
    }

    override val esAtaqueMagico = true

    override fun resolversePara(receptor: Aventurero) {
        val tirada = dado.tirada() + nivelEmisor
        receptor.recibirAtaqueMagicoSiDebe(tirada, poderMagicoEmisor)
    }

}
