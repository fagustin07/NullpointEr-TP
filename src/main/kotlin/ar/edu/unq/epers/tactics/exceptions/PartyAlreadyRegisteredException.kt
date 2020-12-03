package ar.edu.unq.epers.tactics.exceptions

import java.lang.RuntimeException

class PartyAlreadyRegisteredException(partyId: Long) : RuntimeException("La party ${partyId} ya está en el sistema.")