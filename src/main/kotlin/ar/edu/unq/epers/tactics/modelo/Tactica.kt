package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.service.dto.Accion
import ar.edu.unq.epers.tactics.service.dto.Criterio
import ar.edu.unq.epers.tactics.service.dto.TipoDeEstadistica
import ar.edu.unq.epers.tactics.service.dto.TipoDeReceptor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Tactica(
    var prioridad: Int,
    var tipoDeReceptor: TipoDeReceptor,
    var tipoDeEstadistica: TipoDeEstadistica,
    var criterio: Criterio,
    var valor: Int,
    var accion: Accion) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun puedeAplicarseA(emisor: Aventurero, receptor: Aventurero) =
        tipoDeReceptor.test(emisor, receptor)

    fun aplicarseSobre(emisor: Aventurero, receptor: Aventurero): Habilidad {
        return accion.generarHabilidad(emisor, receptor)
    }

}
