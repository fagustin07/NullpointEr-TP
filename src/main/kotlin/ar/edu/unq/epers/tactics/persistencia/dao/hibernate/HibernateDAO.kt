package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.modelo.Party
import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.hibernate.query.Query


open class HibernateDAO<T>(val entityType: Class<T>) {

    open fun crear(entity: T): T {
        val session = HibernateTransactionRunner.currentSession
        session.save(entity)
        return entity
    }

    open fun actualizar(entity: T): T {
        val session = HibernateTransactionRunner.currentSession
        session.update(entity)
        return entity
    }

    open fun recuperar(id: Long): T {
        return createQuery("from ${entityType.name} where id = :id").setParameter("id", id).singleResult
    }

    fun eliminarTodo() {
        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }

    protected fun queryMany(hql: String) =
        createQuery(hql).resultList

    protected fun createQuery(hql: String) =
        HibernateTransactionRunner
            .currentSession
            .createQuery(hql, entityType)

}
