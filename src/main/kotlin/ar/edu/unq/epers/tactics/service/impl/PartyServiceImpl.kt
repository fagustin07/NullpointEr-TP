package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx


class PartyServiceImpl(val dao: PartyDAO) : PartyService {

    override fun crear(party: Party) = runTrx { dao.crear(party) }

    override fun actualizar(party: Party) = runTrx { dao.actualizar(party) }

    override fun recuperar(idDeLaParty: Long) = runTrx { dao.recuperar(idDeLaParty) }

    override fun recuperarTodas() = runTrx { dao.recuperarTodas() }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        TODO("Not yet implemented") // TODO: se agrego en el HITO 2
    }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        return runTrx {
            dao.ejecutarCon(idDeLaParty) { it.agregarUnAventurero(aventurero) }
            aventurero
        }
    }

    override fun eliminarTodo() = runTrx { dao.eliminarTodo() }

}
