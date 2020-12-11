package ar.edu.unq.epers.tactics.service.runner

import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType

class OrientDBSessionFactoryProvider private constructor() {

    private var orientDb: OrientDB
    lateinit var session: ODatabaseSession

    init {
        orientDb = OrientDB("remote:localhost", "root","root",OrientDBConfig.defaultConfig())
        orientDb.createIfNotExists("epers_tactics_db",ODatabaseType.PLOCAL)
    }


    fun createSession(): ODatabaseSession {
        session = orientDb.open("epers_tactics_db", "admin", "admin")

        if(session.getClass("InventarioParty")==null) {
            val inventarioPartyClass = session.createVertexClass("InventarioParty")
            inventarioPartyClass.createProperty("nombre", OType.STRING)
            inventarioPartyClass.createProperty("monedas", OType.INTEGER)

            inventarioPartyClass.createIndex("index_InventarioParty", OClass.INDEX_TYPE.UNIQUE, "nombre")
        }

        if(session.getClass("Item")==null) {
            val itemClass = session.createVertexClass("Item")
            itemClass.createProperty("nombre", OType.STRING)
            itemClass.createProperty("precio", OType.INTEGER)

            itemClass.createIndex("index_Ttem", OClass.INDEX_TYPE.UNIQUE, "nombre")

        }

        if(session.getClass("haComprado")==null) session.createEdgeClass("haComprado")
        if(session.getClass("haVendido")==null) session.createEdgeClass("haVendido")

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
