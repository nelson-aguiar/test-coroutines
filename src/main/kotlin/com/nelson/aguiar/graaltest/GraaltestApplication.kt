package com.nelson.aguiar.graaltest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class GraaltestApplication

fun main(args: Array<String>) {
	runApplication<GraaltestApplication>(*args)
}
