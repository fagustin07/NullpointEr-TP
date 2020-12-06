package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import com.orientechnologies.orient.core.db.ODatabaseSession

open class OrientDBDataDAO : DataDAO {


    override fun clear() {
        runTrx{
            listOf(OrientDBItemDAO(),OrientDBInventarioPartyDAO(), OrientDBOperacionesDAO())
                .forEach { it.clear() }
        }
    }
}
