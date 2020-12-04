package ar.edu.unq.epers.tactics.service.runner

import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig

class OrientDBSessionFactoryProvider private constructor() {

    private var orientDb: OrientDB
    var db: ODatabaseSession

    init {
        orientDb = OrientDB("remote:localhost", OrientDBConfig.defaultConfig())

        db = orientDb.open("test", "admin", "admin")
    }


    fun createSession(): ODatabaseSession {
        db = orientDb.open("test", "admin", "admin")

        if(db.getClass("Party")==null) db.createVertexClass("Party")
        if(db.getClass("Item")==null) db.createVertexClass("Item")
        if(db.getClass("haComprado")==null) db.createEdgeClass("haComprado")

        return db
    }

    companion object {

        private var INSTANCE: OrientDBSessionFactoryProvider? = null

        val instance: OrientDBSessionFactoryProvider
            get() {
                if (INSTANCE == null) {
                    INSTANCE =
                        OrientDBSessionFactoryProvider()
                }
                return INSTANCE!!
            }
    }
}
