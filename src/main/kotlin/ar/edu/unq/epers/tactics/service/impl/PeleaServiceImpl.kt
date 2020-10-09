package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Pelea
import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PeleaDAO
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx
import java.lang.RuntimeException

class PeleaServiceImpl(val peleaDAO: PeleaDAO, val partyDAO: PartyDAO, val aventureroDAO: AventureroDAO) :
    PeleaService {

    override fun iniciarPelea(idDeLaParty: Long) =
        runTrx {
            val party = partyDAO.recuperar(idDeLaParty)
            party.entrarEnPelea()
            partyDAO.actualizar(party)
            peleaDAO.crear(Pelea(party))
        }

    override fun estaEnPelea(partyId: Long) = runTrx { partyDAO.recuperar(partyId).estaEnPelea() }

    override fun resolverTurno(peleaId: Long, aventureroId: Long, enemigos: List<Aventurero>) =
        //TODO: resolverTurno(idPelea:Long, idAventurero:Long, enemigos: List<Aventurero>) : Habilidad
        // - Dada la lista de enemigos, el aventurero debe utilizar sus Tacticas para elegir que
        // habilidad utilizar sobre que receptor. Deberiamos utilizar la pelea y recuperar el aventurero
        // desde la party que tenga vinculada?
        runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val habilidadGenerada = aventurero.resolverTurno(enemigos)
            aventureroDAO.actualizar(aventurero)
            habilidadGenerada
        }

    override fun recibirHabilidad(aventureroId: Long, habilidad: Habilidad) =
        runTrx {
            //TODO: recibirHabilidad(idPelea:Long, idAventurero:Long, habilidad: Habilidad):Aventurero
            // - El aventurero debe resolver la habilidad que esta siendo ejecutada sobre el,
            // chequear esto para que se ejecute correctamente

            val aventurero = aventureroDAO.recuperar(aventureroId)

            habilidad.resolversePara(aventurero)

            aventureroDAO.actualizar(aventurero)
/*
            habilidad.resolverse()
            val aventureroDespuesDeRecibirHabilidad = habilidad.aventureroReceptor
            aventureroDAO.actualizar(aventureroDespuesDeRecibirHabilidad)

 */
        }

    override fun terminarPelea(idDeLaPelea: Long) =
        runTrx {
            val pelea = peleaDAO.recuperar(idDeLaPelea)
            pelea.finalizar()
            peleaDAO.actualizar(pelea)

            pelea
        }

}