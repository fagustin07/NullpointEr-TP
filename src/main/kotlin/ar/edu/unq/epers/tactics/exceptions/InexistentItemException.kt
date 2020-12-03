package ar.edu.unq.epers.tactics.exceptions

class InexistentItemException(nombreItem: String) : RuntimeException("No existe el item llamado ${nombreItem}.")
