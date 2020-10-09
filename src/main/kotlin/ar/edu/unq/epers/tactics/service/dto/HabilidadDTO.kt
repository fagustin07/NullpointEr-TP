package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.dado.DadoDe20
import ar.edu.unq.epers.tactics.modelo.habilidades.*
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
abstract class HabilidadDTO() {
    companion object {

        fun desdeModelo(habilidad: Habilidad): HabilidadDTO {
            return when (habilidad) {
                is Ataque -> AtaqueDTO(
                        "ATAQUE_FISICO",
                        habilidad.dañoFisico,
                        habilidad.precisionFisica,
                        AventureroDTO.desdeModelo(habilidad.aventureroReceptor)
                )
                is Defensa -> DefensaDTO(
                        "DEFENDER",
                        AventureroDTO.desdeModelo(habilidad.aventureroEmisor),
                        AventureroDTO.desdeModelo(habilidad.aventureroReceptor)
                )
                is Curacion -> CurarDTO(
                        "CURAR",
                        habilidad.poderMagicoEmisor,
                        AventureroDTO.desdeModelo(habilidad.aventureroReceptor)
                )
                is AtaqueMagico -> AtaqueMagicoDTO(
                        "ATAQUE_MAGICO",
                        habilidad.poderMagicoEmisor,
                        habilidad.nivelEmisor,
                        AventureroDTO.desdeModelo(habilidad.aventureroReceptor)
                )
                is Meditacion -> {
                    return MeditarDTO("MEDITAR",
                            AventureroDTO.desdeModelo(habilidad.aventureroReceptor)
                    )
                }
                else -> {
                    HabilidadNulaDTO("NADA",
                        AventureroDTO.desdeModelo(habilidad.aventureroReceptor)
                    )
                }
            }
        }
    }

    abstract fun aModelo(): Habilidad

}
class HabilidadNulaDTO(var tipo: String, val objetivo: AventureroDTO) : HabilidadDTO(){
    override fun aModelo() = HabilidadNula(objetivo.aModelo())
}

data class AtaqueDTO(val tipo: String, val daño: Double, val precisionFisica: Double, val objetivo: AventureroDTO) : HabilidadDTO() {
    override fun aModelo() = Ataque(daño, precisionFisica, objetivo.aModelo(), DadoDe20())
}

class DefensaDTO(val tipo: String, val source: AventureroDTO, val objetivo: AventureroDTO) : HabilidadDTO() {
    override fun aModelo() = Defensa(source.aModelo(), objetivo.aModelo())
}

data class CurarDTO(val tipo: String, val poderMagico: Double, val objetivo: AventureroDTO) : HabilidadDTO() {
    override fun aModelo() = Curacion(poderMagico, objetivo.aModelo())
}

data class AtaqueMagicoDTO(val tipo: String, val poderMagico: Double, val sourceLevel: Int, val objetivo: AventureroDTO) : HabilidadDTO() {
    override fun aModelo() = AtaqueMagico(poderMagico, sourceLevel, objetivo.aModelo(), DadoDe20())
}

class MeditarDTO(val tipo: String, val objetivo: AventureroDTO) : HabilidadDTO() {
    override fun aModelo(): Habilidad {
        val objetivo = objetivo.aModelo()
        return Meditacion(objetivo, objetivo)
    }
}