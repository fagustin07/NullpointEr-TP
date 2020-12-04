package ar.edu.unq.epers.tactics.service.runner

import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig

class OrientDBSessionFactoryProvider private constructor() {

    private var orientDb: OrientDB
    lateinit var session: ODatabaseSession

    init {
        orientDb = OrientDB("remote:localhost", "root","root",OrientDBConfig.defaultConfig())
        orientDb.createIfNotExists("epers_tactics_db",ODatabaseType.PLOCAL)
    }


    fun createSession(): ODatabaseSession {
        session = orientDb.open("epers_tactics_db", "admin", "admin")

        if(session.getClass("PartyConMonedas")==null) session.createVertexClass("PartyConMonedas")
        if(session.getClass("Item")==null) session.createVertexClass("Item")
        if(session.getClass("haComprado")==null) session.createEdgeClass("haComprado")

        return session
    }

    companion object {

        private var INSTANCE: OrientDBSessionFactoryProvider? = null

        val instance: OrientDBSessionFactoryProvider
            get() {
                if (INSTANCE == null) {
                    INSTANCE = OrientDBSessionFactoryProvider()
                }
                return INSTANCE!!
            }
    }
}
