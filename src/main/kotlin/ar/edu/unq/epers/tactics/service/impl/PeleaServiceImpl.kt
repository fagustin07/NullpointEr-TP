package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import java.util.*

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO): PeleaService {

    override fun iniciarPelea(partyId: Long, nombrePartyEnemiga:String): Pelea {
        return runTrx {
            val party = partyDAO.ejecutarCon(partyId) { it.entrarEnPelea() }
            peleaDAO.crear(Pelea(party,nombrePartyEnemiga))
        }
    }

    override fun estaEnPelea(partyId: Long) = runTrx { partyDAO.recuperar(partyId).estaEnPelea() }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>) =
        runTrx {
            val habilidadGenerada = (aventureroDAO.resultadoDeEjecutarCon(aventureroId) { it.resolverTurno(enemigos) }) as Habilidad
            peleaDAO.ejecutarCon(peleaId) { it.registrarEmisionDe(habilidadGenerada) }
            habilidadGenerada
        }

    override fun recibirHabilidad(peleaId: Long, aventureroId: Long, habilidad: Habilidad) =
        runTrx {
            //TODO: agregar el peleaID.
            val aventurero = aventureroDAO.recuperar(aventureroId)

            habilidad.resolversePara(aventurero)

            val pelea = peleaDAO.recuperar(peleaId)
            pelea.registrarRecepcionDe(habilidad)
            peleaDAO.actualizar(pelea)

            aventureroDAO.actualizar(aventurero)
            val aventureroActualizado = aventureroDAO.ejecutarCon(aventureroId) { habilidad.resolversePara(it) }
            peleaDAO.ejecutarCon(peleaId) { it.registrarRecepcionDe(habilidad) }
            aventureroActualizado
        }

    override fun terminarPelea(idDeLaPelea: Long) =
        runTrx { peleaDAO.ejecutarCon(idDeLaPelea) { it.finalizar() } }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {

        val paginaABuscar = pagina ?: 0
        if (paginaABuscar < 0) { throw RuntimeException("No se puede pedir una pagina negativa") }

            return runTrx {
                val peleasRecuperadas = peleaDAO.recuperarOrdenadas(partyId, paginaABuscar)
                val peleasTotales = peleaDAO.cantidadDePeleas().toInt()
                PeleasPaginadas(
                        peleasRecuperadas,
                        peleasTotales)
            }
    }
}