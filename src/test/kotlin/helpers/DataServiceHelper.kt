package helpers

import ar.edu.unq.unidad1.wop.dao.impl.JDBCConnector.execute

class DataServiceHelper : DataService {
    override fun crearSetDeDatosIniciales() {
        TODO("Not yet implemented")
    }

    override fun eliminarTodo() {
        val sqlQuery = "TRUNCATE TABLE party"
        execute { conn->
            val stmt = conn.prepareStatement(sqlQuery)
            stmt.executeUpdate()
            stmt.close()
        }
    }
}