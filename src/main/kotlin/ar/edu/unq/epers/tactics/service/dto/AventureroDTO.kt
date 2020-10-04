package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.habilidades.*


data class AventureroDTO(var id: Long?, var nivel: Int, var nombre: String, var imagenURL: String, var tacticas: List<TacticaDTO>, var atributos: AtributosDTO) {

    companion object {

        fun desdeModelo(aventurero: Aventurero): AventureroDTO {
            return AventureroDTO(aventurero.id(),
                    aventurero.nivel(),
                    aventurero.nombre(),
                    "imagen",
                    listOf(),
                    AtributosDTO(
                            aventurero.id(),
                            aventurero.fuerza(),
                            aventurero.destreza(),
                            aventurero.constitucion(),
                            aventurero.inteligencia()
                    )
            )
        }
    }

    fun aModelo(): Aventurero {
        val aventurero = Aventurero(
                this.nombre,
                this.atributos.fuerza,
                this.atributos.destreza,
                this.atributos.inteligencia,
                this.atributos.constitucion
        )

        aventurero.darleElId(this.id)

        return aventurero
    }

    fun actualizarModelo(aventurero: Aventurero) = aventurero.actualizarse(this)
}

data class AtributosDTO(var id: Long?, var fuerza: Int, var destreza: Int, var constitucion: Int, var inteligencia: Int)
data class TacticaDTO(var id: Long?, var prioridad: Int, var receptor: TipoDeReceptor, var tipoDeEstadistica: TipoDeEstadistica, var criterio: Criterio, var valor: Int, var accion: Accion)

enum class TipoDeReceptor {
    ALIADO { override fun test(emisor: Aventurero, receptor: Aventurero) = emisor.esAliadoDe(receptor) },
    ENEMIGO { override fun test(emisor: Aventurero, receptor: Aventurero) = emisor.esEnemigoDe(receptor) },
    UNO_MISMO { override fun test(emisor: Aventurero, receptor: Aventurero) = emisor == receptor };

    abstract fun test(emisor: Aventurero, receptor: Aventurero): Boolean
}

enum class TipoDeEstadistica {
    VIDA  { override fun valorPara(aventurero: Aventurero) = aventurero.vida() },
    ARMADURA { override fun valorPara(aventurero: Aventurero) = aventurero.armadura() },
    MANA { override fun valorPara(aventurero: Aventurero) = aventurero.mana() },
    VELOCIDAD { override fun valorPara(aventurero: Aventurero) = aventurero.velocidad() },
    DAÑO_FISICO { override fun valorPara(aventurero: Aventurero) = aventurero.dañoFisico() },
    DAÑO_MAGICO { override fun valorPara(aventurero: Aventurero) = aventurero.poderMagico() },
    PRECISION_FISICA { override fun valorPara(aventurero: Aventurero) = aventurero.precisionFisica() };

    abstract fun valorPara(aventurero: Aventurero): Int
}

enum class Criterio {
    IGUAL { override fun evaluarseCon(valorAComparar: Int, valorDeComparacion: Int) = valorAComparar == valorDeComparacion },
    MAYOR_QUE { override fun evaluarseCon(valorDeAventurero: Int, valorDeComparacion: Int) = valorDeAventurero > valorDeComparacion },
    MENOR_QUE { override fun evaluarseCon(valorDeAventurero: Int, valorDeComparacion: Int) = valorDeAventurero < valorDeComparacion };

    abstract fun evaluarseCon(valorDeAventurero: Int, valorDeComparacion: Int): Boolean
}

enum class Accion {
    ATAQUE_FISICO { override fun generar(emisor:Aventurero, receptor: Aventurero) = Ataque.para(emisor,receptor, DadoDe20()) },
    DEFENDER{ override fun generar(emisor:Aventurero, receptor: Aventurero) = Defensa.para(emisor,receptor) },
    CURAR{ override fun generar(emisor:Aventurero, receptor: Aventurero) = Curacion.para(emisor,receptor)},
    ATAQUE_MAGICO{ override fun generar(emisor:Aventurero, receptor: Aventurero) = AtaqueMagico.para(emisor,receptor, DadoDe20()) },
    MEDITAR{ override fun generar(emisor:Aventurero, receptor: Aventurero) = Meditacion.para(emisor,receptor) };

    abstract  fun generar(emisor:Aventurero, receptor: Aventurero) : Habilidad
}