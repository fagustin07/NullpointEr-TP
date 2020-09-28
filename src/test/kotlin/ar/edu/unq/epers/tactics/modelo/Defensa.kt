package ar.edu.unq.epers.tactics.modelo

class Defensa(val aventureroEmisor: Aventurero, val aventureroReceptor: Aventurero): Habilidad() {
    override fun resolverParaReceptor(aventureroReceptor: Aventurero) {
        aventureroEmisor.entrarEnDefensaDurante(1)
        aventureroReceptor.entrarEnDefensaDurante(3)
    }

}
