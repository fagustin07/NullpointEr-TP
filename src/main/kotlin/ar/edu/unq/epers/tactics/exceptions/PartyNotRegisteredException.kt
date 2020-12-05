package ar.edu.unq.epers.tactics.exceptions

import java.lang.RuntimeException

class PartyNotRegisteredException(nombreParty: String) :
    RuntimeException("No exite una party llamada ${nombreParty} en el sistema.") {

}
