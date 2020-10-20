package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class AventureroLeaderboardServiceImpl(val aventureroDAO: AventureroDAO) : AventureroLeaderboardService {

    override fun mejorGuerrero() = runTrx { aventureroDAO.mejorGuerrero() }

    override fun mejorMago() = runTrx { aventureroDAO.mejorMago() }

    override fun mejorCurandero() = runTrx { aventureroDAO.mejorCurandero() }

    override fun buda() = runTrx { aventureroDAO.buda() }

}