package ar.edu.unq.epers.tactics.persistencia.dao.jdbc

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.unidad1.wop.dao.impl.JDBCConnector.execute
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class JDBCPartyDAO : IPartyDAO {

    override fun crear(party: Party): Long {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("INSERT INTO party (nombre) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
            ps.setString(1, party.nombre)
            ps.executeUpdate()
            chequearCreacionDeParty(ps, party)
            val partyId = recuperarPartyID(ps, party)
            party.id = partyId
            partyId
        }
    }

    override fun actualizar(party: Party) {
        execute { connection ->
            val ps = connection.prepareStatement(
                    "UPDATE party SET numeroDeAventureros = ${party.numeroDeAventureros} WHERE id = ${party.id}"
            )
            ps.executeUpdate()
            ps.close()
        }
    }

    override fun recuperar(idDeLaParty: Long): Party {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT * FROM party WHERE id = ?")
            ps.setLong(1, idDeLaParty)
            val resultSet = ps.executeQuery()
            resultSet.next()

            val party = mapPartyToObjectFrom(resultSet)

            ps.close()
            party
        }
    }

    override fun recuperarTodas() =
            execute { connection ->
                val ps = connection.prepareStatement("SELECT * FROM party ORDER BY nombre ASC")
                val resultSet = ps.executeQuery()

                val parties = mutableListOf<Party>()

                while (resultSet.next()) {
                    parties.add(mapPartyToObjectFrom(resultSet))
                }

                ps.close()
                parties
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

    private fun chequearCreacionDeParty(ps: PreparedStatement, party: Party) {
        if (ps.updateCount != 1) {
            throw RuntimeException("No se creo correctamente la party $party")
        }
    }

    private fun mapPartyToObjectFrom(resultSet: ResultSet): Party {
        val id = resultSet.getLong("id")
        val nombre = resultSet.getString("nombre")
        val numeroDeAventureros = resultSet.getInt("numeroDeAventureros")

        val party = Party(nombre)
        party.id = id
        party.numeroDeAventureros = numeroDeAventureros

        return party
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