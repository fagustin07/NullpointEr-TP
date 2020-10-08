package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero

class Curacion(val poderMagicoEmisor: Int, aventureroReceptor: Aventurero) : Habilidad(aventureroReceptor) {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero): Curacion {
            val curacionCreada = Curacion(aventureroEmisor.poderMagico(), aventureroReceptor)
            aventureroEmisor.consumirMana()
            return curacionCreada
        }
    }

    override fun resolversePara(receptor: Aventurero) = receptor.curar(poderMagicoEmisor)

}
