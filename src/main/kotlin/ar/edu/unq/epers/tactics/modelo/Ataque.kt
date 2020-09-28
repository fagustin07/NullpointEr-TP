package ar.edu.unq.epers.tactics.modelo

class Ataque(val danioFisico: Int, val precisionFisica: Int, val aventureroReceptor: Aventurero, val dadoDe20: DadoDe20) : Habilidad() {

    companion object {

        fun para(aventureroEmisor: Aventurero, aventureroReceptor: Aventurero, dadoDe20: DadoDe20): Ataque {
            return Ataque(aventureroEmisor.danio_fisico(), aventureroEmisor.precision_fisica(), aventureroReceptor, dadoDe20)
        }
    }

    override fun resolverParaReceptor(aventureroReceptor: Aventurero) {
        aventureroReceptor.recibirDañoSiDebe(danioFisico, dadoDe20.tirada() + precisionFisica)
    }

}
