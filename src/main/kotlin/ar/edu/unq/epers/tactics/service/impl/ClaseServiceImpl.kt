package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.ClaseService

class ClaseServiceImpl(private val claseDAO: ClaseDAO) : ClaseService {

    override fun crearClase(nombreDeLaClase: String): Clase {
        val clase = Clase(nombreDeLaClase)
        return claseDAO.crear(clase)
    }

    override fun crearMejora(
        nombreClaseInicio: String,
        nombreClaseAMejorar: String,
        atributos: List<String>,
        valorAAumentar: Int
    ): Mejora {
        return claseDAO.crearMejora(nombreClaseInicio,nombreClaseAMejorar,atributos,valorAAumentar)
    }


    override fun recuperarTodas(): List<Clase> {
        return claseDAO.recuperarTodas()
    }
}