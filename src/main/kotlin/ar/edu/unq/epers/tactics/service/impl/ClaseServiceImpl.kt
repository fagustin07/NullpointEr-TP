package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.ClaseService
import java.lang.RuntimeException

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
        claseDAO.verificarBidireccionalidad(nombreClaseInicio,nombreClaseAMejorar)
        return claseDAO.crearMejora(nombreClaseInicio,nombreClaseAMejorar,atributos,valorAAumentar)
    }

//    private fun verificarBidireccionalidad(nombreClaseInicio: String,nombreClaseAMejorar: String){
//        val clases = claseDAO.verificarBidireccionalidad(nombreClaseInicio,nombreClaseAMejorar)
//        val condicion = clases.any { it.nombreDeLaClase == nombreClaseInicio }
//        if(condicion){
//            throw RuntimeException("La mejora que estas queriendo crear no es posible")
//        }
//    }

    override fun recuperarTodas(): List<Clase> {
        return claseDAO.recuperarTodas()
    override fun requerir(clasePredecesora: Clase, claseSucesora: Clase) {
        verificarBidireccionalidad(clasePredecesora, claseSucesora)
        claseDAO.requerir(clasePredecesora.nombre(), claseSucesora.nombre())
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