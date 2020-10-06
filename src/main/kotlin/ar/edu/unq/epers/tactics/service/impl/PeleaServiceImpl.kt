package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO) :
    PeleaService {

    override fun iniciarPelea(idDeLaParty: Long) =
        runTrx {
            val party = partyDAO.recuperar(idDeLaParty)
            party.entrarEnPelea()
            partyDAO.actualizar(party)

            peleaDAO.crear(Pelea(idDeLaParty))
        }

    override fun estaEnPelea(partyId: Long) = runTrx { partyDAO.recuperar(partyId).estaEnPelea() }

    override fun actualizar(pelea: Pelea) = runTrx { peleaDAO.actualizar(pelea) }

    override fun recuperar(idDeLaPelea: Long) = runTrx { peleaDAO.recuperar(idDeLaPelea) }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>) =
        runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val habilidadGenerada = aventurero.resolverTurno(enemigos)
            aventureroDAO.actualizar(aventurero)
            habilidadGenerada
        }

    override fun recibirHabilidad(aventureroId: Long, habilidad: Habilidad) =
        runTrx {
            habilidad.resolverse()
            val aventureroDespuesDeRecibirHabilidad = habilidad.aventureroReceptor
            aventureroDAO.actualizar(aventureroDespuesDeRecibirHabilidad)
        }

    override fun terminarPelea(idDeLaParty: Long) =
        runTrx {
            val partyRecuperada = partyDAO.recuperar(idDeLaParty)
            partyRecuperada.salirDePelea()
            partyDAO.actualizar(partyRecuperada)

            this.peleaDeParty(idDeLaParty)
        }

    private fun peleaDeParty(idDeLaParty: Long): Pelea {
        return peleaDAO.recuperarUltimaPeleaDeParty(idDeLaParty)
    }
}