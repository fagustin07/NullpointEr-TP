package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransactionRunner
import org.neo4j.driver.*

class Neo4JClaseDAO : ClaseDAO {

    override fun crear(clase: Clase): Clase {
        Neo4JTransactionRunner().runTrx { session ->
            session.writeTransaction {
                val query = "MERGE (n:Clase {nombre: ${'$'}nombre})"
                it.run(
                    query, Values.parameters(
                        "nombre", clase.nombre()
                    )
                )
            }
        }

        return clase
    }

    override fun actualizar(entity: Clase): Clase {
        TODO("Not yet implemented")
    }

    override fun recuperar(id: Long): Clase {
        TODO("Not yet implemented")
    }

    /*override fun recuperarTodas(): List<Clase> {
        return Neo4JTransactionRunner().runTrx{ session ->
            val result = session.run("MATCH (n:Clase) RETURN n")
            result.list{ record ->
                Clase(record[0]["nombre"].asString())
            }
        }
    }*/

    override fun requerir(nombreClaseHabilitada: String, nombreClaseRequerida: String) {
        Neo4JTransactionRunner().runTrx { session ->
            val query = """
               MATCH (claseHabilitada:Clase {nombre: ${'$'}nombreClaseHabilitada}) 
               MATCH (claseRequerida:Clase {nombre: ${'$'}nombreClaseRequerida}) 
               MERGE (claseHabilitada)-[:requiere]->(claseRequerida)
            """
            session.run(
                query,
                Values.parameters(
                    "nombreClaseHabilitada", nombreClaseHabilitada,
                    "nombreClaseRequerida", nombreClaseRequerida
                )
            )
        }
    }

    override fun requeridasDe(clase: Clase): MutableList<Clase> {
        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
               MATCH (clase:Clase {nombre: ${'$'}nombreClase})  
               MATCH (clase)-[:requiere]->(requisito) 
               RETURN requisito.nombre
            """
            val result = session.run(query, Values.parameters("nombreClase", clase.nombre()))
            result.list {
                Clase(it[0].asString())
            }
        }
    }

    override fun requiereEnAlgunNivelDe(claseSucesora: Clase, claseAntecesora: Clase): Boolean {
        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
                MATCH (clase:Clase {nombre: ${'$'}nombreDeClaseAntecesora})  
                MATCH (clase)-[:requiere *1..]->(requisito) 
                RETURN ${'$'}nombreDeClaseSucesora IN collect(requisito.nombre)
            """
            val result = session.run(
                query,
                Values.parameters(
                    "nombreDeClaseSucesora",
                    claseSucesora.nombre(),
                    "nombreDeClaseAntecesora",
                    claseAntecesora.nombre()
                )
            )
            result.single()[0].asBoolean()
        }
    }

    fun clear() {
        Neo4JTransactionRunner().runTrx { session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }
}