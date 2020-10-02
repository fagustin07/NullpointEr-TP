package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class AtaqueMagico(val poderMagicoEmisor: Int, val nivelEmisor: Int, val aventureroReceptor: Aventurero, val dadoDe20: DadoDe20) : Habilidad() {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dadoDe20: DadoDe20): AtaqueMagico {
            val ataqueMagico = AtaqueMagico(aventureroEmisor.poderMagico(), aventureroEmisor.nivel(), aventureroReceptor, dadoDe20)
            aventureroEmisor.consumirMana()
            return ataqueMagico
        }
    }

    override fun resolverse() {
        val tirada = dadoDe20.tirada() + nivelEmisor
        aventureroReceptor.recibirAtaqueMagicoSiDebe(tirada, poderMagicoEmisor)
    }

}
