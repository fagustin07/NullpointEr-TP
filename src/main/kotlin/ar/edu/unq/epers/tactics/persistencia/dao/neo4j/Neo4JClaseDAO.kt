package ar.edu.unq.epers.tactics.persistencia.dao.neo4j

import ar.edu.unq.epers.tactics.modelo.Clase
import ar.edu.unq.epers.tactics.persistencia.dao.ClaseDAO
import org.neo4j.driver.*

class Neo4JClaseDAO: ClaseDAO {

    private val driver: Driver

    init {
        val env = System.getenv()
        val url = env.getOrDefault("NEO_URL", "bolt://localhost:7687")
        val username = env.getOrDefault("NEO_USER", "neo4j")
        val password = env.getOrDefault("NEO_PASSWORD", "root")

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password),
            Config.builder().withLogging(Logging.slf4j()).build()
        )
    }

    override fun crear(clase: Clase): Clase {
        driver.session().use { session ->

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
        return driver.session().use { session ->
            val result = session.run("MATCH (n:Clase) RETURN n")
            result.list{ record ->
                Clase(record[0]["nombre"].asString())
            }
        }
    }

    fun clear() {
        return driver.session().use { session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }
}