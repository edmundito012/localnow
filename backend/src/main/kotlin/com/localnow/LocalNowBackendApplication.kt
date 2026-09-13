package com.localnow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LocalNowBackendApplication

fun main(args: Array<String>) {
	runApplication<LocalNowBackendApplication>(*args)
}
