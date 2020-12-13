package helpers

import ar.edu.unq.epers.tactics.modelo.*
import ar.edu.unq.epers.tactics.modelo.enums.Accion
import ar.edu.unq.epers.tactics.modelo.enums.Criterio
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeEstadistica
import ar.edu.unq.epers.tactics.modelo.enums.TipoDeReceptor
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernateAventureroDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePartyDAO
import ar.edu.unq.epers.tactics.persistencia.dao.hibernate.HibernatePeleaDAO
import ar.edu.unq.epers.tactics.persistencia.dao.mongodb.MongoFormacionDAO
import ar.edu.unq.epers.tactics.persistencia.dao.orientdb.OrientDBInventarioPartyDAO
import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.AventureroService
import ar.edu.unq.epers.tactics.service.PartyService
import ar.edu.unq.epers.tactics.service.PeleaService
import ar.edu.unq.epers.tactics.service.impl.AventureroLeaderboardServiceImpl
import ar.edu.unq.epers.tactics.service.impl.AventureroServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PartyServiceImpl
import ar.edu.unq.epers.tactics.service.impl.PeleaServiceImpl
import java.lang.Math.random

class Factory {
    private val leaderboardService: AventureroLeaderboardService
    private val peleaService: PeleaService
    private val partyService: PartyService
    private val aventureroService : AventureroService
    private var miniId = 0

    init {
        val aventureroDAO = HibernateAventureroDAO()
        val peleaDAO = HibernatePeleaDAO()
        val partyDAO = HibernatePartyDAO()
        partyService = PartyServiceImpl(partyDAO, OrientDBInventarioPartyDAO(), MongoFormacionDAO())
        leaderboardService = AventureroLeaderboardServiceImpl(aventureroDAO)
        peleaService = PeleaServiceImpl(peleaDAO, partyDAO, aventureroDAO, OrientDBInventarioPartyDAO())
        aventureroService = AventureroServiceImpl(aventureroDAO, partyDAO, MongoFormacionDAO())
    }

    fun comenzarPeleaDe(partyId: Long): Long {
        val peleaId = peleaService.iniciarPelea(partyId, "party enemiga").id()!!
        return peleaId
    }

    private fun terminarPeleaDe(partyID: Long) {
        peleaService.terminarPelea(partyID)
    }

    fun nuevoGuerreroEn(partyId: Long): Aventurero {
        val aventurero = Aventurero("Guerrero " + random() * 999)
        val ataqueFisico = this.tacticaDeAtaqueFisicoSobreEnemigo()

        return aventureroEn(partyId, ataqueFisico, aventurero)
    }

    fun nuevoMagoEn(partyId: Long): Aventurero {
        val aventurero = Aventurero("Mago " + random() * 999)
        val ataqueMagico = this.tacticaDeAtaqueMagico()
        return aventureroEn(partyId, ataqueMagico, aventurero)
    }

    fun nuevoCuranderoEgoistaEn(partyId: Long): Aventurero {
        val aventurero = Aventurero("Curandero " + random() * 999)
        val curacion = this.tacticaDeCuracionUnoMismo()

        return aventureroEn(partyId, curacion, aventurero)

    }

    fun nuevoMeditadorEn(partyId: Long): Aventurero {
        val aventurero = Aventurero("Meditador " + random() * 999)
        val meditacion = this.tacticaDeMeditacion()

        return aventureroEn(partyId, meditacion, aventurero)

    }

    fun aventureroEn(partyId: Long, tactica: Tactica, aventurero: Aventurero): Aventurero {
        aventurero.agregarTactica(tactica)
        partyService.agregarAventureroAParty(partyId, aventurero)

        return aventurero
    }

    fun nuevaPartyPersistida(): Long {
        val party = Party("Nombre de party${miniId}", "/party.jpg")
        val partyId = partyService.crear(party).id()!!
        this.miniId+=1
        return partyId
    }

    fun tacticaDeCuracionUnoMismo() =
        Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.CURAR)

    fun tacticaDeAtaqueMagico() =
        Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.ATAQUE_MAGICO)

    fun tacticaDeMeditacion() =
        Tactica(1, TipoDeReceptor.UNO_MISMO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.MEDITAR)

    fun tacticaDeAtaqueFisicoSobreEnemigo() =
        Tactica(1, TipoDeReceptor.ENEMIGO, TipoDeEstadistica.VIDA, Criterio.MAYOR_QUE, 0.0, Accion.ATAQUE_FISICO)

    fun aventureroLeaderboardService() = this.leaderboardService
    fun partyService() = this.partyService
    fun peleaService() = this.peleaService
    fun aventureroService() = this.aventureroService

    fun crearAventureroConExperiencia(puntosDeExperiencia: Int): Aventurero {
        val partyID =  nuevaPartyPersistida()
        val aventurero = nuevoGuerreroEn(partyID)

        repeat(puntosDeExperiencia) {
            val peleaID = comenzarPeleaDe(partyID)
            terminarPeleaDe(peleaID)
        }

        return aventurero
    }

    fun crearStats(paresAtributoGanancia: List<Pair<String, Int>>) =
        paresAtributoGanancia.map { AtributoDeFormacion(it.first, it.second) }

    fun crearAventureroProficienteEnAventurero(partyId: Long): Aventurero {
        return nuevoMagoEn(partyId)
    }
}
