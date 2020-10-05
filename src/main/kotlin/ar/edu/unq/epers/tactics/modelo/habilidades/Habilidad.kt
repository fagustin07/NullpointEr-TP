package ar.edu.unq.epers.tactics.modelo.habilidades

import ar.edu.unq.epers.tactics.modelo.Aventurero


abstract class Habilidad(val aventureroReceptor: Aventurero) {



    abstract fun resolverse()

}