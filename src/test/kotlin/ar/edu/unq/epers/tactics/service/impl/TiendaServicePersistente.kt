package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBItemDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBPartyDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx

class TiendaServicePersistente(protected val partyMonedasDAO: OrientDBPartyDAO, protected val itemDAO: OrientDBItemDAO) {

    fun registrarParty(partyId: Long, cantidadDeMonedasIniciales: Int) =
        runTrx { partyMonedasDAO.registrar(partyId, cantidadDeMonedasIniciales) }

    fun recuperar(partyId: Long) =
        runTrx { partyMonedasDAO.recuperar(partyId) }


}
