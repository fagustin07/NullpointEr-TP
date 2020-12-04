package ar.edu.unq.epers.tactics.service.runner

import com.orientechnologies.orient.core.db.ODatabaseSession

object OrientDBTransactionRunner {
    private var sessionThreadLocal: ThreadLocal<ODatabaseSession?> = ThreadLocal()


    val currentSession: ODatabaseSession
        get() = sessionThreadLocal.get() ?: throw RuntimeException("No hay ninguna session en el contexto")


    fun <T> runTrx(bloque: ()->T): T {
        val session = OrientDBSessionFactoryProvider.instance.createSession()

        sessionThreadLocal.set(session)

        session.use {
            val tx =  session.begin()
            try {
                val resultado = bloque()
                tx!!.commit()
                return resultado
            } catch (e: RuntimeException) {
                tx.rollback()
                throw e
            }finally {
                sessionThreadLocal.set(null)
            }
        }
    }
}