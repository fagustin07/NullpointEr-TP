package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.PartyDAO
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner.runTrx

class AventureroServiceImpl(val aventureroDAO: AventureroDAO, val partyDAO: PartyDAO, val formacionDAO: FormacionDAO): AventureroService {

    override fun actualizar(aventurero: Aventurero) = runTrx{ aventureroDAO.actualizar(aventurero) }

    override fun recuperar(idDelAventurero: Long) = runTrx { aventureroDAO.recuperar(idDelAventurero) }

    override fun eliminar(aventurero: Aventurero) {
        runTrx {
            val partyId = aventurero.party!!.id()!!

            partyDAO.ejecutarCon(partyId) {
                it.removerA(aventurero)
                it.actualizarAtributosEnBaseA(formacionDAO.formacionesQuePosee(it))
            }
        }
    }
}