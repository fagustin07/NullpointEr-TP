package helpers

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.PartyService

class DataServiceHelper(val service: PartyService) : DataService {

    override fun crearSetDeDatosIniciales() {

        val aTeam = Party("The A team")
        val bTeam = Party("The B team")
        val cTeam = Party("The C team")

        val idATeam = service.crear(aTeam).id!!
        val idBTeam = service.crear(bTeam).id!!
        val idCTeam = service.crear(cTeam).id!!

        val aventureroParaATeam = Aventurero(aTeam, 5, "Legolas")
        val aventureroParaBTeam = Aventurero(bTeam, 10, "Gimli")
        val aventureroParaCTeam1 = Aventurero(cTeam, 3, "Frodo")
        val aventureroParaCTeam2 = Aventurero(cTeam, 3, "Sam")

        service.agregarAventureroAParty(idATeam, aventureroParaATeam)
        service.agregarAventureroAParty(idBTeam, aventureroParaBTeam)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam1)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam2)
    }

    override fun eliminarTodo() = service.eliminarTodo()
}