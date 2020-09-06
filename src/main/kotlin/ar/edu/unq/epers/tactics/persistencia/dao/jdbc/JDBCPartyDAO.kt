package ar.edu.unq.epers.tactics.persistencia.dao.jdbc

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.unidad1.wop.dao.impl.JDBCConnector.execute
import java.sql.Connection
import java.sql.PreparedStatement

class JDBCPartyDAO: IPartyDAO {

    override fun crear(party: Party): Long {
        return execute { conn: Connection ->
            crearParty(conn, party)
            obtenerPartyId(conn, party)
        }
    }
    fun eliminarTablaDeParty(){
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

    private fun crearParty(conn: Connection, party: Party) {
        val ps = conn.prepareStatement("INSERT INTO party (nombre) VALUES (?)")
        ps.setString(1, party.nombre)
        ps.executeUpdate()
        chequearCreacionDeParty(ps, party)
        ps.close()
    }

    private fun obtenerPartyId(conn: Connection, party: Party): Long {
        val ps = conn.prepareStatement("SELECT id FROM party  WHERE nombre=?")
        ps.setString(1,party.nombre)
        val resultSet = ps.executeQuery()
        var partyID: Long? = null
        while (resultSet.next()) partyID = resultSet.getLong("id")
        ps.close()
        return partyID!!
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
