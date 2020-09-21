package ar.edu.unq.epers.tactics.service.runner

import ar.edu.unq.unidad3.service.runner.HibernateSessionFactoryProvider
import org.hibernate.Session
import javax.persistence.EntityTransaction


object HibernateTransactionRunner {
    private var sessionThreadLocal: ThreadLocal<Session?> = ThreadLocal()

    val currentSession: Session
        get() {
            if (sessionThreadLocal.get() == null) {
                throw RuntimeException("No hay ninguna session en el contexto")
            }
            return sessionThreadLocal.get()!!
        }


    fun <T> runTrx(bloque: ()->T): T {
        val session = HibernateSessionFactoryProvider.instance.createSession()
        sessionThreadLocal.set(session)
        session.use {
            val tx =  session!!.beginTransaction()
            try {
                //codigo de negocio
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
