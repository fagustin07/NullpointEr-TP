package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransactionRunner
import org.neo4j.driver.*

class Neo4JClaseDAO: ClaseDAO {

    override fun crear(clase: Clase): Clase {
        Neo4JTransactionRunner().runTrx{ session ->
            session.writeTransaction {
                val query = "MERGE (n:Clase {nombre: ${'$'}nombre})"
                it.run(query, Values.parameters(
                    "nombre", clase.nombre()
                ))
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

    override fun recuperarTodas(): List<Clase> {
        return Neo4JTransactionRunner().runTrx{ session ->
            val result = session.run("MATCH (n:Clase) RETURN n")
            result.list{ record ->
                Clase(record[0]["nombre"].asString())
            }
        }
    }

    fun clear() {
        Neo4JTransactionRunner().runTrx{ session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }
}