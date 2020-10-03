package ar.edu.unq.epers.tactics.modelo.enums

import ar.edu.unq.epers.tactics.service.dto.Criterio
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CriterioTest {

    @Test
    fun IGUAL(){
        assertTrue(Criterio.IGUAL.evaluarseCon(1,1))
        assertFalse(Criterio.IGUAL.evaluarseCon(1,0))
    }

    @Test
    fun MAYOR_QUE(){
        assertTrue(Criterio.MAYOR_QUE.evaluarseCon(2,1))
        assertFalse(Criterio.MAYOR_QUE.evaluarseCon(1,1))
    }

    @Test
    fun MENOR_QUE(){
        assertTrue(Criterio.MENOR_QUE.evaluarseCon(0,1))
        assertFalse(Criterio.MENOR_QUE.evaluarseCon(1,1))
    }

}