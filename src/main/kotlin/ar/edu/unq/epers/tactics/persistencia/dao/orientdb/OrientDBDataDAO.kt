package ar.edu.unq.epers.tactics.persistencia.dao.orientdb

import ar.edu.unq.epers.tactics.modelo.calendario.ProveedorDeFechasReal
import ar.edu.unq.epers.tactics.persistencia.dao.DataDAO
import ar.edu.unq.epers.tactics.service.runner.OrientDBTransactionRunner.runTrx

open class OrientDBDataDAO : DataDAO {


    override fun clear() {
        val proveedorDeFechas = ProveedorDeFechasReal()

        runTrx{
            listOf(OrientDBItemDAO(proveedorDeFechas),OrientDBInventarioPartyDAO(), OrientDBOperacionesDAO(proveedorDeFechas))
                .forEach { it.clear() }
        }
    }
}
