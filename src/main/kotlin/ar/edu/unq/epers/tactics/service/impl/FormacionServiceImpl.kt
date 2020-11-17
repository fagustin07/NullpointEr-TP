package ar.edu.unq.epers.tactics.service.impl

import ar.edu.unq.epers.tactics.modelo.AtributoDeFormacion
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO
import ar.edu.unq.epers.tactics.service.FormacionService
import ar.edu.unq.epers.tactics.service.PartyService

class FormacionServiceImpl(val formacionDAO: FormacionDAO, val partyService: PartyService) : FormacionService {

    override fun crearFormacion(
        nombreFormacion: String,
        requerimientos: Map<String, Int>,
        stats: List<AtributoDeFormacion>
    ): Formacion {
        val formacion = Formacion(nombreFormacion, requerimientos, stats)

        return formacionDAO.guardar(formacion)
    }

    override fun todasLasFormaciones(): List<Formacion> {
        return formacionDAO.getAll()
    }

    override fun atributosQueCorresponden(partyId: Long): List<AtributoDeFormacion> {
        TODO("Not yet implemented")
    }

    override fun formacionesQuePosee(partyId: Long): List<Formacion> {
        val party = partyService.recuperar(partyId)
        return formacionDAO.formacionesQuePosee(party)
    }
}