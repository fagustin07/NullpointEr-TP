package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Tactica(var prioridad: Int,
              var receptor: TipoDeReceptor,
              var tipoDeEstadistica: TipoDeEstadistica,
              var criterio: Criterio,
              var valor: Int,
              var accion: Accion) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun evaluarse(emisor: Aventurero, aventureros: List<Aventurero>): Boolean {
        return aventureros.any{ aventurero -> receptor.test(emisor, aventurero) }
    }

}
