package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Aventurero

// Si no esta todo en la misma linea el coverage tira falso positivo
data class AventureroDTO(var id: Long?, var nivel: Int, var nombre: String, var imagenURL: String, var dañoRecibido: Double, var tacticas: List<TacticaDTO>, var atributos: AtributosDTO) {

    companion object {

        fun desdeModelo(aventurero: Aventurero): AventureroDTO {

            return AventureroDTO(
                aventurero.id(),
                aventurero.nivel(),
                aventurero.nombre(),
                aventurero.imagenURL(),
                aventurero.dañoRecibido(),
                aventurero.tacticas().map { TacticaDTO.desdeModelo(it) },
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
                this.imagenURL,
                this.atributos.fuerza,
                this.atributos.destreza,
                this.atributos.inteligencia,
                this.atributos.constitucion
        )
        this.tacticas.forEach { aventurero.agregarTactica(it.aModelo()) }
        aventurero.darleElId(this.id)
        aventurero.actualizarDañoRecibido(this.dañoRecibido)
        return aventurero
    }

    fun actualizarModelo(aventurero: Aventurero) = aventurero.actualizarse(aModelo())
}

data class AtributosDTO(var id: Long?, var fuerza: Double, var destreza: Double, var constitucion: Double, var inteligencia: Double)