package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.PeleasPaginadas
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO): PeleaService {

    override fun iniciarPelea(partyId: Long, nombrePartyEnemiga:String): Pelea {
        return runTrx {
            val party = partyDAO.recuperar(partyId)
            party.entrarEnPelea()
            partyDAO.actualizar(party)
            peleaDAO.crear(Pelea(party,nombrePartyEnemiga))
        }
    }

    override fun estaEnPelea(partyId: Long) = runTrx { partyDAO.recuperar(partyId).estaEnPelea() }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>) =
        runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val habilidadGenerada = aventurero.resolverTurno(enemigos)
            aventureroDAO.actualizar(aventurero)
            habilidadGenerada
        }

    override fun recibirHabilidad(aventureroId: Long, habilidad: Habilidad) =
        runTrx {
            //TODO: agregar el peleaID.
            val aventurero = aventureroDAO.recuperar(aventureroId)

            habilidad.resolversePara(aventurero)

            aventureroDAO.actualizar(aventurero)
        }

    override fun terminarPelea(idDeLaPelea: Long) =
        runTrx {
            val pelea = peleaDAO.recuperar(idDeLaPelea)
            pelea.finalizar()
            peleaDAO.actualizar(pelea)

            pelea
        }

    override fun recuperarOrdenadas(partyId: Long, pagina: Int?): PeleasPaginadas {

        val paginaABuscar = pagina ?: 0
        if (paginaABuscar < 0) { throw RuntimeException("No se puede pedir una pagina negativa") }

            return runTrx {
                val peleasRecuperadas = peleaDAO.recuperarOrdenadas(partyId, paginaABuscar)
                val peleasTotales = peleaDAO.cantidadDePeleas().toInt()
                PeleasPaginadas(
                        peleasRecuperadas,
                        peleasTotales)
            }
    }
}