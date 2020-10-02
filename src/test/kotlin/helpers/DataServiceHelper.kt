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

        val aventureroParaATeam = Aventurero("Legolas", 15, 23, 57, 87)
        val aventureroParaBTeam = Aventurero("Gimli", 10, 24, 74, 16)
        val aventureroParaCTeam1 = Aventurero("Frodo", 99, 45, 7, 80)
        val aventureroParaCTeam2 = Aventurero("Sam", 16, 20, 76, 8)

        service.agregarAventureroAParty(idATeam, aventureroParaATeam)
        service.agregarAventureroAParty(idBTeam, aventureroParaBTeam)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam1)
        service.agregarAventureroAParty(idCTeam, aventureroParaCTeam2)
    }

    override fun eliminarTodo() = service.eliminarTodo()
}