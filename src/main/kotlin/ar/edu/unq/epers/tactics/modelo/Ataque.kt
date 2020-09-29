package ar.edu.unq.epers.tactics.modelo

class Ataque(val dañoFisico: Int, val precisionFisica: Int, val aventureroReceptor: Aventurero, val dadoDe20: DadoDe20) : Habilidad() {

    companion object {
        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dadoDe20: DadoDe20): Ataque {
            return Ataque(aventureroEmisor.danioFisico(), aventureroEmisor.precisionFisica(), aventureroReceptor, dadoDe20)
        }
    }

    override fun resolverse() {
        aventureroReceptor.recibirDañoSiDebe(dañoFisico, dadoDe20.tirada() + precisionFisica)
    }

}
