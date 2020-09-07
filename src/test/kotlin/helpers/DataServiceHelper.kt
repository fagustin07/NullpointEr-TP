package helpers

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.persistencia.dao.jdbc.JDBCPartyDAO
import ar.edu.unq.epers.tactics.service.PersistentPartyService
import ar.edu.unq.unidad1.wop.dao.impl.JDBCConnector.execute

class DataServiceHelper : DataService {
    override fun crearSetDeDatosIniciales() {
        val service = PersistentPartyService(JDBCPartyDAO())

        val aTeam = Party("The A team")
        val bTeam = Party("The B team")
        val cTeam = Party("The C team")

        val idATeam = service.crear(aTeam)
        val idBTeam = service.crear(bTeam)
        val idCTeam = service.crear(cTeam)

        val aventureroParaATeam = Aventurero(aTeam, 5, "Legolas")
        val aventureroParaBTeam = Aventurero(bTeam, 10, "Gimli")
        val aventureroParaCTeam1 = Aventurero(cTeam, 3, "Frodo")
        val aventureroParaCTeam2 = Aventurero(cTeam, 3, "Sam")

        service.agregarAventureroAParty(idATeam, aventureroParaATeam)
        service.agregarAventureroAParty(idBTeam, aventureroParaBTeam)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam1)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam2)
    }

    override fun eliminarTodo() {
        val sqlQuery = "TRUNCATE TABLE party"
        execute { conn->
            val stmt = conn.prepareStatement(sqlQuery)
            stmt.executeUpdate()
            stmt.close()
        }
    }
}