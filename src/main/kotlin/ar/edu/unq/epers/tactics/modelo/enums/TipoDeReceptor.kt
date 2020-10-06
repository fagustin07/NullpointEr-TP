package ar.edu.unq.epers.tactics.modelo.enums

import ar.edu.unq.epers.tactics.modelo.Aventurero

enum class TipoDeReceptor {
    ALIADO { override fun test(emisor: Aventurero, receptor: Aventurero) = emisor.esAliadoDe(receptor) },
    ENEMIGO { override fun test(emisor: Aventurero, receptor: Aventurero) = emisor.esEnemigoDe(receptor) },
    UNO_MISMO { override fun test(emisor: Aventurero, receptor: Aventurero) = emisor == receptor };

    abstract fun test(emisor: Aventurero, receptor: Aventurero): Boolean
}