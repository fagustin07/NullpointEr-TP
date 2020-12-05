package ar.edu.unq.epers.tactics.exceptions

import java.lang.RuntimeException

class PartyAlreadyRegisteredException(nombreParty: String) : RuntimeException("La party ${nombreParty} ya está en el sistema.")