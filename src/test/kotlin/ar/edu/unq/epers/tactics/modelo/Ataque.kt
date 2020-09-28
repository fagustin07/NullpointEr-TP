package ar.edu.unq.epers.tactics.modelo

class Ataque(val danioFisico: Int, val precisionFisica: Int, val aventureroReceptor: Aventurero, val dadoDe20: DadoDe20) : Habilidad() {
    override fun resolverParaReceptor(aventureroReceptor: Aventurero) {
        aventureroReceptor.recibirDañoSiDebe(danioFisico, dadoDe20.tirada() + precisionFisica)
    }

}
