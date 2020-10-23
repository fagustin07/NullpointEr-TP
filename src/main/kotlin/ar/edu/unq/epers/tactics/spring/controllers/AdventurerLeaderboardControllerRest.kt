package ar.edu.unq.epers.tactics.spring.controllers

import ar.edu.unq.epers.tactics.service.AventureroLeaderboardService
import ar.edu.unq.epers.tactics.service.dto.AventureroDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@ServiceREST
@RequestMapping("/leaderboard/adventurer")
class AdventurerLeaderboardControllerRest(private val aventureroLeaderboardService: AventureroLeaderboardService) {

    @GetMapping("/mejorGuerrero")
    fun mejorGuerrero() = AventureroDTO.desdeModelo(aventureroLeaderboardService.mejorGuerrero())

    @GetMapping("/mejorMago")
    fun mejorMago() = AventureroDTO.desdeModelo(aventureroLeaderboardService.mejorMago())

    @GetMapping("/mejorCurandero")
    fun mejorCurandero() = AventureroDTO.desdeModelo(aventureroLeaderboardService.mejorCurandero())

    @GetMapping("/buda")
    fun buda() = AventureroDTO.desdeModelo(aventureroLeaderboardService.buda())

}