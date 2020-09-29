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
    private var  turnosDefendidos = 0
    private var defensor : Aventurero? = null
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
    fun vida() = vida
    fun mana() = mana
    fun fuerza() = fuerza
    fun destreza() = destreza
    fun constitucion() = constitucion
    fun inteligencia() = inteligencia

    //Estadisticas
    fun armadura() = nivel() + constitucion
    fun velocidad() = nivel() + destreza
    fun dañoFisico() = nivel() + fuerza + (destreza / 2)
    fun poderMagico() = mana
    fun precisionFisica() = nivel() + fuerza + destreza


    private fun recibirDaño(dañoRecibido: Int) {
        if (this.tieneDefensor()){
            enviarDañoADefensor(dañoRecibido)
        } else {
            val vidaSiSufreAtaque = vida - dañoRecibido
            if (vidaSiSufreAtaque>0) this.vida = vida - dañoRecibido
        }
    }

    private fun enviarDañoADefensor(dañoADefensor: Int) {
        if(defensor!!.estaVivo()){
            defensor!!.recibirDaño(dañoADefensor/2)
            this.consumirTurnoDeDefensa()
        }else{
            defensor = null
            this.recibirDaño(dañoADefensor)
        }
    }

    private fun estaVivo()= this.vida > 0

    fun recibirDañoSiDebe(dañoFisico: Int, precisionFisica: Int) {
        val claseDeArmadura = this.armadura() + (this.velocidad() / 2)

        if(precisionFisica >= claseDeArmadura) this.recibirDaño(dañoFisico)
    }

    fun esDefendidoPor(defensor : Aventurero) {
        this.defensor = defensor
        this.turnosDefendidos = 3
    }

    private fun consumirTurnoDeDefensa() {
        turnosDefendidos -= 1

        if(turnosDefendidos==0){
            defensor = null
        }
    }

    private fun tieneDefensor() = this.defensor != null

//    fun atacar(receptor: Aventurero) = Habilidad(this).atacar(receptor)
//    fun defender(receptor: Aventurero) = state.defender(receptor)
//    fun curar(receptor: Aventurero) = Habilidad(this).curar(receptor)
//    fun atacar_con_magia(receptor: Aventurero) = Habilidad(this).atacar_con_magia_ofensiva(receptor)
//    fun meditar(receptor: Aventurero) = Habilidad(this).meditar(receptor)

}
