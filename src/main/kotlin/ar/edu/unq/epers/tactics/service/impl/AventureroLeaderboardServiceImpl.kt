package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class AventureroLeaderboardServiceImpl(val aventureroDAO: AventureroDAO) : AventureroLeaderboardService {

    override fun mejorGuerrero() = runTrx { aventureroDAO.mejorGuerrero() }

    override fun mejorMago(): Aventurero {
        TODO("Not yet implemented")
    }

    override fun mejorCurandero(): Aventurero {
        TODO("Not yet implemented")
    }

    override fun buda() = runTrx { aventureroDAO.buda() }

}