package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.modelo.tienda.InventarioParty
import ar.edu.unq.epers.tactics.persistencia.dao.InventarioPartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.Direccion
import ar.edu.unq.epers.tactics.service.Orden
import ar.edu.unq.epers.tactics.service.PartyPaginadas
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner

class PartyServiceImpl(val partyDAO: PartyDAO, val inventarioPartyDAO: InventarioPartyDAO) : PartyService {

    override fun crear(party: Party) = runTrx {
        OrientDBTransactionRunner.runTrx {
            inventarioPartyDAO.guardar(InventarioParty(party.nombre()))
        }

        partyDAO.crear(party)
    }

    override fun actualizar(party: Party) = runTrx { partyDAO.actualizar(party) }

    override fun recuperar(idDeLaParty: Long) = runTrx { partyDAO.recuperar(idDeLaParty) }

    override fun recuperarTodas() = runTrx { partyDAO.recuperarTodas() }

    override fun recuperarOrdenadas(orden: Orden, direccion: Direccion, pagina: Int?): PartyPaginadas {
        val paginaSolicitada = pagina ?: 0
        if(paginaSolicitada < 0) throw RuntimeException("No puedes pedir paginas negativas")

       return runTrx {
             val recuperadas = partyDAO.recuperarOrdenadas(orden,direccion,paginaSolicitada)
             PartyPaginadas(recuperadas, partyDAO.cantidadDePartys().toInt())
        }
    }

    override fun recuperarPorNombre(nombre: String): Party {
        return runTrx { partyDAO.recuperarPorNombre(nombre) }
    }

    override fun agregarAventureroAParty(idDeLaParty: Long, aventurero: Aventurero): Aventurero {
        return runTrx {
            partyDAO.ejecutarCon(idDeLaParty) { it.agregarUnAventurero(aventurero) }
            aventurero
        }
    }

    override fun eliminarTodo() = runTrx { partyDAO.eliminarTodo() }

}
