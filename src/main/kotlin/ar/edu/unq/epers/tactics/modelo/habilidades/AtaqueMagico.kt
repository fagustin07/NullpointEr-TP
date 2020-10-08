package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.dado.Dado

class AtaqueMagico(val poderMagicoEmisor: Int, val nivelEmisor: Int, aventureroReceptor: Aventurero, val dado: Dado) : Habilidad(aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dado: Dado): AtaqueMagico {
            val ataqueMagico = AtaqueMagico(aventureroEmisor.poderMagico(), aventureroEmisor.nivel(), aventureroReceptor, dado)
            aventureroEmisor.consumirMana()
            return ataqueMagico
        }
    }

    override fun resolverse() {
        val tirada = dado.tirada() + nivelEmisor
        aventureroReceptor.recibirAtaqueMagicoSiDebe(tirada, poderMagicoEmisor)
    }

}
