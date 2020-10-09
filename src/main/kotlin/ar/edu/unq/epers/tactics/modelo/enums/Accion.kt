package ar.edu.unq.epers.tactics.modelo.enums

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.dado.DadoDe20
import ar.edu.unq.epers.tactics.modelo.habilidades.*

enum class Accion {
    ATAQUE_FISICO { override fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) = Ataque.para(emisor,receptor, DadoDe20()) },
    DEFENDER{ override fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) = Defensa.para(emisor,receptor) },
    CURAR{ override fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) = Curacion.para(emisor,receptor)},
    ATAQUE_MAGICO{ override fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) = AtaqueMagico.para(emisor,receptor, DadoDe20()) },
    MEDITAR{ override fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) = Meditacion.para(emisor,receptor) },
    NADA{ override fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) = HabilidadNula.para(emisor,receptor) };

    abstract  fun generarHabilidad(emisor: Aventurero, receptor: Aventurero) : Habilidad
}