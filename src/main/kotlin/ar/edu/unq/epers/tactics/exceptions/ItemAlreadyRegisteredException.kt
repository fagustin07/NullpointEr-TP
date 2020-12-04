package ar.edu.unq.epers.tactics.exceptions

import java.lang.RuntimeException

class ItemAlreadyRegisteredException(nombreItem: String) :
    RuntimeException("El item ${nombreItem} ya se encuentra en el sistema.")