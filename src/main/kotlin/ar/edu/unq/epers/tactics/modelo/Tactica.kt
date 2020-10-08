package ar.edu.unq.epers.tactics.modelo

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.modelo.enums.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Tactica(
    internal var prioridad: Int,
    internal var tipoDeReceptor: TipoDeReceptor,
    internal var tipoDeEstadistica: TipoDeEstadistica,
    internal var criterio: Criterio,
    internal var valor: Int,
    internal var accion: Accion) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    internal var id: Long? = null

    fun puedeAplicarseA(emisor: Aventurero, receptor: Aventurero) =
        this.tipoDeReceptor.test(emisor, receptor) &&
                criterio.evaluarseCon(tipoDeEstadistica.valorPara(receptor),
                        valor) &&
                receptor.estaVivo()

    fun aplicarseSobre(emisor: Aventurero, receptor: Aventurero): Habilidad {
        return accion.generarHabilidad(emisor, receptor)
    }

    fun actualizarse(otraTactica: Tactica) {
        this.prioridad = otraTactica.prioridad
        this.tipoDeReceptor = otraTactica.tipoDeReceptor
        this.tipoDeEstadistica = otraTactica.tipoDeEstadistica
        this.criterio = otraTactica.criterio
        this.valor = otraTactica.valor
        this.accion = otraTactica.accion
    }

}
