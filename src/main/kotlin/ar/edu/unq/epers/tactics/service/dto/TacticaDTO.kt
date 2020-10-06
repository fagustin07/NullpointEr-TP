package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Tactica
import ar.edu.unq.epers.tactics.modelo.enums.*

data class TacticaDTO(
        var id: Long?,
        var prioridad: Int,
        var receptor: TipoDeReceptor,
        var tipoDeEstadistica: TipoDeEstadistica,
        var criterio: Criterio,
        var valor: Int,
        var accion: Accion
) {

    companion object {

        fun desdeModelo(tactica: Tactica): TacticaDTO {
            return TacticaDTO(
                tactica.id,
                tactica.prioridad,
                tactica.tipoDeReceptor,
                tactica.tipoDeEstadistica,
                tactica.criterio,
                tactica.valor,
                tactica.accion
            )
        }
    }

    fun aModelo(): Tactica {
        val tactica = Tactica(
            this.prioridad,
            this.receptor,
            this.tipoDeEstadistica,
            this.criterio,
            this.valor,
            this.accion
        )
        tactica.id = this.id

        return tactica
    }

    fun actualizarModelo(tactica: Tactica) = tactica.actualizarse(this.aModelo())
}