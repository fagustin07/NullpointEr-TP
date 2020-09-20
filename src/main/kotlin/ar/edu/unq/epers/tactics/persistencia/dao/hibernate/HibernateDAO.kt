package ar.edu.unq.epers.tactics.persistencia.dao.hibernate

import ar.edu.unq.epers.tactics.service.runner.HibernateTransactionRunner
import org.hibernate.query.Query


open class HibernateDAO<T>(val entityType: Class<T>) {

    open fun crear(entity: T): T {
        val session = HibernateTransactionRunner.currentSession
        val id = session.save(entity)
        return entity
    }

    open fun actualizar(entity: T) : T {
        val session = HibernateTransactionRunner.currentSession
        session.update(entity)
        return entity
    }

    open fun recuperar(id: Long): T {
        val session = HibernateTransactionRunner.currentSession
        val recoveryEntity = session.get(entityType, id)
        if(recoveryEntity == null){
            throw Exception("No existe una entidad con ese id")
            return recoveryEntity
        } else { return recoveryEntity }
    }


    protected fun queryMany(hql: String) =
        createQuery(hql).resultList

    protected fun queryOne(hql: String) : T {
        val query = createQuery(hql)
        query.maxResults = 1
        return query.singleResult
    }

    protected fun queryManyWithParameter(hql: String, parameterName: String, parameterValue: Any) =
        queryManyWithParameters(hql, mapOf(parameterName to parameterValue))

    protected fun queryManyWithParameters(hql: String, parameters: Map<String, Any>) : Collection<T> =
        createQueryWithParameters(hql, parameters).resultList


    protected fun createQuery(hql: String) =
        HibernateTransactionRunner
            .currentSession
            .createQuery(hql, entityType)

    protected fun createQueryWithParameter(hql: String, parameterName: String, parameterValue: Any) =
        createQueryWithParameters(hql, mapOf(parameterName to parameterValue))

    protected fun createQueryWithParameters(hql: String, parameters: Map<String, Any>) : Query<T> {
        val query = createQuery(hql)
        parameters.forEach {parameterName, parameterValue -> query.setParameter(parameterName, parameterValue) }
        return query
    }

}
