package ar.edu.unq.epers.tactics.exceptions

import java.lang.RuntimeException

class PartyUnregisteredException(partyId: Long) :
    RuntimeException("La party con id ${partyId} no se encuentra en el sistema.") {

}
