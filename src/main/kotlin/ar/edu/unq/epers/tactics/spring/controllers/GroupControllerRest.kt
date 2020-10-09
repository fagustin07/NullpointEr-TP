package ar.edu.unq.epers.tactics.spring.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@ServiceREST
@RequestMapping("/group")
class GroupControllerRest(private val groupName : String) {
    @GetMapping
    fun getGroupId() = GroupResponse(groupName)
}

data class GroupResponse(val name:String)