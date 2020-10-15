package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner

class AventureroLeaderboardServiceImpl(val aventureroDAO: AventureroDAO) : AventureroLeaderboardService {

    override fun mejorGuerrero(): Aventurero {
        TODO("Not yet implemented")
    }

    override fun mejorMago(): Aventurero {
        TODO("Not yet implemented")
    }

    override fun mejorCurandero(): Aventurero {
        TODO("Not yet implemented")
    }

    override fun buda() = HibernateTransactionRunner.runTrx { aventureroDAO.buda() }

}