package ar.edu.unq.epers.tactics.modelo

import javax.persistence.*


@Entity(name = "Aventurero")
class Aventurero(
    @ManyToOne
    val party: Party,
    var nombre: String,
    private var fuerza:Int = 0,
    private var destreza: Int = 0,
    private var inteligencia:Int = 0,
    private var constitucion:Int = 0
    )
{
    @Transient private var  turnosDefendidos = 0
    @Transient private var defensor : Aventurero? = null
    private var vida: Int
    private var mana: Int

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    init{
       vida = ((nivel() * 5) + (constitucion * 2) + fuerza)
       mana =  nivel() + inteligencia
    }


    fun nivel() = 1

    fun fuerza() = fuerza
    fun destreza() = destreza
    fun constitucion() = constitucion
    fun inteligencia() = inteligencia

    //Estadisticas
    fun vida() = vida
    fun mana() = mana
    fun armadura() = nivel() + constitucion
    fun velocidad() = nivel() + destreza
    fun dañoFisico() = nivel() + fuerza + (destreza / 2)
    fun poderMagico() = mana
    fun precisionFisica() = nivel() + fuerza + destreza


    fun recibirAtaqueFisicoSiDebe(dañoFisico: Int, precisionFisica: Int) {
        val claseDeArmadura = this.armadura() + (this.velocidad() / 2)

        if(precisionFisica >= claseDeArmadura) this.recibirDaño(dañoFisico)
    }


    fun defendidoPor(defensor : Aventurero) {
        this.defensor = defensor
        this.turnosDefendidos = 3
    }

    private fun recibirDaño(dañoRecibido: Int) {
        if (this.tieneDefensor()){
            defensor!!.recibirDaño(dañoRecibido/2)
            this.consumirTurnoDeDefensa()
        } else {
            this.vida = 0.coerceAtLeast(vida - dañoRecibido)
        }
    }

    private fun consumirTurnoDeDefensa() {
        turnosDefendidos -= 1

        if(turnosDefendidos==0){
            defensor = null
        }
    }

    private fun tieneDefensor() = this.defensor != null && this.defensor!!.estaVivo()

    private fun estaVivo()= this.vida > 0

    fun curar(vidaACurar: Int) {
        this.vida += vidaACurar
    }

    fun restarMana(manaARestar: Int) {
        this.mana -= manaARestar
    }

    fun recibirAtaqueMagicoSiDebe(tirada: Int, daño: Int) {
        if(tirada >= this.velocidad() / 2){
            this.recibirDaño(daño)
        }

    }

    fun meditar() {
        this.mana += this.nivel()
    }

//    fun atacar(receptor: Aventurero) = Habilidad(this).atacar(receptor)
//    fun defender(receptor: Aventurero) = state.defender(receptor)
//    fun curar(receptor: Aventurero) = Habilidad(this).curar(receptor)
//    fun atacar_con_magia(receptor: Aventurero) = Habilidad(this).atacar_con_magia_ofensiva(receptor)
//    fun meditar(receptor: Aventurero) = Habilidad(this).meditar(receptor)

}
