package ar.edu.unq.epers.tactics.service.runner

import org.neo4j.driver.*

class Neo4JTransactionRunner {

    private lateinit var driver: Driver

    init {
        val env = System.getenv()
        val url = env.getOrDefault("NEO_URL", "bolt://localhost:7687")
        val username = env.getOrDefault("NEO_USER", "neo4j")
        val password = env.getOrDefault("NEO_PASSWORD", "root")

        driver = GraphDatabase.driver(
            url, AuthTokens.basic(username, password),
            Config.builder().withLogging(Logging.slf4j()).build()
        )
    }

    fun <T> runTrx(bloque: (Session)->T): T {
        return driver.session().use { session ->  bloque(session) }
    }

}