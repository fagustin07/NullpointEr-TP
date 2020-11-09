package ar.edu.unq.epers.tactics.persistencia.dao.mongodb

import ar.edu.unq.epers.tactics.modelo.Formacion
import ar.edu.unq.epers.tactics.persistencia.dao.FormacionDAO

class MongoFormacionDAO : MongoDAO<Formacion>(Formacion::class.java), FormacionDAO {

}