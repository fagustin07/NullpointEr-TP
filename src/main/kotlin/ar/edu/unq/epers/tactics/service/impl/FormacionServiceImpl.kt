package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.service.FormacionService

class FormacionServiceImpl(val formacionDAO: FormacionDAO) : FormacionService {

    override fun crearFormacion(
        nombreFormacion: String,
        requerimientos: List<Clase>,
        stats: List<AtributoDeFormacion>
    ): Formacion {
        return Formacion(nombreFormacion,requerimientos,stats)
    }

    override fun todasLasFormaciones(): List<Formacion> {
        TODO("Not yet implemented")
    }

    override fun atributosQueCorresponden(partyId: Int): List<AtributoDeFormacion> {
        TODO("Not yet implemented")
    }

    override fun formacionesQuePosee(partyId: Int): List<Formacion> {
        TODO("Not yet implemented")
    }
}