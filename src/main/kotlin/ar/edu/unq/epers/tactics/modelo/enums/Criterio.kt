package ar.edu.unq.epers.tactics.modelo.enums

enum class Criterio {
    IGUAL { override fun evaluarseCon(valorAComparar: Int, valorDeComparacion: Int) = valorAComparar == valorDeComparacion },
    MAYOR_QUE { override fun evaluarseCon(valorAComparar: Int, valorDeComparacion: Int) = valorAComparar > valorDeComparacion },
    MENOR_QUE { override fun evaluarseCon(valorAComparar: Int, valorDeComparacion: Int) = valorAComparar < valorDeComparacion };

    abstract fun evaluarseCon(valorAComparar: Int, valorDeComparacion: Int): Boolean
}