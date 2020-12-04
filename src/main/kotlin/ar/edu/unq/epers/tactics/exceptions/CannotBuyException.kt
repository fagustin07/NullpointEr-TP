package ar.edu.unq.epers.tactics.exceptions

import java.lang.RuntimeException

class CannotBuyException(nombreItem: String, priceDifference : Int) :
    RuntimeException("No puedes comprar '${nombreItem}', te faltan ${priceDifference} monedas.")