package ar.edu.unq.epers.tactics.spring.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@ServiceREST
@RequestMapping("/group")
class GroupControllerREST(private val groupName : String) {
    @GetMapping
    fun getGroupId() = groupName
}