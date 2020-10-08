package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.dado.Dado

class Ataque(val dañoFisico: Int, val precisionFisica: Int, aventureroReceptor: Aventurero, val dado: Dado) : Habilidad(aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dado: Dado): Ataque {
            return Ataque(aventureroEmisor.dañoFisico(), aventureroEmisor.precisionFisica(), aventureroReceptor, dado)
        }
    }

    override fun resolverse() {
        aventureroReceptor.recibirAtaqueFisicoSiDebe(dañoFisico, dado.tirada() + precisionFisica)
    }

}
