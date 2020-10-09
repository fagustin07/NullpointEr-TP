package helpers

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.PartyService

class DataServiceHelper(val service: PartyService) : DataService {

    override fun crearSetDeDatosIniciales() {

        val aTeam = Party("The A team", "URL")
        val bTeam = Party("The B team", "URL")
        val cTeam = Party("The C team", "URL")

        val idATeam = service.crear(aTeam).id()!!
        val idBTeam = service.crear(bTeam).id()!!
        val idCTeam = service.crear(cTeam).id()!!

        val aventureroParaATeam = Aventurero("Legolas", "",15.0, 23.0, 57.0, 87.0)
        val aventureroParaBTeam = Aventurero("Gimli","", 10.0, 24.0, 74.0, 16.0)
        val aventureroParaCTeam1 = Aventurero("Frodo","", 99.0, 45.0, 7.0, 80.0)
        val aventureroParaCTeam2 = Aventurero("Sam","", 16.0, 20.0, 76.0, 8.0)

        service.agregarAventureroAParty(idATeam, aventureroParaATeam)
        service.agregarAventureroAParty(idBTeam, aventureroParaBTeam)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam1)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam2)
    }

    override fun eliminarTodo() = service.eliminarTodo()
}