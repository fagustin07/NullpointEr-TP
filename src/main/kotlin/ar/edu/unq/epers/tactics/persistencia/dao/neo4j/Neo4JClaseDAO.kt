package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransactionRunner
import org.neo4j.driver.*
import java.lang.RuntimeException

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

    override fun crearMejora(nombreClaseInicio: String, nombreClaseAMejorar: String, atributos: List<String>, valorAAumentar: Int): Mejora {
        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
               MATCH (claseInicio:Clase {nombre: ${'$'}nombreClaseInicio}) 
               MATCH (claseAMejorar:Clase {nombre: ${'$'}nombreClaseAMejorar}) 
               MERGE (claseInicio)-[h:habilita {puntos:${'$'}valorAAumentar, atributos:${'$'}atributos}]->(claseAMejorar)
               RETURN claseInicio, claseAMejorar,h
            """
            val result = session.run(
                query,
                Values.parameters(
                    "nombreClaseInicio", nombreClaseInicio,
                    "nombreClaseAMejorar", nombreClaseAMejorar,
                    "valorAAumentar", valorAAumentar,
                    "atributos", atributos
                )
            )
            val record = result.single()
                val claseInicio = record[0]
                val claseAMejorar = record[1]
                val mejora = record[2]
                val nombreClaseInicio: String = claseInicio["nombre"].asString()
                val nombreClaseAMejorar: String = claseAMejorar["nombre"].asString()
                val atributos: List<String> = mejora["atributos"].asList{ it.asString() }
                val puntos: Int = mejora["puntos"].asInt()
                Mejora(nombreClaseInicio,nombreClaseAMejorar,atributos,puntos)
        }
    }

    override fun recuperarTodas(): List<Clase> {
        return Neo4JTransactionRunner().runTrx{ session ->
            val result = session.run("MATCH (c) RETURN c.nombre")
            result.list{ record ->
                Clase(record[0].asString())
            }
        }
    }

    override fun verificarBidireccionalidad(nombreClaseInicio: String, nombreClaseAMejorar: String){
        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
                        MATCH (c:Clase {nombre: ${'$'}nombreClase }) 
                        MATCH (habilitante)-[:habilita]->(habilitado)
                        RETURN habilitado
                    """
            val result = session.run(
                    query,Values.parameters(
                    "nombreClase",nombreClaseAMejorar
                )
            )
            result.list { record ->
                val clase = record[0]
                val nombreClaseHabilitadaPorClaseAMejorar: String = clase["nombre"].asString()
                if(nombreClaseHabilitadaPorClaseAMejorar == nombreClaseInicio){
                    throw RuntimeException("La mejora que estas queriendo crear no es posible")
                }
            }
        }
    }

    fun clear() {
        Neo4JTransactionRunner().runTrx{ session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }
}