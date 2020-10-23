package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import javax.persistence.NoResultException


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
        return try {
            createQuery("from ${entityType.name} where id = :id")
                .setParameter("id", id)
                .singleResult
        } catch (e: NoResultException) {
            throw RuntimeException("No existe ${entityType.simpleName} con id ${id}")
        }
    }

    fun eliminarTodo() {
        HibernateTransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }

    fun cantidadDeEntidades() =
        HibernateTransactionRunner
            .currentSession
            .createQuery("select count(*) from ${entityType.simpleName}", Long::class.javaObjectType)
            .setMaxResults(1)
            .singleResult

    protected fun queryMany(hql: String) =
        createQuery(hql).resultList

    protected fun createQuery(hql: String) =
        HibernateTransactionRunner
            .currentSession
            .createQuery(hql, entityType)

}
