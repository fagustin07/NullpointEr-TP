package ar.edu.unq.epers.tactics.service

import ar.edu.unq.epers.tactics.modelo.Aventurero
import ar.edu.unq.epers.tactics.modelo.Party

interface AventureroLeaderboardService {
    fun mejorGuerrero():Aventurero
    fun mejorMago():Aventurero
    fun mejorCurandero():Aventurero
    fun buda():Aventurero
}