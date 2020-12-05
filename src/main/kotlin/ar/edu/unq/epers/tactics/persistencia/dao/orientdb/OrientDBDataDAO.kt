package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBSessionFactoryProvider
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx
import com.orientechnologies.orient.core.db.ODatabaseSession

open class OrientDBDataDAO : DataDAO {
    val db: ODatabaseSession get() = OrientDBSessionFactoryProvider.instance.session

    override fun clear() {
        runTrx{
            db.command("DELETE VERTEX FROM PartyConMonedas")
            db.command("DELETE VERTEX FROM Item")
            db.command("DELETE EDGE HaComprado")
        }
    }
}
