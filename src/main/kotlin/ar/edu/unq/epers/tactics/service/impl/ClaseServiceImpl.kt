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

    override fun crearMejora(
        nombreClaseInicio: String,
        nombreClaseAMejorar: String,
        atributos: List<Atributo>,
        valorAAumentar: Int
    ): Mejora {
        claseDAO.verificarBidireccionalidad(nombreClaseInicio, nombreClaseAMejorar)
        return claseDAO.crearMejora(nombreClaseInicio, nombreClaseAMejorar, atributos, valorAAumentar)
    }

    override fun requerir(clasePredecesora: Clase, claseSucesora: Clase) {
        verificarBidireccionalidad(clasePredecesora, claseSucesora)
        claseDAO.requerir(clasePredecesora.nombre(), claseSucesora.nombre())
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

    override fun ganarProficiencia(
        aventureroId: Long,
        nombreClaseInicio: String,
        nombreClaseAMejorar: String
    ): Aventurero {
        return HibernateTransactionRunner.runTrx {
            val aventurero = aventureroDAO.recuperar(aventureroId)
            val mejoraBuscada = buscarLaMejora(nombreClaseInicio, nombreClaseAMejorar)
            obtenerMejoraSiDebe(aventurero, mejoraBuscada)
            aventureroDAO.actualizar(aventurero)
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

    private fun verificarBidireccionalidad(clasePredecesora: Clase, claseSucesora: Clase) {
        if (esRequeridaPor(clasePredecesora, claseSucesora)) {
            throw RuntimeException("No se puede establecer una relacion bidireccional entre ${clasePredecesora.nombre()} y ${claseSucesora.nombre()}")
        }
    }

    private fun esRequeridaPor(claseSucesora: Clase, claseAntecesora: Clase): Boolean {
        return claseDAO.requiereEnAlgunNivelDe(claseSucesora, claseAntecesora)
    }
}
