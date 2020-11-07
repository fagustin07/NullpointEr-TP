package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Atributo
import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.modelo.Mejora
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import ar.edu.unq.epers.tactics.service.runner.Neo4JTransactionRunner
import org.neo4j.driver.*
import java.lang.Integer.max
import java.lang.RuntimeException
import java.util.stream.Collectors

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

    override fun crearMejora(nombreClaseInicio: String, nombreClaseAMejorar: String, atributos: List<Atributo>, valorAAumentar: Int): Mejora {
        verificarQueLaClaseDeInicioNoSeaHabilitadaPorClaseAMejorar(nombreClaseInicio, nombreClaseAMejorar)

        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
               MATCH (claseInicio:Clase {nombre: ${'$'}nombreClaseInicio}) 
               MATCH (claseAMejorar:Clase {nombre: ${'$'}nombreClaseAMejorar}) 
               MERGE (claseInicio)-[h:habilita {puntos:${'$'}valorAAumentar, atributos:${'$'}atributos}]->(claseAMejorar)
               RETURN claseInicio, claseAMejorar,h
            """
            session.run(
                query,
                Values.parameters(
                    "nombreClaseInicio", nombreClaseInicio,
                    "nombreClaseAMejorar", nombreClaseAMejorar,
                    "valorAAumentar", valorAAumentar,
                    "atributos", atributos.map { it.toString() }
                )
            )

            Mejora(nombreClaseInicio,nombreClaseAMejorar,atributos,valorAAumentar)
        }
    }


    override fun requerir(nombreClaseHabilitada: String, nombreClaseRequerida: String) {
        verificarQueNoExistaEnNingunNivelRequerimiento(nombreClaseHabilitada, nombreClaseRequerida)

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

    override fun puedeMejorarse(aventurero: Aventurero, mejora: Mejora): Boolean {
        verificarQueExistaMejora(mejora)

        return aventurero.tieneExperiencia() && Neo4JTransactionRunner().runTrx { session ->
            val query = """
                MATCH (habilitante:Clase { nombre: ${'$'}nombreDeLaClaseInicio })
                MATCH (mejora:Clase { nombre: ${'$'}nombreDeLaClaseAMejorar })

                MATCH path_habilita = (habilitante)-[:habilita { atributos: ${'$'}atributos, puntos: ${'$'}puntos }]->(mejora)
                WHERE habilitante.nombre IN ${'$'}clasesQueSeTiene
                AND NOT EXISTS {
                    MATCH (mejora)-[:requiere]->(requerido)
                    WHERE NOT (requerido.nombre IN ${'$'}clasesQueSeTiene)
                }
                
                RETURN count(path_habilita) > 0                
            """
            session.run(
                    query,
                    Values.parameters(
                            "clasesQueSeTiene", aventurero.clases(),
                            "nombreDeLaClaseInicio", mejora.nombreDeLaClaseInicio(),
                            "nombreDeLaClaseAMejorar", mejora.nombreDeLaClaseAMejorar(),
                            "atributos", mejora.atributos().map { it.toString() },
                            "puntos", mejora.puntosAMejorar()
                    )
            ).single()[0].asBoolean()
        }
    }

    override fun buscarMejora(nombreDeLaClaseInicio: String, nombreDeLaClaseAMejorar: String): Mejora {
        return Neo4JTransactionRunner().runTrx {session ->
            val query = """
               MATCH (claseInicio:Clase {nombre: ${'$'}nombreClaseInicio}) 
               MATCH (claseAMejorar:Clase {nombre: ${'$'}nombreClaseMejorada}) 
               MATCH (claseInicio)-[mejora:habilita]->(claseAMejorar)
               RETURN mejora
            """
            val result = session
                .run(query,
                    Values.parameters(
                        "nombreClaseInicio", nombreDeLaClaseInicio,
                        "nombreClaseMejorada", nombreDeLaClaseAMejorar
                    ))
            val record = result.single()
            val mejora = record[0]
            val atributos: List<Atributo> = mejora["atributos"].asList{ it.asString() }.map { Atributo.desdeString(it) }
            val puntos: Int = mejora["puntos"].asInt()

            Mejora(nombreDeLaClaseInicio,nombreDeLaClaseAMejorar, atributos,puntos)
        }
    }

    override fun caminoMasRentable(puntosDeExperiencia: Int, clasesDePartida: Set<String>, atributoDeseado: Atributo): List<Mejora> {
        return Neo4JTransactionRunner().runTrx {session ->
            val query = """
                UNWIND ${'$'}nombresClasesDePartida AS nombreDeClaseDePartida
                
                MATCH (claseInicio:Clase { nombre: nombreDeClaseDePartida })
                
                MATCH (from:Clase { nombre: nombreDeClaseDePartida })

                MATCH (from)-[mejoras:habilita*$puntosDeExperiencia]->(to:Clase)
                WHERE ${'$'}atributoDeseado IN last(mejoras).atributos
                
                CALL {
                    WITH mejoras
                    RETURN mejoras AS mejorasFiltradas
                    ORDER BY reduce(acc = 0, x in [each IN mejoras WHERE ${'$'}atributoDeseado IN each.atributos| each.puntos] | acc + x) DESC
                    LIMIT 1
                } 
                
                UNWIND mejorasFiltradas AS m
                RETURN [
                    startNode(m).nombre,
                    endNode(m).nombre,
                    m.atributos,
                    m.puntos
                ]
                """

            session
                .run(
                    query,
                    Values.parameters(
                        "longitudMaximaDeCamino", puntosDeExperiencia,
                        "nombresClasesDePartida", clasesDePartida,
                        "atributoDeseado", atributoDeseado.toString()
                    )
                )
                .list()
                .stream()
                .map{

                    Mejora(
                        it[0][0].asString(),
                        it[0][1].asString(),
                        it[0][2].asList { Atributo.desdeString(it.asString()) },
                        it[0][3].asInt())
                }
                .collect(Collectors.toList())
        }
    }

    override fun posiblesMejorasPara(aventurero: Aventurero): Set<Mejora> {
        if (!aventurero.tieneExperiencia()) return emptySet()

        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
                MATCH (habilitante)-[h:habilita]->(mejora)
                WHERE habilitante.nombre IN ${'$'}nombresDeClasesQueSeTiene
                AND NOT (mejora.nombre IN ${'$'}nombresDeClasesQueSeTiene)
                AND NOT EXISTS {
                    MATCH (mejora)-[:requiere]->(requerido)
                    WHERE NOT (requerido.nombre IN ${'$'}nombresDeClasesQueSeTiene)
                }
                RETURN habilitante.nombre, mejora.nombre, h.atributos, h.puntos
                """
            session
                .run(query, Values.parameters("nombresDeClasesQueSeTiene", aventurero.clases()))
                .stream()
                .map {
                    Mejora(
                        it[0].asString(),
                        it[1].asString(),
                        it[2].asList { it.asString() }.map { Atributo.desdeString(it) },
                        it[3].asInt()
                    )
                }
                .collect(Collectors.toSet())
        }
    }

    override fun requeridasDe(claseSucesora: Clase): MutableList<Clase> {
        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
               MATCH (clase:Clase {nombre: ${'$'}nombreClase})  
               MATCH (clase)-[:requiere]->(requisito) 
               RETURN requisito.nombre
            """
            val result = session.run(query, Values.parameters("nombreClase", claseSucesora.nombre()))
            result.list {
                Clase(it[0].asString())
            }
        }
    }

    fun clear() {
        Neo4JTransactionRunner().runTrx { session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }
    /** PRIVATE **/
    /* Testing */
    private fun existeMejora(mejora: Mejora) =
            Neo4JTransactionRunner().runTrx { session ->
                val query = """
                MATCH (from:Clase { nombre: ${'$'}nombreDeLaClaseInicio })
                MATCH (to:Clase { nombre: ${'$'}nombreDeLaClaseAMejorar })
                MATCH path = (from)-[:habilita { atributos: ${'$'}atributos, puntos: ${'$'}puntos }]->(to)
                RETURN count(path) > 0
                """
                //
                session.run(
                        query,
                        Values.parameters(
                                "nombreDeLaClaseInicio", mejora.nombreDeLaClaseInicio(),
                                "nombreDeLaClaseAMejorar", mejora.nombreDeLaClaseAMejorar(),
                                "atributos", mejora.atributos().map { it.toString() },
                                "puntos", mejora.puntosAMejorar()
                        )
                ).single()[0].asBoolean()
            }

    private fun requiereEnAlgunNivelDe(claseSucesora: String, claseAntecesora: String): Boolean {
        return Neo4JTransactionRunner().runTrx { session ->
            val query = """
                MATCH (clase:Clase {nombre: ${'$'}nombreDeClaseAntecesora})  
                MATCH (clase)-[:requiere *1..]->(requisito) 
                RETURN ${'$'}nombreDeClaseSucesora IN collect(requisito.nombre)
            """
            val result = session.run(
                query,
                Values.parameters(
                    "nombreDeClaseSucesora", claseSucesora,
                    "nombreDeClaseAntecesora", claseAntecesora
                )
            )
            result.single()[0].asBoolean()
        }
    }

    /* Assertions */
    private fun verificarQueExistaMejora(mejora: Mejora) {
        if (!existeMejora(mejora)) throw RuntimeException("No existe la mejora")
    }

    private fun verificarQueNoExistaEnNingunNivelRequerimiento(clasePredecesora: String, claseSucesora: String) {
        if (requiereEnAlgunNivelDe(clasePredecesora, claseSucesora)) {
            throw RuntimeException("No se puede establecer una relacion bidireccional entre ${clasePredecesora} y ${claseSucesora}")
        }
    }

    private fun verificarQueLaClaseDeInicioNoSeaHabilitadaPorClaseAMejorar(nombreClaseInicio: String, nombreClaseAMejorar: String){ // TODO: revisar
        val query = """
                MATCH (from:Clase {nombre: ${'$'}fromName })
                MATCH (to:Clase {nombre: ${'$'}toName })
                MATCH path = (to)-[:habilita]->(from)
                RETURN count(path) > 0 AS habilitante_es_habilitada_por_mejora
            """
        return Neo4JTransactionRunner().runTrx { session ->
            val resultado = session
                .run(
                    query,
                    Values.parameters(
                        "fromName", nombreClaseInicio,
                        "toName", nombreClaseAMejorar
                    )
                )
                .single()[0].asBoolean()

            if(resultado){
                throw RuntimeException("La mejora que estas queriendo crear no es posible")
            }
        }
    }

}