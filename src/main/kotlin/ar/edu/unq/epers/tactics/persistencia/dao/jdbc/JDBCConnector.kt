package ar.edu.unq.unidad1.wop.dao.impl

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object JDBCConnector {

    /**
     * Ejecuta un bloque de codigo contra una conexion.
     */
    fun <T> execute(bloque: (Connection)->T): T {
        val connection = openConnection()
        return connection.use(bloque)
    }

    /**
     * Establece una conexion a la url especificada
     *
     * @return la conexion establecida
     */
    private fun openConnection(): Connection {
        val env = System.getenv()
        val user = "root"
        val password = "root"
        val host = "localhost"
        val dataBase = "epers_ejemplo_jdbc"
        val url = env.getOrDefault("SQL_URL", "jdbc:mysql://$host:3306/$dataBase?user=$user&password=$password&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true")

        return try {
            DriverManager.getConnection(url)
        } catch (e: SQLException) {
            throw RuntimeException("No se puede establecer una conexion", e)
        }
    }

    /**
     * Cierra una conexion con la base de datos (libera los recursos utilizados por la misma)
     *
     * @param connection - la conexion a cerrar.
     */
    private fun closeConnection(connection: Connection) {
        try {
            connection.close()
        } catch (e: SQLException) {
            throw RuntimeException("Error al cerrar la conexion", e)
        }
    }
}