package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.AventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.ClaseService
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.neo4j.driver.exceptions.NoSuchRecordException

class ClaseServiceImpl(private val claseDAO: ClaseDAO, val aventureroDAO: AventureroDAO) : ClaseService {

    override fun crearClase(nombreDeLaClase: String): Clase {
        val clase = Clase(nombreDeLaClase)
        return claseDAO.crear(clase)
    }

    override fun crearMejora(nombreClaseInicio: String, nombreClaseAMejorar: String, atributos: List<Atributo>, valorAAumentar: Int): Mejora {
        return claseDAO.crearMejora(nombreClaseInicio, nombreClaseAMejorar, atributos, valorAAumentar)
    }

    override fun requerir(nombreClasePredecesora: String, nombreClaseSucesora: String) {
        claseDAO.requerir(nombreClasePredecesora, nombreClaseSucesora)
    }

    override fun puedeMejorar(aventureroID: Long, mejora: Mejora): Boolean =
        HibernateTransactionRunner.runTrx {
            aventureroDAO.resultadoDeEjecutarCon(aventureroID) {
                claseDAO.puedeMejorarse(it, mejora)
            }
        }

    override fun posiblesMejoras(aventureroID: Long): Set<Mejora> {
        return HibernateTransactionRunner.runTrx {
            aventureroDAO.resultadoDeEjecutarCon(aventureroID) {
                claseDAO.posiblesMejorasPara(it)
            }
        }
    }

    override fun ganarProficiencia(aventureroId: Long,  nombreClaseInicio: String, nombreClaseAMejorar: String): Aventurero {
        return HibernateTransactionRunner.runTrx {
            aventureroDAO.resultadoDeEjecutarCon(aventureroId) {
                val mejoraBuscada = buscarLaMejora(nombreClaseInicio, nombreClaseAMejorar)
                obtenerMejoraSiDebe(it, mejoraBuscada)
                it
            }
        }
    }

    override fun caminoMasRentable(puntosDeExperiencia: Int, aventureroId: Long, atributo: Atributo): List<Mejora> {
        return HibernateTransactionRunner.runTrx {
            aventureroDAO.resultadoDeEjecutarCon(aventureroId) {
                claseDAO.caminoMasRentable(puntosDeExperiencia, it.clases(), atributo)
            }
        }
    }

    private fun buscarLaMejora(nombreDeLaClaseInicio: String, nombreDeLaClaseAMejorar: String): Mejora {
        try {
            return claseDAO.buscarMejora(nombreDeLaClaseInicio, nombreDeLaClaseAMejorar)
        } catch (e: NoSuchRecordException) {
            throw RuntimeException("La mejora de $nombreDeLaClaseInicio hacia $nombreDeLaClaseAMejorar no existe.")
        }
    }

    private fun obtenerMejoraSiDebe(aventurero: Aventurero, mejoraBuscada: Mejora) {
        verificarQuePuedaAplicarseMejoraA(aventurero, mejoraBuscada)
        aventurero.obtenerMejora(mejoraBuscada)
    }

    private fun verificarQuePuedaAplicarseMejoraA(aventurero: Aventurero, mejoraBuscada: Mejora) {
        if (!claseDAO.puedeMejorarse(aventurero, mejoraBuscada))
            throw RuntimeException("El aventurero no cumple las condiciones para obtener una mejora.")
    }
}
