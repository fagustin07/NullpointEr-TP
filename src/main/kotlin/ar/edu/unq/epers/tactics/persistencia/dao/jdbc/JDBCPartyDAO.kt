package ar.edu.unq.epers.tactics.persistencia.dao.jdbc

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.unidad1.wop.dao.impl.JDBCConnector.execute
import java.sql.Connection
import java.sql.PreparedStatement

class JDBCPartyDAO: IPartyDAO {

    override fun crear(party: Party): Long {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("INSERT INTO party (nombre) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
            ps.setString(1, party.nombre)
            ps.executeUpdate()
            chequearCreacionDeParty(ps, party)
            recuperarPartyID(ps, party)
        }
    }

    private fun recuperarPartyID(ps: PreparedStatement, party: Party): Long {
        var partyID: Long? = null
        ps.generatedKeys.use { generatedKeys ->
            if (generatedKeys.next()) {
                partyID = generatedKeys.getLong(1)
            } else {
                throw RuntimeException("Ha fallado la creacion, no se pudo obtener la ID de $party.")
            }
        }
        ps.close()
        return partyID!!
    }

    fun eliminarTablaDeParty() {
        execute { conn: Connection ->
            val ps = conn.prepareStatement("DROP TABLE party")
            ps.executeUpdate()
            ps.close()
        }
    }

    override fun actualizar(party: Party) {
        TODO("Not yet implemented")
    }

    override fun recuperar(idDeLaParty: Long): Party {
        TODO("Not yet implemented")
    }

    override fun recuperarTodas(): List<Party> {
        TODO("Not yet implemented")
    }

    private fun chequearCreacionDeParty(ps: PreparedStatement, party: Party) {
        if (ps.updateCount != 1) {
            throw RuntimeException("No se creo correctamente la party $party")
        }
    }

    init {
        val initializeScript = javaClass.classLoader.getResource("createAll.sql")?.readText()
        execute {
            val ps = it.prepareStatement(initializeScript)
            ps.execute()
            ps.close()
            null
        }
    }
}