package org.example

import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.RestController

@RestController

open class HelloController {
    @GetMapping("/")
    fun home(): String {

        return "Сервер работает!"

    }
}