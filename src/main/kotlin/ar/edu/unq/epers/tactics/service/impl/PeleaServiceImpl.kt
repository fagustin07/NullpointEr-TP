package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBPartyDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO, val partyMonedasDAO: OrientDBPartyDAO): PeleaService {

    override fun iniciarPelea(partyId: Long, nombrePartyEnemiga:String): Pelea {
        return runTrx {
            val party = partyDAO.ejecutarCon(partyId) { it.entrarEnPelea() }
            peleaDAO.crear(Pelea(party,nombrePartyEnemiga))
        }
    }

    override fun estaEnPelea(partyId: Long) = runTrx { partyDAO.recuperar(partyId).estaEnPelea() }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>) =
        runTrx {
            val habilidadGenerada = (aventureroDAO.resultadoDeEjecutarCon(aventureroId) { it.resolverTurno(enemigos) })
            peleaDAO.ejecutarCon(peleaId) { it.registrarEmisionDe(habilidadGenerada) }
            habilidadGenerada
        }

    override fun recibirHabilidad(peleaId: Long, aventureroId: Long, habilidad: Habilidad) =
        runTrx {
            val aventureroActualizado = aventureroDAO.ejecutarCon(aventureroId) { habilidad.resolversePara(it) }
            peleaDAO.ejecutarCon(peleaId) { it.registrarRecepcionDe(habilidad) }
            aventureroActualizado

        }

    override fun terminarPelea(idDeLaPelea: Long) =
          runTrx {
              val pelea = peleaDAO.ejecutarCon(idDeLaPelea) { it.finalizar() }
              this.obtenerRecompensaSiHaGanado(pelea)

              pelea
          }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {
        val paginaABuscar = pagina ?: 0
        if (paginaABuscar < 0) { throw RuntimeException("No se puede pedir una pagina negativa") }

        return runTrx {
            val peleasRecuperadas = peleaDAO.recuperarOrdenadas(partyId, paginaABuscar)
            val peleasTotales = peleaDAO.cantidadDePeleas().toInt()
            PeleasPaginadas(peleasRecuperadas, peleasTotales)
        }
    }

    private fun obtenerRecompensaSiHaGanado(pelea: Pelea) {
        if (pelea.fueGanada()) {
            OrientDBTransactionRunner.runTrx {
                val partyConMonedas = partyMonedasDAO.recuperar(pelea.party.nombre())
                partyConMonedas.adquirirRecompensaDePelea()
                partyMonedasDAO.actualizar(partyConMonedas)
            }
        }
    }
}