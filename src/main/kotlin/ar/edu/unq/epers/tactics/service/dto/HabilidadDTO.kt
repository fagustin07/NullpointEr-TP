package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.habilidades.Habilidad
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = AtaqueDTO::class, name = "Attack"),
        JsonSubTypes.Type(value = DefensaDTO::class, name = "Defend"),
        JsonSubTypes.Type(value = CurarDTO::class, name = "Heal"),
        JsonSubTypes.Type(value = AtaqueMagicoDTO::class, name = "MagicAttack"),
        JsonSubTypes.Type(value = MeditarDTO::class, name = "Meditate")
)
abstract class HabilidadDTO(){
    companion object {

        fun desdeModelo(habilidad: Habilidad):HabilidadDTO{
            TODO()
        }
    }

    fun aModelo(): Habilidad {
        TODO()
    }
}

data class AtaqueDTO(val tipo:String, val daño: Double, val prisicionFisica: Double, val objetivo: AventureroDTO): HabilidadDTO()
class DefensaDTO(val tipo:String, val source: AventureroDTO, val objetivo: AventureroDTO): HabilidadDTO()
data class CurarDTO(val tipo:String, val poderMagico: Double, val objetivo: AventureroDTO): HabilidadDTO()
data class AtaqueMagicoDTO(val tipo:String, val poderMagico: Double, val sourceLevel: Int, val objetivo: AventureroDTO): HabilidadDTO()
class MeditarDTO(val objetivo: AventureroDTO): HabilidadDTO()