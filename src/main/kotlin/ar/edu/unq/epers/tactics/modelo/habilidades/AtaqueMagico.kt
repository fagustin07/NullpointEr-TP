package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class AtaqueMagico(val aventureroEmisor: Aventurero, val aventureroReceptor: Aventurero, val dadoDe20: DadoDe20) : Habilidad() {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dadoDe20: DadoDe20): AtaqueMagico {
            return AtaqueMagico(aventureroEmisor, aventureroReceptor, dadoDe20)
        }
    }

    override fun resolverse() {
        val tirada = dadoDe20.tirada() + aventureroEmisor.nivel()
        aventureroReceptor.recibirAtaqueMagicoSiDebe(tirada, aventureroEmisor.poderMagico())
    }

}
