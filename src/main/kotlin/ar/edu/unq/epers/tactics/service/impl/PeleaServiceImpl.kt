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

    override fun iniciarPelea(idDeLaParty: Long, partyEnemiga:String): Pelea { // TODO: se agrego partyEnemiga:String
        return runTrx {
            val party = partyDAO.recuperar(idDeLaParty)
            party.entrarEnPelea()
            partyDAO.actualizar(party)
            peleaDAO.crear(Pelea(party))
        }
    }

    override fun estaEnPelea(partyId: Long) = runTrx { partyDAO.recuperar(partyId).estaEnPelea() }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>) =
        runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val habilidadGenerada = aventurero.resolverTurno(enemigos)
            aventureroDAO.actualizar(aventurero)

            val pelea = peleaDAO.recuperar(peleaId)
            pelea.registrarEmisionDe(habilidadGenerada)
            peleaDAO.actualizar(pelea)

            habilidadGenerada
        }

    override fun recibirHabilidad(peleaId: Long, aventureroId: Long, habilidad: Habilidad) =
        runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)

            habilidad.resolversePara(aventurero)

            val pelea = peleaDAO.recuperar(peleaId)
            pelea.registrarRecepcionDe(habilidad)
            peleaDAO.actualizar(pelea)

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
        TODO("Not yet implemented")
    }
}