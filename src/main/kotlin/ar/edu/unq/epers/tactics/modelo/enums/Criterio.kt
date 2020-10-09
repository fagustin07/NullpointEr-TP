package ar.edu.unq.epers.tactics.modelo.enums

enum class Criterio {
    IGUAL { override fun evaluarseCon(valorAComparar: Double, valorDeComparacion: Double) = valorAComparar == valorDeComparacion },
    MAYOR_QUE { override fun evaluarseCon(valorAComparar: Double, valorDeComparacion: Double) = valorAComparar > valorDeComparacion },
    MENOR_QUE { override fun evaluarseCon(valorAComparar: Double, valorDeComparacion: Double) = valorAComparar < valorDeComparacion };

    abstract fun evaluarseCon(valorAComparar: Double, valorDeComparacion: Double): Boolean
}