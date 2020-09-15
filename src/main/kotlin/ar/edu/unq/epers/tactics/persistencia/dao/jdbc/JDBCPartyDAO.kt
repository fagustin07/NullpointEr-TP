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

            val partyId = recuperarPartyID(ps)

            party.id = partyId
            ps.close()
            partyId
        }
    }

    override fun actualizar(party: Party) {
        checkearSiPartyTieneId(party)
        execute { connection ->
            val ps = connection.prepareStatement(
                "UPDATE party SET numeroDeAventureros = ? WHERE id = ?"
            )
            ps.setInt(1, party.numeroDeAventureros)
            ps.setLong(2, party.id!!)
            ps.executeUpdate()
            ps.close()
        }
    }

    private fun checkearSiPartyTieneId(party: Party) {
        if (party.id == null) {
            throw RuntimeException("No se puede actualizar una party que no fue creada")
        }
    }

    override fun recuperar(idDeLaParty: Long): Party {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT * FROM party WHERE id = ?")
            ps.setLong(1, idDeLaParty)
            val resultSet = ps.executeQuery()

            if (!resultSet.next()) throw RuntimeException("No hay ninguna party con el id provisto")

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

    private fun recuperarPartyID(ps: PreparedStatement): Long {
        var partyID: Long? = null
        ps.generatedKeys.use { generatedKeys ->
            if (generatedKeys.next()) {
                partyID = generatedKeys.getLong(1)
            }
        }
        return partyID!!
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
