package ar.edu.unq.epers.tactics.modelo.enums

import ar.edu.unq.epers.tactics.modelo.Aventurero

enum class TipoDeEstadistica {
    VIDA  { override fun valorPara(aventurero: Aventurero) = aventurero.vidaActual() },
    ARMADURA { override fun valorPara(aventurero: Aventurero) = aventurero.armadura() },
    MANA { override fun valorPara(aventurero: Aventurero) = aventurero.mana() },
    VELOCIDAD { override fun valorPara(aventurero: Aventurero) = aventurero.velocidad() },
    DAÑO_FISICO { override fun valorPara(aventurero: Aventurero) = aventurero.dañoFisico() },
    DAÑO_MAGICO { override fun valorPara(aventurero: Aventurero) = aventurero.poderMagico() },
    PRECISION_FISICA { override fun valorPara(aventurero: Aventurero) = aventurero.precisionFisica() };

    abstract fun valorPara(aventurero: Aventurero): Int
}