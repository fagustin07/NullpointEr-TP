package ar.edu.unq.epers.tactics.modelo.enums

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CriterioTest {

    @Test
    fun IGUAL(){
        assertTrue(Criterio.IGUAL.evaluarseCon(1.0,1.0))
        assertFalse(Criterio.IGUAL.evaluarseCon(1.0,0.0))
    }

    @Test
    fun MAYOR_QUE(){
        assertTrue(Criterio.MAYOR_QUE.evaluarseCon(2.0,1.0))
        assertFalse(Criterio.MAYOR_QUE.evaluarseCon(1.0,1.0))
    }

    @Test
    fun MENOR_QUE(){
        assertTrue(Criterio.MENOR_QUE.evaluarseCon(0.0,1.0))
        assertFalse(Criterio.MENOR_QUE.evaluarseCon(1.0,1.0))
    }

}